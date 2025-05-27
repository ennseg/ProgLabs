package server.collectionAction;

import java.sql.*;
import java.util.logging.*;

public class DataBaseConnection {
    private final String URL = "jdbc:postgresql://pg/studs";
    private final String my_login = "s467530";
    private final String my_password = "Gc7uKesSiK7P4lVW";
    private static final Logger LOGGER = Logger.getLogger(DataBaseConnection.class.getName());

    public Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, my_login, my_password);
        } catch (SQLException e) {
            LOGGER.severe("Ошибка подключения к базе данных: " + e.getMessage());
            throw e;
        }
    }
}
