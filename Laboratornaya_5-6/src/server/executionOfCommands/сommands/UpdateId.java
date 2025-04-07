package server.executionOfCommands.сommands;

import server.collectionAction.CollectionManager;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import server.model.*;

/**
 * The type Update id.
 */
public class UpdateId extends CommandManager {
    private final CollectionManager collectionManager;

    /**
     * Instantiates a new Update id.
     *
     * @param collectionManager the collection manager
     */
    public UpdateId(CollectionManager collectionManager) {
        super("update", "обновить значение элемента коллекции по id");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse apply(Object arguments) {
        if (arguments == null) {
            return new ExecutionResponse(false, "Команда update требует аргумент (объект Organization).");
        }

        if (!(arguments instanceof Organization)) {
            return new ExecutionResponse(false, "Аргумент команды update должен быть объектом Organization.");
        }

        Organization newOrg = (Organization) arguments;
        int id = newOrg.getId();

        if (collectionManager.getCollection().isEmpty()) {
            return new ExecutionResponse(true, "Коллекция пуста, обновлять нечего.");
        }

        boolean removed = collectionManager.getCollection().removeIf(org -> org.getId() == id);
        if (!removed) {
            return new ExecutionResponse(true, "Элемент с ID " + id + " не найден в коллекции.");
        }

        collectionManager.getCollection().add(newOrg);
        return new ExecutionResponse(true, "Элемент с ID " + id + " успешно обновлён.");
    }
}