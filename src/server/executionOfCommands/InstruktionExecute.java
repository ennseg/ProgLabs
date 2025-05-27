package server.executionOfCommands;

import server.CommandResponse;

/**
 * The interface Instruktion execute.
 */
public interface InstruktionExecute {
    /**
     * Execute comand execution response.
     *
     * @param comand the comand
     * @return the execution response
     */
    CommandResponse executeComand(String comand);
}
