package server.collectionAction;
import server.model.*;
import java.io.IOException;
import java.util.LinkedHashSet;

/**
 * The interface Parse collection.
 */
public interface ParseCollection {
        /**
         * Load.
         *
         * @param fileName   the file name
         * @param collection the collection
         * @throws IOException the io exception
         */
        void load(String fileName, LinkedHashSet<Organization> collection) throws IOException;

        /**
         * Save.
         *
         * @param fileName   the file name
         * @param collection the collection
         * @throws IOException the io exception
         */
        void save(String fileName, LinkedHashSet<Organization> collection) throws IOException;
}
