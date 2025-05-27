package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.logging.*;

import client.standartConsole.Console;
import server.collectionAction.*;
import server.executionOfCommands.*;
import server.executionOfCommands.сommands.Save;

/**
 * The type Server.
 */
public class Server {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    private final int port = 2720;
    private final Runner runner;
    private final CollectionManager collectionManager;
//    private final Save save;
    private final LogManager logManager;
    private final DataBaseConnection dbconnection;
    private final ExecutorService readerPool = Executors.newFixedThreadPool(10);
    private final ExecutorService processorPool = Executors.newCachedThreadPool();
    private final ExecutorService senderPool = Executors.newCachedThreadPool();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    /**
     * Instantiates a new Server.
     *
     * @param collectionManager the collection manager
     * @throws IOException the io exception
     */
    public Server(CollectionManager collectionManager, DataBaseConnection dbconnection) throws IOException {
        this.collectionManager = collectionManager;
        this.runner = new Runner(new Console(), collectionManager, dbconnection);
        this.dbconnection = dbconnection;
//        this.save = new Save(collectionManager);
        this.logManager = new LogManager();

        logManager.setupLogger();
    }

    /**
     * Start.
     */
    public void start() {
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            LOGGER.info("Сервер завершает работу, выполняется автосохранение...");
//            ExecutionResponse response = save.apply(null);
//            if (response.getExitCode()) {
//                System.out.println(response.getMessage());
//            } else {
//                LOGGER.severe("Ошибка автосохранения: " + response.getMessage());
//            }
//        }));

        LOGGER.info("Запуск сервера...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("Сервер запущен на порту " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                LOGGER.info("Клиент подключён: " + clientSocket.getInetAddress());
                readerPool.execute(() -> handleClient(clientSocket));
            }
        } catch (IOException e) {
            LOGGER.severe("Ошибка запуска сервера: " + e.getMessage());
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

                        byte[] data = baos.toByteArray();
                        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                             ObjectInputStream ois = new ObjectInputStream(bais)) {
                            CommandRequest request = (CommandRequest) ois.readObject();
                            LOGGER.info("Получена команда: " + request.getCommandName() + ", аргумент: " + request.getArgument());

                            processorPool.execute(() -> {
                                CommandResponse response = runner.executeCommand(request);
                                if (response == null) {
                                    LOGGER.severe("Ошибка: runner вернул null для команды " + request.getCommandName());
                                    sendResponse(clientSocket, output, new CommandResponse("Внутренняя ошибка сервера", false));
                                } else {
                                    sendResponse(clientSocket, output, response);
                                }
                            });

                            baos.reset();
                        } catch (IOException | ClassNotFoundException e) {
                            LOGGER.warning("Недостаточно данных или ошибка десериализации: " + e.getMessage());
                        }
                    } else if (bytesRead == -1) {
                        LOGGER.info("Клиент отключился.");
                        break;
                    }
                } catch (IOException e) {
                    LOGGER.severe("IOException при обработке запроса: " + e.getMessage());
                    e.printStackTrace();
                    break;
                }
            }
        } catch (IOException e) {
            LOGGER.severe("Ошибка при установке соединения с клиентом: " + e.getMessage());
        } finally {
            try {
                if (!clientSocket.isClosed()) clientSocket.close();
            } catch (IOException e) {
                LOGGER.severe("Ошибка при закрытии сокета: " + e.getMessage());
            }
        }
    }

    private void sendResponse(Socket clientSocket, OutputStream output, CommandResponse response) {
        senderPool.execute(() -> {
            try {
                ByteArrayOutputStream responseBaos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(responseBaos);
                oos.writeObject(response);
                oos.flush();
                byte[] responseData = responseBaos.toByteArray();
                synchronized (output) {
                    output.write(responseData);
                    output.flush();
                    LOGGER.fine("Отправлено " + responseData.length + " байт клиенту.");
                }
//не факт что сработает с response.getMessage() - нужно проверить
                if ("exit".equals(response.getMessage()) && response.isSuccess()) {
                    LOGGER.info("Клиент запросил завершение.");
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        LOGGER.severe("Ошибка при закрытии сокета: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                LOGGER.severe("Ошибка отправки ответа: " + e.getMessage());
            }
        });
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws IOException the io exception
     */
    public static void main(String[] args) throws IOException, SQLException {

        DataBaseConnection dbconnection = new DataBaseConnection();
        CollectionManager collectionManager = new CollectionManager(dbconnection);
        Server server = new Server(collectionManager, dbconnection);
        server.start();
    }
}