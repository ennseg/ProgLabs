package server.executionOfCommands.сommands;

import server.CommandRequest;
import server.collectionAction.CollectionManager;
import server.collectionAction.DataBaseConnection;
import server.collectionAction.DataBaseManager;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import server.model.Organization;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.*;
import java.util.logging.Logger;

public class RemoveGreater extends CommandManager {
    private final CollectionManager collectionManager;
    private static final Logger LOGGER = Logger.getLogger(RemoveGreater.class.getName());
    private final DataBaseConnection dbconnection;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public RemoveGreater(CollectionManager collectionManager, DataBaseConnection dbconnection) {
        super("remove_greater", "удалить из коллекции все элементы, большие чем заданный (по ID)");
        this.collectionManager = collectionManager;
        this.dbconnection = dbconnection;
    }

    @Override
    public ExecutionResponse apply(Object arguments) throws SQLException {
        if (arguments == null || (arguments instanceof String && ((String) arguments).trim().isEmpty())) {
            return new ExecutionResponse(false, "Команда remove_greater требует аргумент (ID).");
        }

        CommandRequest request = (CommandRequest) arguments;
        String argStr = request.getArgument().toString();
        int threshId = Integer.parseInt(argStr.trim());

        String username = request.getUsername();
        if (username == null || username.trim().isEmpty()) {
            return new ExecutionResponse(false, "Ошибка: логин пользователя не указан.");
        }

        DataBaseManager dbManager = new DataBaseManager(dbconnection);
        int userId = dbManager.getUserIdFromUsername(username, dbconnection);
        if (userId == -1) {
            return new ExecutionResponse(false, "Ошибка: пользователь не найден в базе данных.");
        }

        if (!dbManager.hasAccess(threshId, userId)) {
            return new ExecutionResponse(false, "Ошибка: у вас нет прав для удаления этого объекта.");
        }

        lock.writeLock().lock();
        try {
            if (collectionManager.getCollection().isEmpty()) {
                return new ExecutionResponse(true, "Коллекция пуста, удалять нечего.");
            }

            List<Integer> idsToRemove = new ArrayList<>();
            for (Organization org : collectionManager.getCollection()) {
                if (org.getUserId() == userId && org.getId() > threshId) {
                    idsToRemove.add(org.getId());
                }
            }

            for (int id : idsToRemove) {
                String sql = "DELETE FROM Organization WHERE id = ? AND user_id = ?";
                try (Connection conn = dbconnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    stmt.setInt(2, userId);
                    stmt.executeUpdate();
                }
            }

            collectionManager.getCollection().removeIf(
                    org -> org.getId() > threshId && org.getUserId() == userId
            );

            return new ExecutionResponse(true, "Удалено " + idsToRemove.size() + " элементов, больших заданного.");

        } catch (SQLException e) {
            LOGGER.severe("Ошибка удаления элементов: " + e.getMessage());
            throw e;
        } finally {
            lock.writeLock().unlock();
        }
    }
}