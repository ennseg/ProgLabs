package server.executionOfCommands.сommands;

import server.collectionAction.CollectionManager;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import client.standartConsole.Console;

/**
 * The type Show.
 */
public class Show extends CommandManager {
    private final CollectionManager collectionManager;

    /**
     * Instantiates a new Show.
     *
     * @param collectionManager the collection manager
     */
    public Show(CollectionManager collectionManager) {
        super("show", "вывести в стандартный поток вывода все элементы коллекции в строковом представлении");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse apply(Object arguments) {
        if (arguments != null && arguments instanceof String && !((String) arguments).trim().isEmpty()) {
            return new ExecutionResponse(false, "Команда show не принимает аргументы.");
        }

        StringBuilder result = new StringBuilder();
        if (collectionManager.getCollection().isEmpty()) {
            result.append("Коллекция пуста.");
        } else {
            result.append("Элементы коллекции:\n");
            collectionManager.getCollection().forEach(org -> result.append(org.toString()).append("\n"));
        }

        return new ExecutionResponse(true, result.toString());
    }
}