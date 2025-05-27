package server.executionOfCommands.сommands;

import server.collectionAction.*;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import client.standartConsole.Console;

/**
 * The type Exit.
 */
public class Exit extends CommandManager {
    /**
     * Instantiates a new Exit.
     *
     */
    public Exit() {
        super("exit", "завершить программу (без сохранения в файл)");
    }

    @Override
    public ExecutionResponse apply(Object arguments) {
        if (arguments != null && arguments instanceof String && !((String) arguments).isEmpty()) {
            return new ExecutionResponse(false, "Команда exit не принимает аргументы.");
        }
        return new ExecutionResponse(true, "соединение прервано");
    }
}
