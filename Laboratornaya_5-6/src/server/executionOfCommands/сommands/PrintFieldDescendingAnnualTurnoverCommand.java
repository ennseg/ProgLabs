package server.executionOfCommands.сommands;

import server.collectionAction.*;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import server.model.Organization;

import java.util.Comparator;


/**
 * The type Print field descending annual turnover command.
 */
public class PrintFieldDescendingAnnualTurnoverCommand extends CommandManager {
      private final CollectionManager collectionManager;

      /**
       * Instantiates a new Print field descending annual turnover command.
       *
       * @param collectionManager the collection manager
       */
      public PrintFieldDescendingAnnualTurnoverCommand(CollectionManager collectionManager) {
            super("print_field_descending_annual_turnover", "Выводит значения поля annualTurnover всех элементов коллекции в порядке убывания.");
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
                  return new ExecutionResponse(false, "Команда print_field_descending_annual_turnover не принимает аргументы.");
            }
            /*List<Double> annualTurnovers = new ArrayList<>();
            for (var org : collectionManager.getCollection()) {
                  Double turnover = org.getAnnualTurnover();
                  if (turnover != null) {
                        annualTurnovers.add(turnover);
                  }
            }

            if (annualTurnovers.isEmpty()) {
                  return new ExecutionResponse(false, "Коллекция не содержит значений annualTurnover.");
            }

            Collections.sort(annualTurnovers, Collections.reverseOrder());
            console.println("Значения annualTurnover в порядке убывания:");
            for (Double i : annualTurnovers) {
                  console.println(i);
            }*/
            StringBuilder result = new StringBuilder();
            result.append("Значения annualTurnover в порядке убывания:\n");

            var turnovers = collectionManager.getCollection().stream().filter(org -> org.getAnnualTurnover() != null).map(Organization::getAnnualTurnover).sorted(Comparator.reverseOrder()).toList();

            if (turnovers.isEmpty()) {
                  result.append("Коллекция не содержит значений annualTurnover.");
            } else {
                  turnovers.forEach(turnover -> result.append(turnover).append("\n"));
            }

            return new ExecutionResponse(true, result.toString());
      }
}
