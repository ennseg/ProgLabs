package server.model;

/**
 * This is my organization type enum
 * @author I
 */
public enum OrganizationType {
    PUBLIC,
    TRUST,
    PRIVATE_LIMITED_COMPANY;

    /**
     * This is my method to get all organization type names
     * @return string of all type names
     */
    public static String names() {
        StringBuilder nameList = new StringBuilder();
        for (var orgType : values()) {
            nameList.append(orgType.name()).append(", ");
        }
        return nameList.substring(0, nameList.length()-2);
    }
}