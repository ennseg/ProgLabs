package server.executionOfCommands.сommands;

import server.CommandRequest;
import server.collectionAction.CollectionManager;
import server.collectionAction.DataBaseConnection;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;

import java.io.IOException;
import java.sql.*;
import java.util.concurrent.locks.*;
import java.util.logging.Logger;
import server.collectionAction.DataBaseManager;

/**
 * The type Clear.
 */
public class Clear extends CommandManager {
    private final CollectionManager collectionManager;
    private final DataBaseConnection dbconnection;
    private static final Logger LOGGER = Logger.getLogger(Clear.class.getName());
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Instantiates a new Clear.
     *
     * @param collectionManager the collection manager
     */
    public Clear(CollectionManager collectionManager, DataBaseConnection dbconnection) {
        super("clear", "очистить коллекцию");
        this.collectionManager = collectionManager;
        this.dbconnection = dbconnection;
    }

    @Override
    public ExecutionResponse apply(Object arguments) throws SQLException {

        CommandRequest request = (CommandRequest) arguments;
        String username = request.getUsername();
        if (username == null || username.trim().isEmpty()) {
            return new ExecutionResponse(false, "Ошибка: логин пользователя не указан.");
        }

        DataBaseManager dbManager = new DataBaseManager(dbconnection);
        int userId = dbManager.getUserIdFromUsername(username, dbconnection);
        if (userId == -1) {
            return new ExecutionResponse(false, "Ошибка: пользователь не найден в базе данных.");
        }

        lock.writeLock().lock();
        try {
            String sql = "DELETE FROM Organization WHERE user_id = ?";
            try (Connection conn = dbconnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }

            collectionManager.getCollection().removeIf(org -> org.getUserId() == userId);

            return new ExecutionResponse(true, "Коллекция очищена для текущего пользователя.");
        } catch (SQLException e) {
            LOGGER.severe("Ошибка очистки коллекции: " + e.getMessage());
            throw e;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
