package client;

import client.standartConsole.Console;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * The type Client.
 */
public class Client {
    private final String host = "localhost";
    private final int port = 2720;
    private final Console console = new Console();

    /**
     * Start.
     */
    public void start() {
        SocketChannel channel = null;
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(host, port));

            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 3000) {
                if (channel.finishConnect()) {
                    break;
                }
                console.println("Подключение к серверу...");
                Thread.sleep(500);
            }

            if (!channel.isConnected()) {
                throw new IOException("Не удалось подключиться к серверу.");
            }

            Starter starter = new Starter(console, channel);
            starter.interactiveMode();

        } catch (IOException e) {
            console.println("Сервер выключен: " + (e.getMessage() != null ? e.getMessage() : "неизвестная ошибка"));
        } catch (InterruptedException e) {
            console.println("Ошибка при ожидании подключения: " + e.getMessage());
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    console.println("Ошибка при закрытии канала: " + e.getMessage());
                }
            }
        }
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}