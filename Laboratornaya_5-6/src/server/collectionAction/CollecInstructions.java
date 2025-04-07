package server.collectionAction;

import java.util.Date;

/**
 * The interface Collec instructions.
 */
public interface CollecInstructions {
    /**
     * Gets last init time.
     *
     * @return the last init time
     */
    Date getLastInitTime();

    /**
     * Gets last save time.
     *
     * @return the last save time
     */
    Date getLastSaveTime();

    /**
     * Sets last init time.
     */
    void setLastInitTime();

    /**
     * Sets last save time.
     */
    void setLastSaveTime();
}
