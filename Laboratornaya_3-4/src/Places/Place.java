package Places;

public abstract class Place {
    private String name;
    private String property;
    private int id;

    public Place(String name, String property, int id) {
        this.name = name;
        this.property = property;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProperty() {
        return this.property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public record PlaceObj(String name, String property, int id) {}
}

