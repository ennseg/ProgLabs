package server.collectionAction;

import server.model.*;
import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.logging.Logger;
import java.util.concurrent.locks.*;

/**
 * The type Collection manager.
 */
public class CollectionManager implements CollecInstructions {
    private static final Logger LOGGER = Logger.getLogger(CollectionManager.class.getName());
    private static int NowId = 1;
    private final LinkedHashSet<Organization> collection = new LinkedHashSet<Organization>();
    private Date lastSaveTime;
    private Date lastInitTime;
    private final DataBaseManager dataBaseManager;
    private final DataBaseConnection dbConnection;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Instantiates a new Collection manager.
     *
     * @param dbConnection the db connection
     * @throws IOException  the io exception
     * @throws SQLException the sql exception
     */
    public CollectionManager(DataBaseConnection dbConnection) throws IOException, SQLException {
        this.lastSaveTime = null;
        this.lastInitTime = null;
        this.dbConnection = dbConnection;
        this.dataBaseManager = new DataBaseManager(dbConnection);
        loadCollection();
    }

    /**
     * Gets collection.
     *
     * @return the collection
     */
    public LinkedHashSet<Organization> getCollection() {
        //lock.readLock().lock();
        //try {
            return collection;
        //} finally {
            //lock.readLock().unlock();
        //}
    }

    /**
     * Save collection.
     *
     * @throws IOException  the io exception
     * @throws SQLException the sql exception
     */
    public void saveCollection() throws IOException, SQLException {
    }

    /**
     * Load collection.
     *
     * @throws IOException  the io exception
     * @throws SQLException the sql exception
     */
    public void loadCollection() throws IOException, SQLException {
        lock.writeLock().lock();
        try {
            this.collection.clear();
            this.collection.addAll(dataBaseManager.load());
            LOGGER.info("Коллекция загружена в память: " + collection.size() + " элементов.");
        } finally {
            lock.writeLock().unlock();
        }
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