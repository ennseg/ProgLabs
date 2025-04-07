/*import server.collectionAction.*;
import client.standartConsole.*;
import server.executionOfCommands.*;

/**
 * This is my main class
 * @author I
 */
/*public class Main {
    /**
     * This is my main method
     * @param args command line arguments
     * @return nothing
     */
    /*public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Ошибка: укажите имя CSV-файла как аргумент командной строки.");
            System.exit(1);
        }

        String fileName = args[0];
        Console console = new Console();
        DumpManager dumpManager = new DumpManager();
        CollectionManager collectionManager = new CollectionManager(dumpManager, fileName, console);

        try {
            collectionManager.loadCollection();
            console.println("Коллекция успешно загружена из файла: " + fileName);
        } catch (Exception e) {
            console.printError("Ошибка при загрузке коллекции из файла: " + e.getMessage());
            System.exit(1);
        }

        Runner runner = new Runner(console, collectionManager);
        console.println("Программа запущена. Введите команды ");
        //runner.interactiveMode();
    }
}*/