package server.executionOfCommands.сommands;

import server.collectionAction.*;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import client.standartConsole.Console;

import java.io.IOException;
import java.sql.SQLException;

/**
 * The type Save.
 */
public class Save extends CommandManager {
    private final CollectionManager collectionManager;

    /**
     * Instantiates a new Save.
     *
     * @param collectionManager the collection manager
     */
    public Save(CollectionManager collectionManager) {
        super("save", "сохранить коллекцию в файл (внутренняя команда)");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse apply(Object arguments) {
        if (arguments != null && arguments instanceof String && !((String) arguments).trim().isEmpty()) {
            return new ExecutionResponse(false, "Команда save не принимает аргументы.");
        }

        if (collectionManager.getCollection().isEmpty()) {
            return new ExecutionResponse(true, "Коллекция пуста, сохранён пустой файл.");
        }

        try {
            collectionManager.saveCollection();
            return new ExecutionResponse(true, "Коллекция успешно сохранена в базу данных.");
        } catch (IOException | SQLException e) {
            return new ExecutionResponse(false, "Ошибка при сохранении коллекции: " + e.getMessage());
        }
    }
}