package server;

import java.io.Serializable;

public class CommandResponse implements Serializable {
    private static final long serialVersionUID = 777L;
    private boolean success;   // Успешно ли выполнена команда
    private String message;

    public CommandResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
