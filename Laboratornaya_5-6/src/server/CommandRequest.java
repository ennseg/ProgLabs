package server;

import java.io.Serializable;

public class CommandRequest implements Serializable {
    private static final long serialVersionUID = 777L;
    private String commandName;
    private Object argument;

    public CommandRequest(Object argument, String commandName) {
        this.argument = argument;
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

    public Object getArgument() {
        return argument;
    }
}
