package server.executionOfCommands;

/**
 * The type Command manager.
 */
public abstract class CommandManager implements Describable, Executable {
    private final String name;
    private final String description;


    /**
     * Instantiates a new Command manager.
     *
     * @param name        the name
     * @param description the description
     */
    public CommandManager(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CommandManager command = (CommandManager) obj;
        return name.equals(command.name) && description.equals(command.description);
    }

    @Override
    public int hashCode() {
        return name.hashCode() + description.hashCode();
    }

    @Override
    public String toString() {
        return "Command{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}


