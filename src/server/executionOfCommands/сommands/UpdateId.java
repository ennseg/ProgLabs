package server.executionOfCommands.сommands;

import server.CommandRequest;
import server.collectionAction.CollectionManager;
import server.collectionAction.DataBaseConnection;
import server.collectionAction.DataBaseManager;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;
import server.model.Organization;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

public class UpdateId extends CommandManager {
    private final CollectionManager collectionManager;
    private static final Logger LOGGER = Logger.getLogger(UpdateId.class.getName());
    private final DataBaseConnection dbconnection;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public UpdateId(CollectionManager collectionManager, DataBaseConnection dbconnection) {
        super("update", "обновить значение элемента коллекции по id");
        this.collectionManager = collectionManager;
        this.dbconnection = dbconnection;
    }

    public boolean organizationExists(DataBaseConnection dbconnection, int id) throws SQLException {
        String query = "SELECT 1 FROM Organization WHERE id = ?";
        try (
                Connection conn = dbconnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)
        ) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }


    @Override
    public ExecutionResponse apply(Object arguments) throws SQLException {
        if (arguments == null) {
            return new ExecutionResponse(false, "Команда update требует аргумент (объект Organization).");
        }

        if (!(arguments instanceof CommandRequest)) {
            return new ExecutionResponse(false, "Ошибка: ожидается CommandRequest в качестве аргумента.");
        }

        CommandRequest request = (CommandRequest) arguments;
        Object arg = request.getArgument();

        if (!(arg instanceof Organization)) {
            return new ExecutionResponse(false, "Аргумент команды update должен быть объектом Organization.");
        }

        Organization newOrg = (Organization) arg;
        int id = newOrg.getId();

        if (!organizationExists(dbconnection, id)) {
            return new ExecutionResponse(false, "Объект с данным id отсутствует в базе данных");
        }

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
            return new ExecutionResponse(false, "Ошибка: у вас нет прав для изменения этого объекта.");
        }

        lock.writeLock().lock();
        Connection conn = null;
        try {
            conn = dbconnection.getConnection();
            conn.setAutoCommit(false);

            int coordId;
            try (PreparedStatement coordStmt = conn.prepareStatement(
                    "UPDATE Coordinates SET x = ?, y = ? WHERE id = (SELECT coordinates_id FROM Organization WHERE id = ?) RETURNING id")) {
                coordStmt.setDouble(1, newOrg.getCoordinates().getX());
                coordStmt.setLong(2, newOrg.getCoordinates().getY());
                coordStmt.setInt(3, id);
                ResultSet coordRs = coordStmt.executeQuery();
                coordId = coordRs.next() ? coordRs.getInt("id") : -1;
                if (coordId == -1) throw new SQLException("Не удалось обновить Coordinates для ID: " + id);
            }

            Integer addressId = null;
            try (PreparedStatement getAddrStmt = conn.prepareStatement(
                    "SELECT address_id FROM Organization WHERE id = ?")) {
                getAddrStmt.setInt(1, id);
                ResultSet addrRs = getAddrStmt.executeQuery();
                if (addrRs.next()) {
                    addressId = addrRs.getInt("address_id");
                    if (addrRs.wasNull()) addressId = null;
                }
            }

            if (newOrg.getPostalAddress() != null) {
                if (addressId == null) {
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
                                if (locId == -1) throw new SQLException("Не удалось создать Location.");
                                addrStmt.setInt(3, locId);
                            }
                        } else {
                            addrStmt.setNull(3, Types.INTEGER);
                        }
                        ResultSet addrRs = addrStmt.executeQuery();
                        addressId = addrRs.next() ? addrRs.getInt("id") : null;
                        if (addressId == null) throw new SQLException("Не удалось создать Address.");
                    }
                } else {
                    try (PreparedStatement addrStmt = conn.prepareStatement(
                            "UPDATE Address SET street = ?, zip_code = ? WHERE id = ?")) {
                        addrStmt.setString(1, newOrg.getPostalAddress().getStreet());
                        addrStmt.setString(2, newOrg.getPostalAddress().getZipCode());
                        addrStmt.setInt(3, addressId);
                        addrStmt.executeUpdate();
                    }

                    if (newOrg.getPostalAddress().getTown() != null) {
                        try (PreparedStatement getLocStmt = conn.prepareStatement(
                                "SELECT location_id FROM Address WHERE id = ?")) {
                            getLocStmt.setInt(1, addressId);
                            ResultSet locRs = getLocStmt.executeQuery();
                            if (locRs.next()) {
                                int locationId = locRs.getInt("location_id");
                                if (!locRs.wasNull()) {
                                    try (PreparedStatement locStmt = conn.prepareStatement(
                                            "UPDATE Location SET x = ?, y = ?, name = ? WHERE id = ?")) {
                                        locStmt.setInt(1, newOrg.getPostalAddress().getTown().getX());
                                        locStmt.setLong(2, newOrg.getPostalAddress().getTown().getY());
                                        locStmt.setString(3, newOrg.getPostalAddress().getTown().getName());
                                        locStmt.setInt(4, locationId);
                                        locStmt.executeUpdate();
                                    }
                                } else {
                                    try (PreparedStatement locStmt = conn.prepareStatement(
                                            "INSERT INTO Location (x, y, name) VALUES (?, ?, ?) RETURNING id")) {
                                        locStmt.setInt(1, newOrg.getPostalAddress().getTown().getX());
                                        locStmt.setLong(2, newOrg.getPostalAddress().getTown().getY());
                                        locStmt.setString(3, newOrg.getPostalAddress().getTown().getName());
                                        ResultSet locRs2 = locStmt.executeQuery();
                                        int locId = locRs2.next() ? locRs2.getInt("id") : -1;
                                        if (locId == -1) throw new SQLException("Не удалось создать Location.");
                                        try (PreparedStatement updateAddrStmt = conn.prepareStatement(
                                                "UPDATE Address SET location_id = ? WHERE id = ?")) {
                                            updateAddrStmt.setInt(1, locId);
                                            updateAddrStmt.setInt(2, addressId);
                                            updateAddrStmt.executeUpdate();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (addressId != null) {
                try (PreparedStatement delAddrStmt = conn.prepareStatement(
                        "DELETE FROM Address WHERE id = ?")) {
                    delAddrStmt.setInt(1, addressId);
                    delAddrStmt.executeUpdate();
                }
                addressId = null;
            }

            try (PreparedStatement orgStmt = conn.prepareStatement(
                    "UPDATE Organization SET name = ?, coordinates_id = ?, creation_date = ?, annual_turnover = ?, full_name = ?, employees_count = ?, type = ?::organizationtype, address_id = ?, user_id = ? WHERE id = ?")) {
                orgStmt.setString(1, newOrg.getName());
                orgStmt.setInt(2, coordId);
                orgStmt.setTimestamp(3, Timestamp.valueOf(newOrg.getCreationDate() != null ? newOrg.getCreationDate() : LocalDateTime.now()));
                orgStmt.setDouble(4, newOrg.getAnnualTurnover());
                orgStmt.setString(5, newOrg.getFullName());
                orgStmt.setLong(6, newOrg.getEmployeesCount());
                orgStmt.setString(7, newOrg.getType().toString());
                orgStmt.setObject(8, addressId, Types.INTEGER);
                orgStmt.setInt(9, userId);
                orgStmt.setInt(10, id);
                int rowsAffected = orgStmt.executeUpdate();
                if (rowsAffected == 0) throw new SQLException("Organization с ID " + id + " не найдена.");
            }

            conn.commit();

            boolean removed = collectionManager.getCollection().removeIf(org -> org.getId() == id);
            collectionManager.getCollection().add(newOrg);

            return new ExecutionResponse(true, "Элемент с ID " + id + " успешно обновлён.");

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    LOGGER.warning("Транзакция откатана.");
                } catch (SQLException rollbackEx) {
                    LOGGER.severe("Ошибка при откате транзакции: " + rollbackEx.getMessage());
                }
            }
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
            lock.writeLock().unlock();
        }
    }
}
