package client.standartConsole;
import java.util.NoSuchElementException;
import java.lang.IllegalStateException;
import java.util.Scanner;

/**
 * This is my console class
 * @author I
 */
public class Console implements ConsoleIn, ConsoleOut {

    private static final String P = "> ";
    private static Scanner fileScanner = null;
    private static Scanner defScanner = new Scanner(System.in);

    /**
     * This is my method to print an object
     * @param object object to print
     * @return nothing
     */
    @Override
    public void print(Object object) {
        System.out.print(object);
    }

    /**
     * This is my method to print an object with a newline
     * @param object object to print
     * @return nothing
     */
    @Override
    public void println(Object object) {
        System.out.println(object);
    }

    /**
     * This is my method to print an error message
     * @param object error message to print
     * @return nothing
     */
    @Override
    public void printError(Object object) {
        System.err.println("Error: " + object);
    }

    /**
     * This is my method to read a line from input
     * @return input string
     */
    @Override
    public String readLine() throws NoSuchElementException, IllegalStateException {
        return (fileScanner!=null?fileScanner:defScanner).nextLine();
    }

    /**
     * This is my method to switch input to a file scanner
     * @param scanner scanner object for file input
     * @return nothing
     */
    @Override
    public void selectFileScanner(Scanner scanner) {
        this.fileScanner = scanner;
    }

    /**
     * This is my method to switch input to console scanner
     * @return nothing
     */
    @Override
    public void selectConsoleScanner() {
        this.fileScanner = null;
    }

    /**
     * This is my method to display a prompt
     * @return nothing
     */
    @Override
    public void prompt() {
        print(P);
    }
}