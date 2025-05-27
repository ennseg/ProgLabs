package server.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import server.collectionAction.*;

/**
 * This is my organization class
 * @author I
 */
public class Organization implements Validatable, Serializable, Comparable<Organization> {
    private static final long serialVersionUID = 777L;
    private int id; // Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; // Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; // Поле не может быть null
    private java.time.LocalDateTime creationDate; // Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Double annualTurnover; // Поле может быть null, Значение поля должно быть больше 0
    private String fullName; // Поле может быть null
    private long employeesCount; // Значение поля должно быть больше 0
    private OrganizationType type; // Поле не может быть null
    private Address postalAddress; // Поле не может быть null
    private int userId;

    /**
     * This is my method to get organization ID
     * @return organization ID
     */
    public int getId() { return id; }

    /**
     * This is my method to get organization name
     * @return organization name
     */
    public String getName() { return name; }

    /**
     * This is my method to get organization coordinates
     * @return organization coordinates
     */
    public Coordinates getCoordinates() { return coordinates; }

    /**
     * This is my method to get organization creation date
     * @return creation date
     */
    public LocalDateTime getCreationDate() { return creationDate; }

    /**
     * This is my method to get organization annual turnover
     * @return annual turnover
     */
    public Double getAnnualTurnover() { return annualTurnover; }

    /**
     * This is my method to get organization full name
     * @return full name
     */
    public String getFullName() { return fullName; }

    /**
     * This is my method to get organization employees count
     * @return employees count
     */
    public long getEmployeesCount() { return employeesCount; }

    /**
     * This is my method to get organization type
     * @return organization type
     */
    public OrganizationType getType() { return type; }

    /**
     * This is my method to get organization postal address
     * @return postal address
     */
    public Address getPostalAddress() { return postalAddress; }

    public void setId(int id) { this.id = id; }

    public void setUserId(int userId) {this.userId = userId; }

    public int getUserId() {return userId; }

    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    /**
     * This is my constructor for organization from file
     * @param id organization ID
     * @param name organization name
     * @param coordinates organization coordinates
     * @param creationDate organization creation date
     * @param annualTurnover organization annual turnover
     * @param fullName organization full name
     * @param employeesCount organization employees count
     * @param type organization type
     * @param postalAddress organization postal address
     * @return nothing
     */
    public Organization(int id, String name, Coordinates coordinates, LocalDateTime creationDate, Double annualTurnover, String fullName, long employeesCount, OrganizationType type, Address postalAddress, int userId) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.annualTurnover = annualTurnover;
        this.fullName = fullName;
        this.employeesCount = employeesCount;
        this.type = type;
        this.postalAddress = postalAddress;
        this.userId = userId;
    }

    /**
     * This is my constructor for organization from user input
     * @param name organization name
     * @param coordinates organization coordinates
     * @param annualTurnover organization annual turnover
     * @param fullName organization full name
     * @param employeesCount organization employees count
     * @param type organization type
     * @param postalAddress organization postal address
     * @return nothing
     */
    public Organization(String name, Coordinates coordinates, Double annualTurnover, String fullName, long employeesCount, OrganizationType type, Address postalAddress, int userId) {
        this(CollectionManager.getNowId(), name, coordinates, LocalDateTime.now(), annualTurnover, fullName, employeesCount, type, postalAddress, userId);
    }

    /**
     * This is my method to get string representation of organization
     * @return string representation
     */
    @Override
    public String toString() {
        return "organization{\n" +
                "\"id\": " + id + "\n" +
                "\"name\": \"" + name + "\"\n" +
                "\"coordinates\": \"" + coordinates + "\"\n" +
                "\"creationDate\": \"" + creationDate + "\"\n" +
                "\"annualTurnover\": " + (annualTurnover == null ? "null\n" : "\""+annualTurnover+"\"\n") +
                "\"fullName\": " + (fullName == null ? "null\n" : "\""+fullName+"\"\n") +
                "\"employeesCount\": \"" + employeesCount + "\"\n" +
                "\"type\": \"" + type + "\"\n" +
                "\"postalAddress\": \"" + postalAddress + "\"\n" +
                "\"userId\": \"" + userId + "\"\n"
                + "}";
    }

    /**
     * This is my method to validate organization
     * @return validation result
     */
    @Override
    public boolean valide() {
        if (id <= 0) return false;
        if (name == null || name.isEmpty()) return false;
        if (creationDate == null) return false;
        if (coordinates == null || !coordinates.valide()) return false;
        if (annualTurnover != null && annualTurnover <= 0) return false;
        if (employeesCount <= 0) return false;
        if (type == null) return false;
        if (postalAddress == null) return false;
        if (userId < 0) return false;
        return true;
    }

    /**
     * This is my method to compare organizations
     * @param other organization to compare with
     * @return comparison result
     */
    @Override
    public int compareTo(Organization other) {
        return this.name.compareTo(other.name);
    }

    /**
     * This is my method to check equality of organizations
     * @param o object to compare with
     * @return equality result
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organization that = (Organization) o;
        return Objects.equals(id, that.id);
    }

    /**
     * This is my method to get hash code of organization
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, name, coordinates, creationDate, annualTurnover, fullName, employeesCount, type, postalAddress, userId);
    }
}