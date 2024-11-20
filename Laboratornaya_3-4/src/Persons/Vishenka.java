package Persons;

public class Vishenka extends Person implements DoIt, DonotNow, DonotWaste, Fear, Freedom, Study, Walk{

    public Vishenka(String name, String type, int id) {
        super(name, type, id);
    }

    @Override
    public String doIt() {
        return "за что ему приняться";
    }

    @Override
    public String donotNow() {
        return "прямо не знал";
    }

    @Override
    public String donotWaste() {
        return "не теряет времени даром";
    }

    @Override
    public String fear() {
        return "боясь";
    }

    @Override
    public String freedom() {
        return "был свободен";
    }

    @Override
    public String study() {
        return "усваивает полезные наставления";
    }

    @Override
    public String walk() {
        return "гуляя по";
    }
}
