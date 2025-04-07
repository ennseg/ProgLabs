package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import client.standartConsole.Console;
import server.collectionAction.CollectionManager;
import server.collectionAction.DumpManager;
import server.executionOfCommands.ExecutionResponse;
import server.executionOfCommands.Runner;
import server.executionOfCommands.сommands.Save;

/**
 * The type Server.
 */
public class Server {
    private final int port = 2720;
    private final Runner runner;
    private final CollectionManager collectionManager;
    private final Save save;

    /**
     * Instantiates a new Server.
     *
     * @param collectionManager the collection manager
     */
    public Server(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
        this.runner = new Runner(new Console(), collectionManager);
        this.save = new Save(collectionManager);
    }

    /**
     * Start.
     */
    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Сервер завершает работу, выполняется автосохранение...");
            ExecutionResponse response = save.apply(null);
            if (response.getExitCode()) {
                System.out.println(response.getMessage());
            } else {
                System.err.println("Ошибка автосохранения: " + response.getMessage());
            }
        }));

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Клиент подключён: " + clientSocket.getInetAddress());
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Ошибка запуска сервера: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket) {
        try (InputStream input = clientSocket.getInputStream();
             OutputStream output = clientSocket.getOutputStream()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            while (!clientSocket.isClosed()) {
                try {
                    int bytesRead = input.read(buffer);
                    if (bytesRead > 0) {
                        baos.write(buffer, 0, bytesRead);
                        System.out.println("Прочитано " + bytesRead + " байт от клиента.");

                        byte[] data = baos.toByteArray();
                        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                             ObjectInputStream ois = new ObjectInputStream(bais)) {
                            CommandRequest request = (CommandRequest) ois.readObject();
                            System.out.println("Получена команда: " + request.getCommandName() + ", аргумент: " + request.getArgument());
                            CommandResponse response = runner.executeCommand(request);
                            if (response == null) {
                                System.err.println("Ошибка: runner вернул null для команды " + request.getCommandName());
                                response = new CommandResponse("Внутренняя ошибка сервера", false);
                            }
                            System.out.println("Подготовлен ответ: success=" + response.isSuccess() + ", message=" + response.getMessage());

                            ByteArrayOutputStream responseBaos = new ByteArrayOutputStream();
                            ObjectOutputStream oos = new ObjectOutputStream(responseBaos);
                            oos.writeObject(response);
                            oos.flush();
                            byte[] responseData = responseBaos.toByteArray();
                            output.write(responseData);
                            output.flush();
                            System.out.println("Отправлено " + responseData.length + " байт клиенту.");
                            baos.reset();

                            if ("exit".equals(request.getCommandName()) && response.isSuccess()) {
                                System.out.println("Клиент запросил завершение.");
                                clientSocket.close();
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            System.out.println("Недостаточно данных или ошибка десериализации: " + e.getMessage());
                        }
                    } else if (bytesRead == -1) {
                        System.out.println("Клиент отключился.");
                        break;
                    }
                } catch (IOException e) {
                    System.err.println("IOException при обработке запроса: " + e.getMessage());
                    e.printStackTrace();
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка при установке соединения с клиентом: " + e.getMessage());
        } finally {
            try {
                if (!clientSocket.isClosed()) clientSocket.close();
            } catch (IOException e) {
                System.err.println("Ошибка при закрытии сокета: " + e.getMessage());
            }
        }
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Укажите имя файла для коллекции как аргумент командной строки.");
            return;
        }

        String fileName = args[0];
        Console console = new Console();
        DumpManager dumpManager = new DumpManager();
        CollectionManager collectionManager = new CollectionManager(dumpManager, fileName, console);
        Server server = new Server(collectionManager);
        server.start();
    }
}