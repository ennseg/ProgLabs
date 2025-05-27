package client.standartConsole;

/**
 * This is my console output interface
 * @author I
 */
public interface ConsoleOut {
    /**
     * This is my method to print an object
     * @param object object to print
     * @return nothing
     */
    void print(Object object); // через toString

    /**
     * This is my method to print an object with a newline
     * @param object object to print
     * @return nothing
     */
    void println(Object object);

    /**
     * This is my method to print an error message
     * @param object error message to print
     * @return nothing
     */
    void printError(Object object);

    /**
     * This is my method to display a prompt
     * @return nothing
     */
    void prompt();
}