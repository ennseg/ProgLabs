package server.executionOfCommands.сommands;

import server.collectionAction.CollectionManager;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import client.standartConsole.Console;

/**
 * The type Remove by id.
 */
public class RemoveById extends CommandManager {
    private final CollectionManager collectionManager;

    /**
     * Instantiates a new Remove by id.
     *
     * @param collectionManager the collection manager
     */
    public RemoveById(CollectionManager collectionManager) {
        super("remove_by_id", "удалить элемент из коллекции по его id");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse apply(Object arguments) {
        if (arguments == null || (arguments instanceof String && ((String) arguments).trim().isEmpty())) {
            return new ExecutionResponse(false, "Команда remove_by_id требует аргумент (ID).");
        }

        String argStr = arguments.toString();
        try {
            int id = Integer.parseInt(argStr.trim());

            if (collectionManager.getCollection().isEmpty()) {
                return new ExecutionResponse(true, "Коллекция пуста, удалять нечего.");
            }

            boolean removed = collectionManager.getCollection().removeIf(org -> org.getId() == id);

            if (removed) {
                return new ExecutionResponse(true, "Элемент с ID " + id + " успешно удалён.");
            } else {
                return new ExecutionResponse(true, "Элемент с ID " + id + " не найден в коллекции.");
            }
        } catch (NumberFormatException e) {
            return new ExecutionResponse(false, "ID должен быть целым числом: " + argStr);
        }
    }
}