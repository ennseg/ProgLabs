package server.collectionAction;

import server.PasswordHasher;
import server.model.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.logging.Logger;

public class DataBaseManager {
    private static final Logger LOGGER = Logger.getLogger(DataBaseManager.class.getName());
    private final DataBaseConnection dbconnection;

    public DataBaseManager(DataBaseConnection dbconnection) {
        this.dbconnection = dbconnection;
    }

    public void registerUser(String username, String password) throws SQLException {
        PasswordHasher.HashedPassword hashed = PasswordHasher.hashPassword(password);
        String hash = hashed.getHash();
        String salt = hashed.getSalt();

        String sql = "INSERT INTO users (username, password_hash, salt) VALUES (?, ?, ?)";
        try (Connection conn = dbconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, hash);
            stmt.setString(3, salt);
            stmt.executeUpdate();
        }
    }

    public boolean authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT password_hash, salt FROM users WHERE username = ?";
        try (Connection conn = dbconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    String storedSalt = rs.getString("salt");
                    return PasswordHasher.verifyPassword(password, storedHash, storedSalt);
                }
                return false;
            }
        }
    }

    public int getUserIdByOrganizationId(int orgId) throws SQLException {
        String sql = "SELECT user_id FROM Organization WHERE id = ?";
        try (Connection conn = dbconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orgId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        }
        return -1;
    }

    public int getUserIdFromUsername(String username, DataBaseConnection dbconnection) throws SQLException {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = dbconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1;
    }

    public String getUserNameFromUserId(int id, DataBaseConnection dbconnection) throws SQLException {
        String sql = "SELECT username FROM users WHERE id = ?";
        try (Connection conn = dbconnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        }
        return null;
    }

    public boolean hasAccess(int orgId, int userId) throws SQLException {
        int orgUserId = getUserIdByOrganizationId(orgId);
        return orgUserId != -1 && orgUserId == userId;
    }

    public LinkedHashSet<Organization> load() throws SQLException {
        LinkedHashSet<Organization> collection = new LinkedHashSet<>();
        try (Connection connection = dbconnection.getConnection();
             Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(
                     "SELECT o.*, c.x AS coord_x, c.y AS coord_y, a.street, a.zip_code, l.x AS loc_x, l.y AS loc_y, l.name AS loc_name " +
                             "FROM Organization o " +
                             "JOIN Coordinates c ON o.coordinates_id = c.id " +
                             "LEFT JOIN Address a ON o.address_id = a.id " +
                             "LEFT JOIN Location l ON a.location_id = l.id")) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double coordX = rs.getDouble("coord_x");
                long coordY = rs.getLong("coord_y");
                LocalDateTime creationDate = rs.getTimestamp("creation_date") != null ? rs.getTimestamp("creation_date").toLocalDateTime() : null;
                double annualTurnover = rs.getDouble("annual_turnover");
                String fullName = rs.getString("full_name");
                long employeesCount = rs.getLong("employees_count");
                OrganizationType type = OrganizationType.valueOf(rs.getString("type"));
                int userId = rs.getInt("user_id");

                Coordinates coordinates = new Coordinates(coordX, coordY);
                Location location = null;
                if (rs.getObject("loc_x") != null) {
                    location = new Location(rs.getInt("loc_x"), rs.getLong("loc_y"), rs.getString("loc_name"));
                }
                Address postalAddress = null;
                if (rs.getObject("street") != null) {
                    postalAddress = new Address(rs.getString("street"), rs.getString("zip_code"), location);
                }

                Organization org = new Organization(id, name, coordinates, creationDate, annualTurnover, fullName, employeesCount, type, postalAddress, userId);

                if (org.valide()) {
                    collection.add(org);
                } else {
                    LOGGER.warning("Объект с ID " + id + " не прошёл валидацию и был пропущен.");
                }
            }
            LOGGER.info("Коллекция загружена из базы: " + collection.size() + " элементов.");
        } catch (SQLException e) {
            LOGGER.severe("Ошибка загрузки коллекции из базы: " + e.getMessage());
            throw e;
        }
        return collection;
    }
}