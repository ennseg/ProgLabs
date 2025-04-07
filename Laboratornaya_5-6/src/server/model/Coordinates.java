package server.model;

import java.io.Serializable;

/**
 * This is my coordinates class
 * @author I
 */
public class Coordinates implements Validatable, Serializable {
    private static final long serialVersionUID = 777L;
    private double x;
    private long y; // Максимальное значение поля: 260

    /**
     * This is my constructor for coordinates
     * @param x x-coordinate
     * @param y y-coordinate
     * @return nothing
     */
    public Coordinates(double x, long y) {
        this.x = x;
        this.y = y;
    }

    /**
     * This is my method to get string representation of coordinates
     * @return string representation
     */
    @Override
    public String toString() {
        return x + "; " + y;
    }

    /**
     * This is my method to validate coordinates
     * @return validation result
     */
    @Override
    public boolean valide() {
        return y <= 260; // Проверка максимального значения
    }
}