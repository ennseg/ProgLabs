package server.executionOfCommands.сommands;

import server.collectionAction.CollectionManager;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import client.standartConsole.Console;

/**
 * The type Remove lower.
 */
public class RemoveLower extends CommandManager {
    private final CollectionManager collectionManager;

    /**
     * Instantiates a new Remove lower.
     *
     * @param collectionManager the collection manager
     */
    public RemoveLower(CollectionManager collectionManager) {
        super("remove_lower", "удалить из коллекции все элементы, меньшие чем заданный (по ID)");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse apply(Object arguments) {
        if (arguments == null || (arguments instanceof String && ((String) arguments).trim().isEmpty())) {
            return new ExecutionResponse(false, "Команда remove_lower требует аргумент (ID).");
        }

        String argStr = arguments.toString();
        try {
            int threshId = Integer.parseInt(argStr.trim());

            if (collectionManager.getCollection().isEmpty()) {
                return new ExecutionResponse(true, "Коллекция пуста, удалять нечего.");
            }

            boolean removed = collectionManager.getCollection().removeIf(org -> org.getId() < threshId);

            if (removed) {
                return new ExecutionResponse(true, "Удалены все элементы с ID меньше " + threshId + ".");
            } else {
                return new ExecutionResponse(true, "Не найдено элементов с ID меньше " + threshId + ".");
            }
        } catch (NumberFormatException e) {
            return new ExecutionResponse(false, "Аргумент должен быть целым числом (ID): " + argStr);
        }
    }
}