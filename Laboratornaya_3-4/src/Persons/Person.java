package Persons;

public abstract class Person {
    private String name;
    private String type;
    private int id;

    public Person(String name, String type, int id) {
        this.name = name;
        this.type = type;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return "Person: {"
                + "Parson name = '" + this.getName() + '\''
                + " Айди = " + this.hashCode()
                + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Person)) {
            return false;
        }
        Person c = (Person) o;
        return (id == c.id);
    }
}

interface Fear {
    String fear();
}

interface DonotNow {
    String donotNow();
}

interface DoIt {
    String doIt();
}

interface Sleep {
    String sleep();
}

interface Seet {
    String seet();
}

interface Freedom {
    String freedom();
}

interface Remind {
    String remind();
}

interface Put {
    String put();
}

interface Relax {
    String relax();
}

interface Shure {
    String shure();
}

interface DonotWaste {
    String donotWaste();
}

interface Walk {
    String walk();
}

interface Study {
    String study();
}