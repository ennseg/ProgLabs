package server.executionOfCommands.сommands;

import server.collectionAction.CollectionManager;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import client.standartConsole.Console;

/**
 * The type Clear.
 */
public class Clear extends CommandManager {
    private final CollectionManager collectionManager;

    /**
     * Instantiates a new Clear.
     *
     * @param collectionManager the collection manager
     */
    public Clear(CollectionManager collectionManager) {
        super("clear", "очистить коллекцию");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse apply(Object arguments) {
        if (arguments != null && arguments instanceof String && !((String) arguments).isEmpty()) {
            return new ExecutionResponse(false, "Команда clear не принимает аргументы.");
        }
        if (!collectionManager.getCollection().isEmpty()) {
            collectionManager.getCollection().clear();
            return new ExecutionResponse(true, "Коллекция очищена");
        } else {
            return new ExecutionResponse(true, "Коллекция уже пуста");
        }
    }
}