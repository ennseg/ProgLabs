package server.executionOfCommands;

import server.CommandResponse;

import java.sql.SQLException;

/**
 * The interface Executable.
 */
public interface Executable {
    /**
     * Apply execution response.
     *
     * @param arguments the arguments
     * @return the execution response
     */
    ExecutionResponse apply(Object arguments) throws SQLException;
}
