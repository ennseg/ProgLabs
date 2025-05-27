package server.executionOfCommands;

import server.CommandRequest;
import server.CommandResponse;
import server.Server;
import server.collectionAction.CollectionManager;
import server.collectionAction.DataBaseConnection;
import server.executionOfCommands.сommands.*;
import client.standartConsole.Console;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import server.collectionAction.DataBaseManager;

/**
 * This is my runner class
 * @author I
 */
public class Runner {
    private final Console console;
    private final List<String> memory;
    private final List<String> scriptStack;
    private final List<CommandManager> commands;
    private final CollectionManager collectionManager;
    private final DataBaseConnection dbconnection;
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());
    /**
     * This is my constructor for runner
     * @param console console for input/output
     * @param collectionManager manager for collection operations
     * @return nothing
     */
    public Runner(Console console, CollectionManager collectionManager, DataBaseConnection dbconnection) {
        this.console = console;
        this.memory = new ArrayList<>();
        this.scriptStack = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.collectionManager = collectionManager;
        this.dbconnection = dbconnection;
        initializeCommands();
    }

    /**
     * This is my method to initialize commands
     * @return nothing
     */
    private void initializeCommands() {
        commands.add(new PrintFieldDescendingAnnualTurnoverCommand(collectionManager));
        commands.add(new MinById(collectionManager));
        commands.add(new SumOfAnnualTurnover(collectionManager));
        commands.add(new History(memory));
        commands.add(new RemoveLower(collectionManager, dbconnection));
        commands.add(new RemoveGreater(collectionManager, dbconnection));
        commands.add(new Exit());
        //commands.add(new ExecuteScript(collectionManager, console, this));
        //commands.add(new Save(collectionManager));
        commands.add(new Clear(collectionManager, dbconnection));
        commands.add(new RemoveById(collectionManager, dbconnection));
        commands.add(new UpdateId(collectionManager, dbconnection));
        commands.add(new Add(collectionManager, dbconnection));
        commands.add(new Show(collectionManager));
        commands.add(new Info(collectionManager, console));
        commands.add(new Help(this, console));
        commands.add(new Exist(collectionManager, dbconnection));
        commands.add(new Register(dbconnection));
        commands.add(new Login(dbconnection));
    }

    /**
     * This is my method to get list of commands
     * @return list of commands
     */
    public List<CommandManager> getCommands() {
        return commands;
    }

    /**
     * This is my method to execute a command
     * @param request command string to execute
     * @return execution result
     */

    public CommandResponse executeCommand(CommandRequest request) {
        String commandName = request.getCommandName().toLowerCase();
        Object argument;

        if (commandName.equals("register") || commandName.equals("login") || commandName.equals("add") || commandName.equals("update") || commandName.equals("clear") || commandName.equals("remove_by_id") || commandName.equals("exist")) {
            argument = request;
        } else {
            argument = request.getArgument();
        }

        if (!commandName.equals("register") && !commandName.equals("login")) {
            String username = request.getUsername();
            String password = request.getPassword();

            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                return new CommandResponse("Ошибка: требуется авторизация (указаны логин и пароль)", false);
            }

            DataBaseManager dbManager = new DataBaseManager(dbconnection);
            try {
                if (!dbManager.authenticateUser(username, password)) {
                    return new CommandResponse("Ошибка: авторизация не пройдена", false);
                }
            } catch (SQLException e) {
                LOGGER.severe("Ошибка проверки авторизации: " + e.getMessage());
                return new CommandResponse("Ошибка сервера при проверке авторизации: " + e.getMessage(), false);
            }
        }

        memory.add(commandName);
        scriptStack.add(commandName);

        int rec_deep = 500;
        if (scriptStack.size() > rec_deep) {
            return new CommandResponse("Превышена глубина рекурсии (" + rec_deep + ")", false);
        }
        for (CommandManager cmd : commands) {
            if (cmd.getName().equals(commandName)) {
                try {
                    ExecutionResponse execResponse = cmd.apply(argument);
                    if (execResponse == null) {
                        LOGGER.severe("Команда " + commandName + " вернула null ExecutionResponse");
                        return new CommandResponse("Ошибка сервера: команда не вернула результат", false);
                    }
                    return new CommandResponse(execResponse.getMessage(), execResponse.getExitCode());
                } catch (Exception e) {
                    LOGGER.severe("Ошибка выполнения команды " + commandName + ": " + e.getMessage());
                    e.printStackTrace();
                    return new CommandResponse("Ошибка выполнения команды: " + (e.getMessage() != null ? e.getMessage() : "неизвестная ошибка"), false);
                }
            }
        }
        return new CommandResponse("Неизвестная команда: " + commandName, false);
    }

    /**
     * This is my method to run interactive mode
     * @return nothing
     */
}