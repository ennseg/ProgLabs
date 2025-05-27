package server.executionOfCommands;

import client.standartConsole.*;

/**
 * The type Defolt ask.
 */
public class DefoltAsk implements Ask {
    private ConsoleIn consoleIn;

    /**
     * Instantiates a new Defolt ask.
     *
     * @param consoleIn the console in
     */
    public DefoltAsk(ConsoleIn consoleIn) {
        this.consoleIn = consoleIn;
    }

    @Override
    public String getCommand() {
        return consoleIn.readLine();
    }
}