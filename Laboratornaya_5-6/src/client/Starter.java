package client;

import server.model.*;
import server.CommandRequest;
import server.CommandResponse;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import client.standartConsole.Console;

/**
 * Класс для интерактивного режима на клиенте
 */
public class Starter {
    private final Console console;
    private SocketChannel channel;
    private final NetworkUtil networkUtil;
    private static final Set<String> executingFiles = new HashSet<>();
    private final ScriptExecutor scriptExecutor;

    /**
     * Instantiates a new Starter.
     *
     * @param console the console
     * @param channel the channel
     * @throws IOException the io exception
     */
    public Starter(Console console, SocketChannel channel) throws IOException {
        this.console = console;
        this.channel = SocketChannel.open(new InetSocketAddress("localhost", 2720));
        this.channel.configureBlocking(false);
        this.networkUtil = new NetworkUtil(channel, console);
        this.scriptExecutor = new ScriptExecutor(console, channel, this, networkUtil);
    }

    /**
     * Запуск интерактивного режима
     */
    public void interactiveMode() {
        try {
            while (true) {
                console.prompt();
                String input = console.readLine().trim();
                if (input.isEmpty()) continue;

                String[] parts = input.split("\\s+", 2);
                String commandName = parts[0].toLowerCase();
                String args = parts.length > 1 ? parts[1] : "";

                if (commandName.equals("save")) {
                    console.printError("У вас нет прав на выполнение команды 'save'.");
                    continue;
                }

                CommandRequest request;
                if (commandName.equals("add")) {
                    Organization org = promptOrganization();
                    request = new CommandRequest(org, commandName);
                } else if (commandName.equals("update")) {
                    if (args.trim().isEmpty()) {
                        console.printError("Команда update требует ID.");
                        continue;
                    }
                    try {
                        int id = Integer.parseInt(args.trim());
                        console.println("Обновление элемента с ID " + id + ". Введите новые данные:");
                        Organization org = promptOrganizationWithId(id);
                        request = new CommandRequest(org, commandName);
                    } catch (NumberFormatException e) {
                        console.printError("ID должен быть целым числом.");
                        continue;
                    }
                } else if (commandName.equals("execute_script")) {
                    scriptExecutor.executeScript(args);
                    return;
                } else {
                    request = new CommandRequest(args, commandName);
                }

                try {
                    if (!channel.isOpen()) {
                        console.println("Сервер недоступен, переподключаемся...");
                        reconnect();
                    }
                    networkUtil.sendRequest(request);

                    CommandResponse response = null;
                    long startTime = System.currentTimeMillis();
                    while (System.currentTimeMillis() - startTime < 5000) {
                        response = networkUtil.receiveResponse();
                        if (response != null) break;
                        Thread.sleep(50);
                    }

                    if (response != null) {
                        if (commandName.equals("exit") && response.isSuccess()) {
                            console.println("Завершение работы клиента.");
                            channel.close();
                            return;
                        }
                        if (!response.getMessage().isEmpty()) {
                            if (response.isSuccess()) {
                                console.println(response.getMessage());
                            } else {
                                console.printError(response.getMessage());
                            }
                        }
                    } else {
                        console.printError("Сервер не ответил вовремя.");
                    }
                } catch (IOException e) {
                    console.printError("Ошибка связи с сервером: " + (e.getMessage() != null ? e.getMessage() : "неизвестная ошибка"));
                    try {
                        channel.close();
                        reconnect();
                    } catch (IOException ex) {
                        console.printError("Не удалось переподключиться: " + ex.getMessage());
                    }
                } catch (InterruptedException e) {
                    console.printError("Прерывание ожидания: " + e.getMessage());
                }
            }
        } catch (NoSuchElementException e) {
            console.printError("Пользовательский ввод не обнаружен!");
        } catch (IllegalStateException e) {
            console.printError("Непредвиденная ошибка!");
        } finally {
            try {
                if (channel.isOpen()) channel.close();
            } catch (IOException e) {
                console.printError("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
    }

    protected void reconnect() throws IOException {
        if (channel.isOpen()) channel.close();
        channel = SocketChannel.open(new InetSocketAddress("localhost", 2720));
        channel.configureBlocking(false);
        networkUtil.updateChannel(channel);
        console.println("Переподключение успешно.");
    }

    private Organization promptOrganization() {
        console.println("Добавление нового элемента в коллекцию. Введите данные:");

        String name = promptString("Введите название: ", false);
        Coordinates coordinates = promptCoordinates();
        Double annualTurnover = promptDouble("Введите годовой оборот (или пустую строку для null): ", true);
        String fullName = promptString("Введите полное название (или пустую строку для null): ", true);
        long employeesCount = promptLong("Введите количество сотрудников: ", false);
        OrganizationType type = promptOrganizationType();
        Address address = promptAddress();

        return new Organization(0, name, coordinates, null, annualTurnover, fullName, employeesCount, type, address);
    }

    /**
     * Prompt string string.
     *
     * @param message  the message
     * @param nullable the nullable
     * @return the string
     */
    public String promptString(String message, boolean nullable) {
        while (true) {
            console.print(message);
            String input = console.readLine().trim();
            if (nullable && input.isEmpty()) return null;
            if (!input.isEmpty()) return input;
            console.printError("Значение не может быть пустым.");
        }
    }

    /**
     * Prompt double double.
     *
     * @param message  the message
     * @param nullable the nullable
     * @return the double
     */
    public Double promptDouble(String message, boolean nullable) {
        while (true) {
            console.print(message);
            String input = console.readLine().trim();
            if (nullable && input.isEmpty()) return null;
            try {
                double value = Double.parseDouble(input);
                if (value > 0) return value;
                console.printError("Значение должно быть больше 0.");
            } catch (NumberFormatException e) {
                console.printError("Введите число.");
            }
        }
    }

    /**
     * Prompt long long.
     *
     * @param message  the message
     * @param nullable the nullable
     * @return the long
     */
    public long promptLong(String message, boolean nullable) {
        while (true) {
            console.print(message);
            String input = console.readLine().trim();
            if (!nullable && input.isEmpty()) {
                console.printError("Значение не может быть пустым.");
                continue;
            }
            try {
                long value = Long.parseLong(input);
                if (value > 0) return value;
                console.printError("Значение должно быть больше 0.");
            } catch (NumberFormatException e) {
                console.printError("Введите целое число.");
            }
        }
    }

    /**
     * Prompt coordinates coordinates.
     *
     * @return the coordinates
     */
    public Coordinates promptCoordinates() {
        console.println("Введите координаты:");
        double x = promptDouble("  x: ", false);
        long y = promptLong("  y (максимум 260): ", false);
        if (y > 260) throw new IllegalArgumentException("y не может быть больше 260.");
        return new Coordinates(x, y);
    }

    /**
     * Prompt organization type organization type.
     *
     * @return the organization type
     */
    public OrganizationType promptOrganizationType() {
        console.println("Выберите тип организации (доступные варианты: PUBLIC, TRUST, PRIVATE_LIMITED_COMPANY):");
        while (true) {
            console.print("Тип: ");
            String input = console.readLine().trim();
            try {
                return OrganizationType.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                console.printError("Неверный тип. Выберите из: PUBLIC, TRUST, PRIVATE_LIMITED_COMPANY.");
            }
        }
    }

    /**
     * Prompt address address.
     *
     * @return the address
     */
    public Address promptAddress() {
        console.println("Введите адрес:");
        String street = promptString("  Улица (или пустая строка для null): ", true);
        String zipCode = promptString("  Почтовый индекс (максимум 22 символа): ", false);
        if (zipCode.length() > 22) throw new IllegalArgumentException("Почтовый индекс не может быть длиннее 22 символов.");
        Location town = promptLocation();
        return new Address(street, zipCode, town);
    }

    /**
     * Prompt location location.
     *
     * @return the location
     */
    public Location promptLocation() {
        console.println("Введите местоположение:");
        console.print("  x: ");
        String xInput = console.readLine().trim();
        if (xInput.isEmpty()) throw new IllegalArgumentException("x не может быть null.");
        Integer x = Integer.parseInt(xInput);
        long y = promptLong("  y: ", false);
        String name = promptString("  Название: ", false);
        return new Location(x, y, name);
    }

    private Organization promptOrganizationWithId(int id) {
        console.println("Добавление нового элемента с ID " + id + ". Введите данные:");

        String name = promptString("Введите название: ", false);
        Coordinates coordinates = promptCoordinates();
        Double annualTurnover = promptDouble("Введите годовой оборот (или пустую строку для null): ", true);
        String fullName = promptString("Введите полное название (или пустую строку для null): ", true);
        long employeesCount = promptLong("Введите количество сотрудников: ", false);
        OrganizationType type = promptOrganizationType();
        Address address = promptAddress();

        return new Organization(id, name, coordinates, null, annualTurnover, fullName, employeesCount, type, address);
    }
}