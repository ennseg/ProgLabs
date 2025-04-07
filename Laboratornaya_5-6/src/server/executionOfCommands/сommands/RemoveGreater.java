package server.executionOfCommands.сommands;

import server.collectionAction.CollectionManager;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import client.standartConsole.Console;

/**
 * The type Remove greater.
 */
public class RemoveGreater extends CommandManager {
    private final CollectionManager collectionManager;

    /**
     * Instantiates a new Remove greater.
     *
     * @param collectionManager the collection manager
     */
    public RemoveGreater(CollectionManager collectionManager) {
        super("remove_greater", "удалить из коллекции все элементы, большие чем заданный (по ID)");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse apply(Object arguments) {
        if (arguments == null || (arguments instanceof String && ((String) arguments).trim().isEmpty())) {
            return new ExecutionResponse(false, "Команда remove_greater требует аргумент (ID).");
        }

        String argStr = arguments.toString();
        try {
            int threshdId = Integer.parseInt(argStr.trim());

            if (collectionManager.getCollection().isEmpty()) {
                return new ExecutionResponse(true, "Коллекция пуста, удалять нечего.");
            }

            boolean removed = collectionManager.getCollection().removeIf(org -> org.getId() > threshdId);

            if (removed) {
                return new ExecutionResponse(true, "Удалены все элементы с ID больше " + threshdId + ".");
            } else {
                return new ExecutionResponse(true, "Не найдено элементов с ID больше " + threshdId + ".");
            }
        } catch (NumberFormatException e) {
            return new ExecutionResponse(false, "Аргумент должен быть целым числом (ID): " + argStr);
        }
    }
}