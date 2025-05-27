package server.executionOfCommands.сommands;

import server.collectionAction.CollectionManager;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import client.standartConsole.Console;

/**
 * The type Info.
 */
public class Info extends CommandManager {
    private final CollectionManager collectionManager;

    /**
     * Instantiates a new Info.
     *
     * @param collectionManager the collection manager
     * @param console           the console
     */
    public Info(CollectionManager collectionManager, Console console) {
        super("info", "вывести в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов и т.д.)");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse apply(Object arguments) {
        if (arguments != null && arguments instanceof String && !((String) arguments).isEmpty()) {
            return new ExecutionResponse(false, "Команда info не принимает аргументы.");
        }
        StringBuilder info = new StringBuilder();
        info.append("Информация о коллекции:\n");
        info.append("Тип: ").append(collectionManager.getCollection().getClass().getSimpleName()).append("\n");
        info.append("Дата инициализации: ").append(collectionManager.getLastInitTime()).append("\n");
        info.append("Количество элементов: ").append(collectionManager.getCollection().size());

        return new ExecutionResponse(true, info.toString());
    }
}