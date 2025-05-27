package client.standartConsole;
import java.util.Scanner;

/**
 * This is my console input interface
 * @author I
 */
public interface ConsoleIn {
    /**
     * This is my method to read a line from input
     * @return input string
     */
    String readLine();

    /**
     * This is my method to switch input to a file scanner
     * @param obj scanner object for file input
     * @return nothing
     */
    void selectFileScanner(Scanner obj);

    /**
     * This is my method to switch input to console scanner
     * @return nothing
     */
    void selectConsoleScanner();
}