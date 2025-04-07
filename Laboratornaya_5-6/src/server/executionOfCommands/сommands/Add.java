package server.executionOfCommands.сommands;

import server.collectionAction.CollectionManager;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import server.model.Organization;

import java.time.LocalDateTime;

/**
 * The type Add.
 */
public class Add extends CommandManager {
    private final CollectionManager collectionManager;

    /**
     * Instantiates a new Add.
     *
     * @param collectionManager the collection manager
     */
    public Add(CollectionManager collectionManager) {
        super("add", "добавить новый элемент в коллекцию");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse apply(Object arguments) {
        if (!(arguments instanceof Organization)) {
            return new ExecutionResponse(false, "Команда add требует объект Organization в качестве аргумента.");
        }

        try {
            Organization newOrg = (Organization) arguments;
            newOrg.setId(collectionManager.generateId());
            newOrg.setCreationDate(LocalDateTime.now());

            collectionManager.getCollection().add(newOrg);
            return new ExecutionResponse(true, "Элемент успешно добавлен в коллекцию.");
        } catch (Exception e) {
            return new ExecutionResponse(false, "Ошибка при добавлении элемента: " + e.getMessage());
        }
    }


}