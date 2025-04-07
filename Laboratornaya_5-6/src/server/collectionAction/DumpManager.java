package server.collectionAction;

import server.model.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashSet;

/**
 * This is my dump manager class
 * @author I
 */
public class DumpManager implements ParseCollection {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");

    /**
     * This is my method to load collection from file
     * @param fileName name of the file to load from
     * @param collection collection to load into
     * @return nothing
     */
    @Override
    public void load(String fileName, LinkedHashSet<Organization> collection) {
        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(file);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                Organization organization = parseOrganizationFromCsv(line);
                if (organization != null && organization.valide()) {
                    collection.add(organization);
                } else {
                    System.err.println("Пропущена некорректная строка в CSV: " + line);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Файл не найден: " + fileName);
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + fileName + " — " + e.getMessage());
        }
    }

    /**
     * This is my method to save collection to file
     * @param fileName name of the file to save to
     * @param collection collection to save
     * @return nothing
     */
    @Override
    public void save(String fileName, LinkedHashSet<Organization> collection) throws IOException {
        File file = new File(fileName);
        try (PrintWriter writer = new PrintWriter(file)) {
            for (Organization organization : collection) {
                writer.println(convertOrganizationToCsv(organization));
            }
        } catch (FileNotFoundException e) {
            throw new IOException("Не удалось открыть файл для записи: " + fileName, e);
        }
    }

    /**
     * This is my method to parse organization from CSV line
     * @param csvLine CSV line to parse
     * @return parsed organization or null
     */
    private Organization parseOrganizationFromCsv(String csvLine) {
        String[] parts = csvLine.split(",");
        if (parts.length < 9) {
            return null;
        }

        try {
            int id = Integer.parseInt(parts[0].trim());
            while (id <= CollectionManager.getNowId()) {id++;}
            String name = parts[1].trim();
            if (name.isEmpty()) return null;

            String[] coords = parts[2].split(";");
            if (coords.length != 2) return null;
            double x = Double.parseDouble(coords[0].trim());
            long y = Long.parseLong(coords[1].trim());
            if (y > 260) return null;
            Coordinates coordinates = new Coordinates(x, y);

            LocalDateTime creationDate = LocalDateTime.parse(parts[3].trim(), DATE_FORMATTER);

            Double annualTurnover = parts[4].trim().isEmpty() ? null : Double.parseDouble(parts[4].trim());
            if (annualTurnover != null && annualTurnover <= 0) return null;

            String fullName = parts[5].trim().isEmpty() ? null : parts[5].trim();
            long employeesCount = Long.parseLong(parts[6].trim());
            if (employeesCount <= 0) return null;

            OrganizationType type = OrganizationType.valueOf(parts[7].trim().toUpperCase());

            String[] addressParts = parts[8].split(";");
            if (addressParts.length != 5) return null;
            String street = addressParts[0].trim().isEmpty() ? null : addressParts[0].trim();
            String zipCode = addressParts[1].trim();
            if (zipCode.length() > 22) return null;
            Integer townX = Integer.parseInt(addressParts[2].trim());
            long townY = Long.parseLong(addressParts[3].trim());
            String townName = addressParts[4].trim();
            if (townX == null || townName.isEmpty()) return null;
            Location town = new Location(townX, townY, townName);
            Address postalAddress = new Address(street, zipCode, town);

            if (id >= CollectionManager.getNowId()) {
                CollectionManager.setNowId(id + 1);
            }

            Organization org = new Organization(id, name, coordinates, creationDate, annualTurnover, fullName, employeesCount, type, postalAddress);
            return org.valide() ? org : null;
        } catch (IllegalArgumentException | DateTimeParseException e) {
            return null;
        }
    }

    /**
     * This is my method to convert organization to CSV string
     * @param org organization to convert
     * @return CSV string representation
     */
    private String convertOrganizationToCsv(Organization org) {
        StringBuilder csv = new StringBuilder();
        csv.append(org.getId()).append(",");
        csv.append(org.getName()).append(",");
        csv.append(org.getCoordinates().toString()).append(",");
        csv.append(DATE_FORMATTER.format(org.getCreationDate())).append(",");
        csv.append(org.getAnnualTurnover() != null ? org.getAnnualTurnover() : "").append(",");
        csv.append(org.getFullName() != null ? org.getFullName() : "").append(",");
        csv.append(org.getEmployeesCount()).append(",");
        csv.append(org.getType()).append(",");
        csv.append(org.getPostalAddress().toString());

        return csv.toString();
    }
}