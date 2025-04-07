package server.collectionAction;

import server.model.*;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashSet;
import client.standartConsole.*;

/**
 * The type Collection manager.
 */
public class CollectionManager implements CollecInstructions {
    private static int NowId = 1;
    private LinkedHashSet<Organization> collection = new LinkedHashSet<Organization>();
    private Date lastSaveTime;
    private Date lastInitTime;
    private final DumpManager dumpManager;
    private String fileName;
    private Console console;

    /**
     * Instantiates a new Collection manager.
     *
     * @param dumpManager the dump manager
     * @param fileName    the file name
     * @param console     the console
     */
    public CollectionManager(DumpManager dumpManager, String fileName, Console console) throws IOException {
        this.dumpManager = dumpManager;
        this.lastSaveTime = null;
        this.lastInitTime = null;
        this.fileName = fileName;
        this.console = console;
        loadCollection();
    }

    /**
     * Gets collection.
     *
     * @return the collection
     */
    public LinkedHashSet<Organization> getCollection() {
        return collection;
    }

    /**
     * Save collection.
     *
     * @throws IOException the io exception
     */
    public void saveCollection() throws IOException {
        dumpManager.save(fileName, collection);
        setLastSaveTime();
    }

    /**
     * Load collection.
     *
     * @throws IOException the io exception
     */
    public void loadCollection() throws IOException {
        dumpManager.load(fileName, collection);
        setLastInitTime();
    }

    @Override
    public Date getLastInitTime() {
        return lastInitTime;
    }

    @Override
    public Date getLastSaveTime() {
        return lastSaveTime;
    }

    @Override
    public void setLastInitTime() {
        lastInitTime = new Date();
    }

    @Override
    public void setLastSaveTime() {
        lastSaveTime = new Date();
    }

    /**
     * Gets now id.
     *
     * @return the now id
     */
    public static int getNowId() {
        return NowId;
    }

    /**
     * Sets now id.
     *
     * @param newId the new id
     */
    public static void setNowId(int newId) {
        NowId = newId;
    }

    /**
     * Generate id int.
     *
     * @return the int
     */
    public int generateId() {
        int id = 1;
        int finalId = id;
        while (getCollection().stream().anyMatch(org -> org.getId() == finalId)) {
            id++;
        }
        return id;
    }

    @Override
    public String toString() {
        if (collection.isEmpty()) return "Коллекция пуста!";

        StringBuilder info = new StringBuilder();
        for (var org : collection) {
            info.append(org+"\n\n");
        }
        return info.toString().trim();
    }
}