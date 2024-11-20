package Persons;


public class Petrushka extends Person implements Sleep, Seet, Remind, Put, Relax, Shure {

    public Petrushka(String name, String type, int id) {
        super(name, type, id);
    }

    @Override
    public String put() {
        return "были развешаны";
    }

    @Override
    public String relax() {
        return "отдыхая под";
    }

    @Override
    public String remind() {
        return "ухитрялся напомнить";
    }

    @Override
    public String seet() {
        return "посидеть в свое удовольствие за";
    }

    @Override
    public String shure() {
        return "был уверен";
    }

    @Override
    public String sleep() {
        return "поспать";
    }
}
