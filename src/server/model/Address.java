package server.model;

import java.io.Serializable;

/**
 * This is my address class
 * @author I
 */
public class Address implements Validatable, Serializable {
    private static final long serialVersionUID = 777L;
    private String street; // Поле может быть null
    private String zipCode; // Длина строки не должна быть больше 22, Поле не может быть null
    private Location town; // Поле не может быть null

    /**
     * This is my constructor for address
     * @param street street name
     * @param zipCode postal code
     * @param town town location
     * @return nothing
     */
    public Address(String street, String zipCode, Location town) {
        this.street = street;
        this.zipCode = zipCode;
        this.town = town;
    }

    public String getStreet() {
        return street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public Location getTown() {
        return town;
    }

    /**
     * This is my method to get string representation of address
     * @return string representation
     */
    @Override
    public String toString() {
        return street + "; " + zipCode + "; " + town;
    }

    /**
     * This is my method to validate address
     * @return validation result
     */
    @Override
    public boolean valide() {
        return zipCode != null && zipCode.length() <= 22 && town != null && town.valide();
    }
}