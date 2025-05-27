package server.executionOfCommands.сommands;

import server.CommandRequest;
import server.collectionAction.DataBaseConnection;
import server.collectionAction.DataBaseManager;
import server.executionOfCommands.CommandManager;
import server.executionOfCommands.ExecutionResponse;

import java.sql.SQLException;

public class Register extends CommandManager {
    private final DataBaseConnection dbconnection;

    public Register(DataBaseConnection dbconnection) {
        super("register", "Зарегистрировать нового пользователя");
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

            dbManager.registerUser(username, password);
            return new ExecutionResponse(true, "Пользователь успешно зарегистрирован.");
        } catch (SQLException e) {
            return new ExecutionResponse(false, "Ошибка регистрации: " + e.getMessage());
        }
    }
}
