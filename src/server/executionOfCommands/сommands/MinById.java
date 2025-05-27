package server.executionOfCommands.сommands;

import server.collectionAction.*;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import server.model.Organization;

import java.util.Comparator;
import java.util.Optional;

/**
 * The type Min by id.
 */
public class MinById extends CommandManager {
    private final CollectionManager collectionManager;

    /**
     * Instantiates a new Min by id.
     *
     * @param collectionManager the collection manager
     */
    public MinById(CollectionManager collectionManager) {
        super("min_by_id", "вывести любой объект из коллекции, значение поля id которого является минимальным");
        this.collectionManager = collectionManager;
    }

    /**
     * Apply execution response.
     *
     * @param arguments the arguments
     * @return the execution response
     */
    @Override
    public ExecutionResponse apply(Object arguments) {
        if (arguments != null && arguments instanceof String && !((String) arguments).isEmpty()) {
            return new ExecutionResponse(false, "Команда min_by_id не принимает аргументы.");
        }

        Optional<Organization> minOrg = collectionManager.getCollection().stream().min(Comparator.comparingInt(Organization::getId));

        if (minOrg.isEmpty()) {
            return new ExecutionResponse(false, "Коллекция не содержит объектов.");
        }

        Organization minOrganization = minOrg.get();
        StringBuilder result = new StringBuilder();
        result.append("Объект с минимальным id:\n");
        result.append(minOrganization.toString());

        return new ExecutionResponse(true, result.toString());
    }
}