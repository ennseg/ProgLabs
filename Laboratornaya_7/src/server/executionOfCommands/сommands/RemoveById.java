package server.executionOfCommands.сommands;

import server.CommandRequest;
import server.collectionAction.CollectionManager;
import server.collectionAction.DataBaseConnection;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import java.sql.*;
import java.util.concurrent.locks.*;
import java.util.logging.Logger;
import server.collectionAction.DataBaseManager;

/**
 * The type Remove by id.
 */
public class RemoveById extends CommandManager {
    private final CollectionManager collectionManager;
    private static final Logger LOGGER = Logger.getLogger(RemoveById.class.getName());
    private final DataBaseConnection dbconnection;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Instantiates a new Remove by id.
     *
     * @param collectionManager the collection manager
     * @param dbconnection      the dbconnection
     */
    public RemoveById(CollectionManager collectionManager, DataBaseConnection dbconnection) {
        super("remove_by_id", "удалить элемент из коллекции по его id");
        this.collectionManager = collectionManager;
        this.dbconnection = dbconnection;
    }

    public boolean organizationExists(DataBaseConnection dbconnection, int id) throws SQLException {

        String query = "SELECT 1 FROM Organization WHERE id = ?";
        try (
                Connection conn = dbconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public ExecutionResponse apply(Object arguments) throws SQLException {
        if (arguments == null || (arguments instanceof String && ((String) arguments).trim().isEmpty())) {
            return new ExecutionResponse(false, "Команда remove_by_id требует аргумент (ID).");
        }

        CommandRequest request = (CommandRequest) arguments;
        String argStr = request.getArgument().toString();
        int id = Integer.parseInt(argStr.trim());

        String username = request.getUsername();
        if (username == null || username.trim().isEmpty()) {
            return new ExecutionResponse(false, "Ошибка: логин пользователя не указан.");
        }

        DataBaseManager dbManager = new DataBaseManager(dbconnection);
        int userId = dbManager.getUserIdFromUsername(username, dbconnection);
        if (userId == -1) {
            return new ExecutionResponse(false, "Ошибка: пользователь не найден в базе данных.");
        }

        if (!dbManager.hasAccess(id, userId)) {
            return new ExecutionResponse(false, "Ошибка: у вас нет прав для удаления этого объекта.");
        }


        lock.writeLock().lock();
        try {

            if (collectionManager.getCollection().isEmpty()) {
                return new ExecutionResponse(true, "Коллекция пуста, удалять нечего.");
            }

            Connection conn = null;
            try {
                conn = dbconnection.getConnection();
                conn.setAutoCommit(false);
                try (PreparedStatement stmt = conn.prepareStatement(
                        "DELETE FROM Organization WHERE id = ?")) {
                    stmt.setInt(1, id);
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        LOGGER.info("Удалён объект с ID: " + id + " из базы.");
                    } else {
                        LOGGER.warning("Объект с ID: " + id + " не найден в базе.");
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException rollbackEx) {
                        LOGGER.severe("Ошибка при откате транзакции: " + rollbackEx.getMessage());
                    }
                }
                LOGGER.severe("Ошибка удаления объекта с ID " + id + ": " + e.getMessage());
                throw e;
            } finally {
                if (conn != null) {
                    try {
                        conn.setAutoCommit(true);
                        conn.close();
                    } catch (SQLException closeEx) {
                        LOGGER.severe("Ошибка при закрытии соединения: " + closeEx.getMessage());
                    }
                }
            }

            boolean removed = false;
            if (!organizationExists(dbconnection, id)) {
                removed = collectionManager.getCollection().removeIf(org -> org.getId() == id);
            } else {
                LOGGER.severe("Не удалось удалить объект из базы данных.");
            }
            if (removed) {
                return new ExecutionResponse(true, "Элемент с ID " + id + " удалён из коллекции.");
            } else {
                return new ExecutionResponse(true, "Элемент с ID " + id + " не найден в коллекции или есть в базе данных.");
            }
        } catch (NumberFormatException | SQLException e) {
            return new ExecutionResponse(false, "ID должен быть целым числом: " + argStr);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
