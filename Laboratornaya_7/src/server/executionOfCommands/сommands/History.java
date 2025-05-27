package server.executionOfCommands.сommands;

import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import client.standartConsole.Console;

import java.util.List;

/**
 * The type History.
 */
public class History extends CommandManager {
    private final List<String> memory;

    /**
     * Instantiates a new History.
     *
     * @param memory the memory
     */
    public History(List<String> memory) {
        super("history", "вывести последние 9 команд (без их аргументов");
        this.memory = memory;
    }

    @Override
    public ExecutionResponse apply(Object arguments) {
        if (arguments != null && arguments instanceof String && !((String) arguments).isEmpty()) {
            return new ExecutionResponse(false, "Команда history не принимает аргументы.");
        }

        if (memory == null || memory.isEmpty()) {
            return new ExecutionResponse(true, "История команд пуста.");
        }

        StringBuilder historyMessage = new StringBuilder("Последние 9 команд:\n");
        /*int size = memory.size();
        if (size < 9) {for (int i = 0; i < size; i++) {
            console.println(memory.get(i));}
        } else {
            int startIndex = Math.max(0, size - 9); // Начало (не более 0)
            for (int i = startIndex; i < size; i++) {
                console.println(memory.get(i));
            }*/
        memory.stream()
                .skip(Math.max(0, memory.size() - 9))
                .forEach(command -> historyMessage.append(command).append("\n"));

        return new ExecutionResponse(true, historyMessage.toString());
    }
}
