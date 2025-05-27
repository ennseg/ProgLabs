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
    private String username;
    private String password;

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
            console.println("Подключение к серверу установлено.");
            if (!authenticateUser()) {
                console.println("Авторизация не пройдена. Завершение работы.");
                return;
            }

            while (true) {
                console.prompt();
                String input = console.readLine().trim();
                if (input.isEmpty()) continue;

                String[] parts = input.split("\\s+", 2);
                String commandName = parts[0].toLowerCase();
                String args = parts.length > 1 ? parts[1] : "";

                CommandRequest request;
                if (commandName.equals("add")) {
                    Organization org = promptOrganization();
                    request = new CommandRequest(username, password, org, commandName);
                } else if (commandName.equals("update")) {
                    if (args.trim().isEmpty()) {
                        console.printError("Команда update требует ID.");
                        continue;
                    }
                    try {
                        request = new CommandRequest(username, password, args.trim(), "exist");
                        networkUtil.sendRequest(request);

                        CommandResponse existsResponse = null;
                        long startTime = System.currentTimeMillis();
                        while (System.currentTimeMillis() - startTime < 5000) {
                            existsResponse = networkUtil.receiveResponse();
                            if (existsResponse != null) break;
                            Thread.sleep(50);
                        }

                        int id = Integer.parseInt(args.trim());
                        System.out.println(args);
                        if (existsResponse.isSuccess()) {
                            console.println("Обновление элемента с ID " + id + ". Введите новые данные:");
                            Organization org = promptOrganizationWithId(id);
                            request = new CommandRequest(username, password, org, commandName);
                        } else {
                            console.printError("Объект с ID " + id + " не существует или у вас нет к нему доступа.");
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        console.printError("ID должен быть целым числом.");
                        continue;
                    }
                } else if (commandName.equals("execute_script")) {
                    scriptExecutor.executeScript(args);
                    return;
                } else {
                    request = new CommandRequest(username, password, args, commandName);
                }

                try {
                    if (!channel.isOpen()) {
                        console.println("Сервер недоступен, переподключение...");
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
        } catch (NoSuchElementException | IOException | InterruptedException e) {
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

    private boolean authenticateUser() throws IOException, InterruptedException {
        while (true) {
            console.println("Выберите действие: <1> Войти <2> Зарегистрироваться <3> Выход");
            String choice = console.readLine().trim();
            if (choice.equals("1")) {
                console.print("Введите логин: ");
                String login = console.readLine().trim();
                console.print("Введите пароль: ");
                String pwd = console.readLine().trim();

                CommandRequest loginRequest = new CommandRequest(login, pwd, null, "login");
                networkUtil.sendRequest(loginRequest);

                CommandResponse response = null;
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 5000) {
                    response = networkUtil.receiveResponse();
                    if (response != null) break;
                    Thread.sleep(50);
                }

                if (response == null) {
                    console.printError("Сервер не ответил вовремя.");
                    continue;
                }

                if (response.isSuccess()) {
                    console.println("Авторизация успешна!");
                    this.username = login;
                    this.password = pwd;
                    return true;
                } else {
                    console.printError("Ошибка авторизации: " + response.getMessage());
                }
            } else if (choice.equals("2")) {
                console.print("Введите логин: ");
                String login = console.readLine().trim();
                console.print("Введите пароль: ");
                String pwd = console.readLine().trim();

                CommandRequest registerRequest = new CommandRequest(login, pwd, null, "register");
                networkUtil.sendRequest(registerRequest);

                CommandResponse response = null;
                long startTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - startTime < 5000) {
                    response = networkUtil.receiveResponse();
                    if (response != null) break;
                    Thread.sleep(50);
                }

                if (response == null) {
                    console.printError("Сервер не ответил вовремя.");
                    continue;
                }

                if (response.isSuccess()) {
                    console.println("Регистрация успешна! Теперь войдите.");
                } else {
                    console.printError("Ошибка регистрации: " + response.getMessage());
                }
            } else if (choice.equals("3")) {
                return false;
            } else {
                console.printError("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    /**
     * Reconnect.
     *
     * @throws IOException the io exception
     */
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
        int userId = 0;

        return new Organization(0, name, coordinates, null, annualTurnover, fullName, employeesCount, type, address, userId);
    }

    /**
     * Prompt for creating an Organization with a specific ID.
     */
    private Organization promptOrganizationWithId(int id) {

        String name = promptString("Введите название: ", false);
        Coordinates coordinates = promptCoordinates();
        Double annualTurnover = promptDouble("Введите годовой оборот (или пустую строку для null): ", true);
        String fullName = promptString("Введите полное название (или пустую строку для null): ", true);
        long employeesCount = promptLong("Введите количество сотрудников: ", false);
        OrganizationType type = promptOrganizationType();
        Address address = promptAddress();
        int userId = 0;

        return new Organization(id, name, coordinates, null, annualTurnover, fullName, employeesCount, type, address, userId);
    }

    /**
     * Prompt for a user ID.
     *
     * @param message  the message
     * @param nullable the nullable
     * @return the integer
     */
    /**
     * Prompt for a string.
     *
     * @param message  the message
     * @param nullable the nullable
     * @return the string
     */
    public String promptString(String message, boolean nullable) {
        while (true) {
            try {
                console.print(message);
                String input = console.readLine();
                if (input == null) {
                    console.printError("Ошибка ввода. Попробуйте снова.");
                    continue;
                }
                input = input.trim();
                if (nullable && input.isEmpty()) return null;
                if (!input.isEmpty()) return input;
                console.printError("Значение не может быть пустым.");
            } catch (Exception e) {
                console.printError("Неожиданная ошибка: " + e.getMessage());
            }
        }
    }

    /**
     * Prompt for a double value.
     *
     * @param message  the message
     * @param nullable the nullable
     * @return the double
     */
    public Double promptDouble(String message, boolean nullable) {
        while (true) {
            try {
                console.print(message);
                String input = console.readLine();
                if (input == null) {
                    console.printError("Ошибка ввода. Попробуйте снова.");
                    continue;
                }
                input = input.trim();
                if (nullable && input.isEmpty()) return null;
                double value = Double.parseDouble(input);
                if (Double.isNaN(value) || Double.isInfinite(value)) {
                    console.printError("Значение должно быть конечным числом.");
                    continue;
                }
                if (value <= 0) {
                    console.printError("Значение должно быть больше 0.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                console.printError("Ошибка: Введите число с плавающей точкой.");
            } catch (Exception e) {
                console.printError("Неожиданная ошибка: " + e.getMessage());
            }
        }
    }

    /**
     * Prompt for a long value.
     *
     * @param message  the message
     * @param nullable the nullable
     * @return the long
     */
    public long promptLong(String message, boolean nullable) {
        while (true) {
            try {
                console.print(message);
                String input = console.readLine();
                if (input == null) {
                    console.printError("Ошибка ввода. Попробуйте снова.");
                    continue;
                }
                input = input.trim();
                if (!nullable && input.isEmpty()) {
                    console.printError("Значение не может быть пустым.");
                    continue;
                }
                long value = Long.parseLong(input);
                if (value <= 0) {
                    console.printError("Значение должно быть больше 0.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                console.printError("Ошибка: Введите целое число.");
            } catch (Exception e) {
                console.printError("Неожиданная ошибка: " + e.getMessage());
            }
        }
    }

    /**
     * Prompt for Coordinates.
     *
     * @return the coordinates
     */
    public Coordinates promptCoordinates() {
        console.println("Введите координаты:");
        while (true) {
            try {
                double x = promptDouble("  x: ", false);
                long y = promptLong("  y (максимум 260): ", false);
                if (y > 260) {
                    console.printError("y не может быть больше 260.");
                    continue;
                }
                return new Coordinates(x, y);
            } catch (IllegalArgumentException e) {
                console.printError("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                console.printError("Неожиданная ошибка при вводе координат: " + e.getMessage());
            }
        }
    }

    /**
     * Prompt for OrganizationType.
     *
     * @return the organization type
     */
    public OrganizationType promptOrganizationType() {
        console.println("Выберите тип организации (доступные варианты: PUBLIC, TRUST, PRIVATE_LIMITED_COMPANY):");
        while (true) {
            try {
                console.print("Тип: ");
                String input = console.readLine();
                if (input == null) {
                    console.printError("Ошибка ввода. Попробуйте снова.");
                    continue;
                }
                input = input.trim();
                return OrganizationType.valueOf(input.toUpperCase());
            } catch (IllegalArgumentException e) {
                console.printError("Неверный тип. Выберите из: PUBLIC, TRUST, PRIVATE_LIMITED_COMPANY.");
            } catch (Exception e) {
                console.printError("Неожиданная ошибка: " + e.getMessage());
            }
        }
    }

    /**
     * Prompt for Address.
     *
     * @return the address
     */
    public Address promptAddress() {
        console.println("Введите адрес:");
        while (true) {
            try {
                String street = promptString("  Улица (или пустая строка для null): ", true);
                String zipCode = promptString("  Почтовый индекс (максимум 22 символа): ", false);
                if (zipCode.length() > 22) {
                    console.printError("Почтовый индекс не может быть длиннее 22 символов.");
                    continue;
                }
                Location town = promptLocation();
                return new Address(street, zipCode, town);
            } catch (IllegalArgumentException e) {
                console.printError("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                console.printError("Неожиданная ошибка при вводе адреса: " + e.getMessage());
            }
        }
    }

    /**
     * Prompt for Location.
     *
     * @return the location
     */
    public Location promptLocation() {
        console.println("Введите местоположение:");
        while (true) {
            try {
                console.print("  x: ");
                String xInput = console.readLine();
                if (xInput == null) {
                    console.printError("Ошибка ввода. Попробуйте снова.");
                    continue;
                }
                xInput = xInput.trim();
                if (xInput.isEmpty()) {
                    console.printError("x не может быть пустым.");
                    continue;
                }
                Integer x = Integer.parseInt(xInput);
                long y = promptLong("  y: ", false);
                String name = promptString("  Название: ", false);
                return new Location(x, y, name);
            } catch (NumberFormatException e) {
                console.printError("Ошибка: x должен быть целым числом.");
            } catch (IllegalArgumentException e) {
                console.printError("Ошибка: " + e.getMessage());
            } catch (Exception e) {
                console.printError("Неожиданная ошибка при вводе местоположения: " + e.getMessage());
            }
        }
    }
}