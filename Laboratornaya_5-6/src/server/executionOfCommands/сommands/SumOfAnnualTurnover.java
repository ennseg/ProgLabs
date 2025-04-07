package server.executionOfCommands.сommands;

import server.collectionAction.*;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import server.model.Organization;
import client.standartConsole.Console;

/**
 * The type Sum of annual turnover.
 */
public class SumOfAnnualTurnover extends CommandManager {
    private final CollectionManager collectionManager;

    /**
     * Instantiates a new Sum of annual turnover.
     *
     * @param collectionManager the collection manager
     */
    public SumOfAnnualTurnover(CollectionManager collectionManager) {
        super("sum_of_annual_turnover", "вывести сумму значений поля annualTurnover для всех элементов коллекции");
        this.collectionManager = collectionManager;
    }

    @Override
    public ExecutionResponse apply(Object arguments) {
        if (arguments != null && arguments instanceof String && !((String) arguments).trim().isEmpty()) {
            return new ExecutionResponse(false, "Команда sum_of_annual_turnover не принимает аргументы.");
        }

        double annualTurnovers = collectionManager.getCollection().stream()
                .filter(org -> org.getAnnualTurnover() != null)
                .mapToDouble(Organization::getAnnualTurnover)
                .sum();

        StringBuilder result = new StringBuilder();
        result.append("Сумма значений annualTurnover: ");
        if (collectionManager.getCollection().isEmpty() || annualTurnovers == 0) {
            result.append("0 (коллекция пуста или не содержит значений annualTurnover).");
        } else {
            result.append(annualTurnovers);
        }

        return new ExecutionResponse(true, result.toString());
    }
}
