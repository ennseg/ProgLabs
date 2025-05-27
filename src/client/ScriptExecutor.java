package client;

import server.model.*;
import server.CommandRequest;
import server.CommandResponse;
import client.standartConsole.Console;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * The type ScriptExecutor.
 */
public class ScriptExecutor {
    private final Console console;
    private final SocketChannel channel;
    private final Set<String> executingFiles = new HashSet<>();
    private final Starter starter;
    private final NetworkUtil networkUtil;
    private String username;
    private String password;

    /**
     * Instantiates a new ScriptExecutor.
     *
     * @param console     the console
     * @param channel     the channel
     * @param starter     the starter
     * @param networkUtil the network utility instance
     */
    public ScriptExecutor(Console console, SocketChannel channel, Starter starter, NetworkUtil networkUtil) {
        this.console = console;
        this.channel = channel;
        this.starter = starter;
        this.networkUtil = networkUtil;
    }

    /**
     * Execute script from file.
     *
     * @param filePath the file path
     * @return true if script execution should terminate the client (e.g., exit command), false otherwise
     */
    public boolean executeScript(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            console.printError("Укажите имя файла скрипта!");
            return false;
        }

        File scriptFile = new File(filePath);
        if (!scriptFile.exists()) {
            console.printError("Файл '" + filePath + "' не существует!");
            return false;
        }

        if (!Files.isReadable(Paths.get(filePath))) {
            console.printError("Нет прав для чтения файла '" + filePath + "'!");
            return false;
        }

        String absolutePath = scriptFile.getAbsolutePath();
        if (executingFiles.contains(absolutePath)) {
            console.printError("Обнаружена рекурсия: файл '" + filePath + "' уже выполняется!");
            return false;
        }

        try (Scanner fileScanner = new Scanner(scriptFile)) {
            executingFiles.add(absolutePath);
            console.println("Выполнение скрипта из файла '" + filePath + "'...");

            while (fileScanner.hasNextLine()) {
                String command = fileScanner.nextLine().trim();
                if (command.isEmpty()) continue;

                String[] parts = command.split("\\s+", 2);
                String commandName = parts[0].toLowerCase();
                String args = parts.length > 1 ? parts[1] : "";

                if (commandName.equals("save")) {
                    console.printError("У вас нет прав на выполнение команды 'save'.");
                    continue;
                }

                CommandRequest request;
                if (commandName.equals("add")) {
                    Organization org = parseOrganization(args);
                    if (org == null) {
                        console.printError("Ошибка парсинга команды add: " + command);
                        continue;
                    }
                    request = new CommandRequest(username, password, org, commandName);
                } else if (commandName.equals("update")) {
                    Organization org = parseUpdateOrganization(args);
                    if (org == null) {
                        console.printError("Ошибка парсинга команды update: " + command);
                        continue;
                    }
                    request = new CommandRequest(username, password, org, commandName);
                } else if (commandName.equals("execute_script")) {
                    if (executeScript(args)) {
                        return true;
                    }
                    continue;
                } else {
                    request = new CommandRequest(username, password, args, commandName);
                }

                console.println("Выполняется: " + command);
                try {
                    if (!channel.isOpen()) {
                        console.println("Сервер недоступен, переподключаемся...");
                        starter.reconnect();
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
                            console.println("Завершение работы клиента из скрипта.");
                            channel.close();
                            return true;
                        }
                        if (!response.getMessage().isEmpty()) {
                            if (response.isSuccess()) {
                                console.println(response.getMessage());
                            } else {
                                console.printError(response.getMessage());
                            }
                        }
                    } else {
                        console.printError("Сервер не ответил вовремя на команду: " + command);
                    }
                } catch (IOException e) {
                    console.printError("Ошибка связи с сервером при выполнении " + command + ": " + e.getMessage());
                    try {
                        channel.close();
                        starter.reconnect();
                    } catch (IOException ex) {
                        console.printError("Не удалось переподключиться: " + ex.getMessage());
                    }
                } catch (InterruptedException e) {
                    console.printError("Прерывание ожидания при выполнении " + command + ": " + e.getMessage());
                }
            }

