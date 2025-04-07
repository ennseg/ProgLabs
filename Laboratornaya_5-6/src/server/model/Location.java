package server.model;

import java.io.Serializable;

/**
 * This is my location class
 * @author I
 */
public class Location implements Validatable, Serializable {
    private static final long serialVersionUID = 777L;
    private Integer x; // Поле не может быть null
    private long y;
    private String name; // Поле не может быть null

    /**
     * This is my constructor for location
     * @param x x-coordinate
     * @param y y-coordinate
     * @param name location name
     * @return nothing
     */
    public Location(Integer x, long y, String name) {
        this.x = x;
        this.y = y;
        this.name = name;
    }

    /**
     * This is my method to get string representation of location
     * @return string representation
     */
    @Override
    public String toString() {
        return x + "; " + y + "; " + name;
    }

    /**
     * This is my method to validate location
     * @return validation result
     */
    @Override
    public boolean valide() {
        return x != null && name != null;
    }
}