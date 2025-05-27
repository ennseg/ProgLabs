package server.executionOfCommands.сommands;

import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import server.executionOfCommands.Runner;
import client.standartConsole.Console;

/**
 * The type Help.
 */
public class Help extends CommandManager {
    private final Runner runner;
    /**
     * Instantiates a new Help.
     *
     * @param runner  the runner
     * @param console the console
     */
    public Help(Runner runner, Console console) {
        super("help", "вывести справку по доступным командам");
        this.runner = runner;
    }

    @Override
    public ExecutionResponse apply(Object arguments) {
        if (arguments != null && arguments instanceof String && !((String) arguments).isEmpty()) {
            return new ExecutionResponse(false, "Команда help не принимает аргументы.");
        }
        StringBuilder helpMessage = new StringBuilder("Список доступных команд:\n");
        helpMessage.append("Серверные команды:\n");
        for (CommandManager command : runner.getCommands()) {
            helpMessage.append(command.getName())
                    .append(" : ")
                    .append(command.getDescription())
                    .append("\n");
        }
        helpMessage.append("\nКлиентские команды:\n");
        helpMessage.append("execute_script <file_name> : считать и исполнить скрипт из указанного файла\n");
        return new ExecutionResponse(true, helpMessage.toString());
    }
}