            executingFiles.remove(absolutePath);
            console.println("Скрипт из файла '" + filePath + "' успешно выполнен.");
            return false;
        } catch (FileNotFoundException e) {
            console.printError("Ошибка при открытии файла '" + filePath + "': " + e.getMessage());
            executingFiles.remove(absolutePath);
            return false;
        }
    }

    private Organization parseOrganization(String args) {
        String[] fields = args.split("\\s+");
        if (fields.length != 13) {
            console.printError("Неверное количество аргументов для add. Ожидается 13, например add \"name\" x y annualTurnover \"fullName\" employeesCount type \"street\" \"zipCode\" townX townY \"townName\": " + args);
            return null;
        }

        try {
            String name = fields[0].replace("\"", "");
            double x = Double.parseDouble(fields[1]);
            long y = Long.parseLong(fields[2]);
            Coordinates coordinates = new Coordinates(x, y);

            Double annualTurnover = fields[3].equals("null") ? null : Double.parseDouble(fields[3]);
            String fullName = fields[4].equals("null") ? null : fields[4].replace("\"", "");
            long employeesCount = Long.parseLong(fields[5]);
            OrganizationType type = OrganizationType.valueOf(fields[6].toUpperCase());
            String street = fields[7].equals("null") ? null : fields[7].replace("\"", "");
            String zipCode = fields[8].equals("null") ? null : fields[8].replace("\"", "");
            Integer townX = fields[9].equals("null") ? null : Integer.parseInt(fields[9]);
            long townY = Long.parseLong(fields[10]);
            String townName = fields[11].equals("null") ? null : fields[11].replace("\"", "");
            Location town = new Location(townX, townY, townName);
            Address address = new Address(street, zipCode, town);
            int userId = Integer.parseInt(fields[12]);

            return new Organization(0, name, coordinates, null, annualTurnover, fullName, employeesCount, type, address, userId);
        } catch (IllegalArgumentException e) {
            console.printError("Ошибка формата данных: " + e.getMessage());
            return null;
        }
    }

    private Organization parseUpdateOrganization(String args) {
        String[] fields = args.split("\\s+");
        if (fields.length != 14) {
            console.printError("Неверное количество аргументов для update. Ожидается 14: " + args);
            return null;
        }

        try {
            int id = Integer.parseInt(fields[0]);
            String name = fields[1].replace("\"", "");
            double x = Double.parseDouble(fields[2]);
            long y = Long.parseLong(fields[3]);
            Coordinates coordinates = new Coordinates(x, y);

            Double annualTurnover = fields[4].equals("null") ? null : Double.parseDouble(fields[4]);
            String fullName = fields[5].equals("null") ? null : fields[5].replace("\"", "");
            long employeesCount = Long.parseLong(fields[6]);
            OrganizationType type = OrganizationType.valueOf(fields[7].toUpperCase());

            String street = fields[8].equals("null") ? null : fields[8].replace("\"", "");
            String zipCode = fields[9].equals("null") ? null : fields[9].replace("\"", "");
            Integer townX = fields[10].equals("null") ? null : Integer.parseInt(fields[10]);
            long townY = Long.parseLong(fields[11]);
            String townName = fields[12].equals("null") ? null : fields[12].replace("\"", "");
            Location town = new Location(townX, townY, townName);
            Address address = new Address(street, zipCode, town);
            int userId = Integer.parseInt(fields[13]);

            return new Organization(id, name, coordinates, null, annualTurnover, fullName, employeesCount, type, address, userId);
        } catch (IllegalArgumentException e) {
            console.printError("Ошибка формата данных: " + e.getMessage());
            return null;
        }
    }
}