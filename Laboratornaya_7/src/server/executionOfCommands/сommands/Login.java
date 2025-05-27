package server.executionOfCommands.сommands;

import server.CommandRequest;
import server.collectionAction.DataBaseConnection;
import server.collectionAction.DataBaseManager;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;

import java.sql.SQLException;

public class Login extends CommandManager {
    private final DataBaseConnection dbconnection;

    public Login(DataBaseConnection dbconnection) {
        super("login", "Войти в систему");
        this.dbconnection = dbconnection;
    }

    @Override
    public ExecutionResponse apply(Object argument) {
        DataBaseManager dbManager = new DataBaseManager(dbconnection);
        try {
            CommandRequest request = (CommandRequest) argument;
            String username = request.getUsername();
            String password = request.getPassword();

            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                return new ExecutionResponse(false, "Логин и пароль не могут быть пустыми.");
            }

            boolean isAuthenticated = dbManager.authenticateUser(username, password);
            if (isAuthenticated) {
                return new ExecutionResponse(true, "Авторизация успешна.");
            } else {
                return new ExecutionResponse(false, "Неверный логин или пароль.");
            }
        } catch (SQLException e) {
            return new ExecutionResponse(false, "Ошибка авторизации: " + e.getMessage());
        }
    }
}
