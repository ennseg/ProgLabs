package client;

import client.standartConsole.Console;
import server.CommandRequest;
import server.CommandResponse;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NetworkUtil {
    private SocketChannel channel;
    private final Console console;
    private ByteArrayOutputStream responseBuffer;

    public NetworkUtil(SocketChannel channel, Console console) {
        this.channel = channel;
        this.console = console;
        this.responseBuffer = new ByteArrayOutputStream();
    }

    public void sendRequest(CommandRequest request) throws IOException, InterruptedException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(request);
        oos.flush();

        byte[] data = baos.toByteArray();
        ByteBuffer buffer = ByteBuffer.wrap(data);
        while (buffer.hasRemaining()) {
            int bytesWritten = channel.write(buffer);
            if (bytesWritten == 0) {
                Thread.sleep(10);
            }
        }
    }

    public CommandResponse receiveResponse() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4096);
        int bytesRead = channel.read(buffer);
        if (bytesRead > 0) {
            buffer.flip();
            responseBuffer.write(buffer.array(), 0, bytesRead);

            byte[] data = responseBuffer.toByteArray();
            try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
                 ObjectInputStream ois = new ObjectInputStream(bais)) {
                Object obj = ois.readObject();
                if (obj instanceof CommandResponse) {
                    responseBuffer.reset();
                    return (CommandResponse) obj;
                } else {
                    console.printError("Получен объект неверного типа: " + obj.getClass().getName());
                    return null;
                }
            } catch (IOException | ClassNotFoundException e) {
                console.println("Недостаточно данных или ошибка десериализации: " + e.getMessage());
                return null;
            }
        } else if (bytesRead == -1) {
            console.println("Соединение закрыто сервером.");
            throw new IOException("Сервер разорвал соединение");
        }
        return null;
    }

    public void updateChannel(SocketChannel newChannel) {
        this.channel = newChannel;
        this.responseBuffer.reset();
    }
}