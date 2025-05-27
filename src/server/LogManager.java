package server;

import java.io.IOException;
import java.util.logging.*;

public class LogManager {
    private static final Logger LOGGER = Logger.getLogger(Server.class.getName());

    public void setupLogger() throws IOException {
        LOGGER.setLevel(Level.ALL);

        LOGGER.setUseParentHandlers(false);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(consoleHandler);

        FileHandler fileHandler = new FileHandler("server.log", true);
        fileHandler.setLevel(Level.ALL);
        fileHandler.setFormatter(new SimpleFormatter());
        LOGGER.addHandler(fileHandler);
    }
}
