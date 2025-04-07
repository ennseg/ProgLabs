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
        try {
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(host, port));

            while (!channel.finishConnect()) {
                console.println("Подключение к серверу...");
                Thread.sleep(500);
            }
            console.println("Подключение к серверу установлено.");

            Starter starter = new Starter(console, channel);
            starter.interactiveMode();

            channel.close();
        } catch (IOException | InterruptedException e) {
            console.println("Сервер выключен");
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