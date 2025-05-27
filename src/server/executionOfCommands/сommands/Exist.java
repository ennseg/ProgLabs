package server.executionOfCommands.сommands;

import server.CommandRequest;
import server.collectionAction.CollectionManager;
import server.collectionAction.DataBaseConnection;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import server.model.Organization;
import server.collectionAction.DataBaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.*;
import java.util.logging.Logger;

public class Exist extends CommandManager {
    private final CollectionManager collectionManager;
    private static final Logger LOGGER = Logger.getLogger(Exist.class.getName());
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final DataBaseConnection dbconnection;

    public Exist(CollectionManager collectionManager, DataBaseConnection dbconnection) {
        super("exist", "Системная команда для проверки существования объекта");
        this.collectionManager = collectionManager;
        this.dbconnection = dbconnection;
    }

    @Override
    public ExecutionResponse apply(Object arguments) throws SQLException {
        if (!(arguments instanceof CommandRequest)) {
            return new ExecutionResponse(false, "Ошибка: ожидается CommandRequest в качестве аргумента.");
        }

        CommandRequest request = (CommandRequest) arguments;
        String arg = request.getArgument().toString();
        int id = Integer.parseInt(arg.trim());
        String username = request.getUsername();

        if (arg == null || arg.trim().isEmpty()) {
            return new ExecutionResponse(false, "Ошибка: требуется ID для проверки.");
        }

        try {
            if (id <= 0) {
                return new ExecutionResponse(false, "Ошибка: ID должен быть положительным числом.");
            }
        } catch (NumberFormatException e) {
            return new ExecutionResponse(false, "Ошибка: ID должен быть целым числом.");
        }

        if (username == null || username.trim().isEmpty()) {
            return new ExecutionResponse(false, "Ошибка: логин пользователя не указан.");
        }

        DataBaseManager dbManager = new DataBaseManager(dbconnection);
        int userId = dbManager.getUserIdFromUsername(username, dbconnection);
        if (userId == -1) {
            return new ExecutionResponse(false, "Ошибка: пользователь не найден в базе данных.");
        }

        String query = "SELECT 1 FROM Organization WHERE id = ?";
        boolean exists = false;
        try (Connection conn = dbconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                exists = rs.next();
            }
        }
        if (!exists) {
            return new ExecutionResponse(false, "Объект с ID " + id + " не существует.");
        }

        if (!dbManager.hasAccess(id, userId)) {
            return new ExecutionResponse(false, "У вас нет прав для редактирования объекта с ID " + id + ".");
        }

        return new ExecutionResponse(true, "Объект существует и у вас есть права на его редактирование.");
    }
}
