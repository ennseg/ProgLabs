package server;

import java.io.Serializable;

public class CommandRequest implements Serializable {
    private static final long serialVersionUID = 777L;
    private String commandName;
    private Object argument;
    private final String username;
    private final String password;

    public CommandRequest(String username, String password, Object argument, String commandName) {
        this.argument = argument;
        this.commandName = commandName;
        this.username = username;
        this.password = password;
    }

    public String getCommandName() {
        return commandName;
    }
    public Object getArgument() {
        return argument;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
