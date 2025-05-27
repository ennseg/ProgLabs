package server.executionOfCommands.сommands;


import server.CommandRequest;
import server.collectionAction.CollectionManager;
import server.collectionAction.DataBaseConnection;
import server.collectionAction.DataBaseManager;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import server.model.Organization;

import java.time.LocalDateTime;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import java.sql.*;

/**
 * The type Add.
 */
public class Add extends CommandManager {
    private final CollectionManager collectionManager;
    private static final Logger LOGGER = Logger.getLogger(Add.class.getName());
    private final DataBaseConnection dbconnection;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Instantiates a new Add.
     *
     * @param collectionManager the collection manager
     */
    public Add(CollectionManager collectionManager, DataBaseConnection dbconnection) {
        super("add", "добавить новый элемент в коллекцию");
        this.collectionManager = collectionManager;
        this.dbconnection = dbconnection;
    }

    public boolean organizationExists(DataBaseConnection dbconnection, int id) throws SQLException {
        String query = "SELECT 1 FROM Organization WHERE id = ?";
        try (Connection conn = dbconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }


    @Override
    public ExecutionResponse apply(Object arguments) {
        if (!(arguments instanceof CommandRequest)) {
            return new ExecutionResponse(false, "Ошибка: ожидается CommandRequest в качестве аргумента.");
        }

        CommandRequest request = (CommandRequest) arguments;
        Object arg = request.getArgument();

        if (!(arg instanceof Organization)) {
            return new ExecutionResponse(false, "Команда add требует объект Organization в качестве аргумента.");
        }

        lock.writeLock().lock();
        try {
            Organization newOrg = (Organization) arg;
            newOrg.setCreationDate(LocalDateTime.now());

            String username = ((CommandRequest) arguments).getUsername();
            if (username == null || username.trim().isEmpty()) {
                return new ExecutionResponse(false, "Ошибка: логин пользователя не указан.");
            }

            DataBaseManager dbManager = new DataBaseManager(dbconnection);
            int userId = dbManager.getUserIdFromUsername(username, dbconnection);
            if (userId == -1) {
                return new ExecutionResponse(false, "Ошибка: пользователь не найден в базе данных.");
            }
            newOrg.setUserId(userId);

            Connection conn = null;
            try {
                conn = dbconnection.getConnection();
                conn.setAutoCommit(false);

                int coordId;
                try (PreparedStatement coordStmt = conn.prepareStatement(
                        "INSERT INTO Coordinates (x, y) VALUES (?, ?) RETURNING id")) {
                    coordStmt.setDouble(1, newOrg.getCoordinates().getX());
                    coordStmt.setLong(2, newOrg.getCoordinates().getY());
                    ResultSet coordRs = coordStmt.executeQuery();
                    coordId = coordRs.next() ? coordRs.getInt("id") : -1;
                    if (coordId == -1) {
                        throw new SQLException("Не удалось создать запись в таблице Coordinates.");
                    }
                }

                Integer addressId = null;
                if (newOrg.getPostalAddress() != null) {
                    try (PreparedStatement addrStmt = conn.prepareStatement(
                            "INSERT INTO Address (street, zip_code, location_id) VALUES (?, ?, ?) RETURNING id")) {
                        addrStmt.setString(1, newOrg.getPostalAddress().getStreet());
                        addrStmt.setString(2, newOrg.getPostalAddress().getZipCode());
                        if (newOrg.getPostalAddress().getTown() != null) {
                            try (PreparedStatement locStmt = conn.prepareStatement(
                                    "INSERT INTO Location (x, y, name) VALUES (?, ?, ?) RETURNING id")) {
                                locStmt.setInt(1, newOrg.getPostalAddress().getTown().getX());
                                locStmt.setLong(2, newOrg.getPostalAddress().getTown().getY());
                                locStmt.setString(3, newOrg.getPostalAddress().getTown().getName());
                                ResultSet locRs = locStmt.executeQuery();
                                int locId = locRs.next() ? locRs.getInt("id") : -1;
                                if (locId == -1) {
                                    throw new SQLException("Не удалось создать запись в таблице Location.");
                                }
                                addrStmt.setInt(3, locId);
                            }
                        } else {
                            addrStmt.setNull(3, Types.INTEGER);
                        }
                        ResultSet addrRs = addrStmt.executeQuery();
                        addressId = addrRs.next() ? addrRs.getInt("id") : null;
                        if (addressId == null) {
                            throw new SQLException("Не удалось создать запись в таблице Address.");
                        }
                    }
                }

                try (PreparedStatement orgStmt = conn.prepareStatement(
                        "INSERT INTO Organization (name, coordinates_id, creation_date, annual_turnover, full_name, employees_count, type, address_id, user_id) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?::organizationtype, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    orgStmt.setString(1, newOrg.getName());
                    orgStmt.setInt(2, coordId);
                    orgStmt.setTimestamp(3, Timestamp.valueOf(newOrg.getCreationDate() != null ? newOrg.getCreationDate() : LocalDateTime.now()));
                    orgStmt.setDouble(4, newOrg.getAnnualTurnover());
                    orgStmt.setString(5, newOrg.getFullName());
                    orgStmt.setLong(6, newOrg.getEmployeesCount());
                    orgStmt.setString(7, newOrg.getType().toString());
                    orgStmt.setObject(8, addressId, Types.INTEGER);
                    orgStmt.setInt(9, userId);
                    orgStmt.executeUpdate();

                    try (ResultSet generatedKeys = orgStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int generatedId = generatedKeys.getInt(1);
                            newOrg.setId(generatedId);
                        } else {
                            throw new SQLException("Ошибка при получении сгенерированного ID");
                        }
                    }
                }

                conn.commit();
            } catch (SQLException e) {
                if (conn != null) {
                    try {
                        conn.rollback();
                        LOGGER.info("Транзакция откатана из-за ошибки.");
                    } catch (SQLException rollbackEx) {
                        LOGGER.severe("Ошибка при откате транзакции: " + rollbackEx.getMessage());
                    }
                }
                LOGGER.severe("Ошибка добавления объекта в базу: " + e.getMessage());
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

            if (organizationExists(dbconnection, newOrg.getId())) {
                collectionManager.getCollection().add(newOrg);
            } else {
                LOGGER.severe("Не удалось успешно добавить элемент в базу данных, обновление памяти отменено.");
            }
            return new ExecutionResponse(true, "Элемент успешно добавлен в коллекцию.");
        } catch (Exception e) {
            return new ExecutionResponse(false, "Ошибка при добавлении элемента: " + e.getMessage());
        } finally {
            lock.writeLock().unlock();
        }
    }
}