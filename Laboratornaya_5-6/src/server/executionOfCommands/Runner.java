package server.executionOfCommands;

import server.CommandRequest;
import server.CommandResponse;
import server.collectionAction.CollectionManager;
import server.executionOfCommands.сommands.*;
import client.standartConsole.Console;
import java.util.List;
import java.util.ArrayList;

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

    /**
     * This is my constructor for runner
     * @param console console for input/output
     * @param collectionManager manager for collection operations
     * @return nothing
     */
    public Runner(Console console, CollectionManager collectionManager) {
        this.console = console;
        this.memory = new ArrayList<>();
        this.scriptStack = new ArrayList<>();
        this.commands = new ArrayList<>();
        this.collectionManager = collectionManager;
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
        commands.add(new RemoveLower(collectionManager));
        commands.add(new RemoveGreater(collectionManager));
        commands.add(new Exit());
        //commands.add(new ExecuteScript(collectionManager, console, this));
        commands.add(new Save(collectionManager));
        commands.add(new Clear(collectionManager));
        commands.add(new RemoveById(collectionManager));
        commands.add(new UpdateId(collectionManager));
        commands.add(new Add(collectionManager));
        commands.add(new Show(collectionManager));
        commands.add(new Info(collectionManager, console));
        commands.add(new Help(this, console));
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
        Object argument = request.getArgument();
        System.out.println("Начало обработки команды: " + commandName + ", аргумент: " + argument);

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
                        System.err.println("Команда " + commandName + " вернула null ExecutionResponse");
                        return new CommandResponse("Ошибка сервера: команда не вернула результат", false);
                    }
                    System.out.println("Команда выполнена: exitCode=" + execResponse.getExitCode() + ", сообщение=" + execResponse.getMessage());
                    return new CommandResponse(execResponse.getMessage(), execResponse.getExitCode());
                } catch (Exception e) {
                    System.err.println("Ошибка выполнения команды " + commandName + ": " + e.getMessage());
                    e.printStackTrace();
                    return new CommandResponse("Ошибка выполнения команды: " + (e.getMessage() != null ? e.getMessage() : "неизвестная ошибка"), false);
                }
            }
        }
        System.out.println("Команда не найдена: " + commandName);
        return new CommandResponse("Неизвестная команда: " + commandName, false);
    }

    /**
     * This is my method to run interactive mode
     * @return nothing
     */
}