import Persons.*;
import Places.*;
import Items.*;

public class Main extends Data {
    public static void main(String[] args) {

            try {
                    Person per1 = new Petrushka(Names.Petrushka.getNameData(), Types.Senior.getTypeData(), 1);
                    Person per2 = new Vishenka(Names.Vishenka.getNameData(), Types.None.getTypeData(), 2);

                    if (per1 == null) {
                            throw new Exceptions.PersonNotFound("Персонаж " + Names.Petrushka.getNameData() + " не существует.");
                    } else if (per2 == null) {
                            throw new Exceptions.PersonNotFound("Персонаж " + Names.Vishenka.getNameData() + " не существует.");
                    }

                    Place pl1 = new Garden(Names.Garden.getNameData(), Names.Vishenka.getNameData(), 1);
                    Place pl2 = new Prison(Names.Prison.getNameData(), Names.Vishenka.getNameData(), 2);
                    Place pl3 = new Tree(Names.Tree.getNameData(), Names.Petrushka.getNameData(), 3);

                    if (pl1 == null) {
                            throw new Exceptions.PlaceNotFound("Место " + Names.Garden.getNameData() + " не найдено.");
                    } else if (pl2 == null) {
                            throw new Exceptions.PlaceNotFound("Место " + Names.Prison.getNameData() + " не найдено.");
                    } else if (pl3 == null) {
                            throw new Exceptions.PlaceNotFound("Место " + Names.Tree.getNameData() + " не найдено.");
                    }

                    Item it1 = new GrapeVodka(Names.GrapeVodka.getNameData(), Names.Petrushka.getNameData(), 1);
                    Item it2 = new Inscriptions(Names.Inscription.getNameData(), Names.Vishenka.getNameData(), 2);

                    if (it1 == null) {
                            throw new Exceptions.ItemNotFound("Предмет " + Names.GrapeVodka.getNameData() + " не найден.");
                    } else if (it2 == null) {
                            throw new Exceptions.ItemNotFound("Предмет " + Names.Inscription.getNameData() + " не найден.");
                    }

                    String[] output = new String[]{
                            ((Vishenka) per2).fear() + " " + pl2.getName() + ", " + per2.getName() + " " + ((Vishenka) per2).donotNow() + ", " + ((Vishenka) per2).doIt() + ".",
                            Data.text.get(0) + ", " + per1.getType() + " " + per1.getName() + " " + Data.text.get(1) + " " + ((Petrushka) per1).sleep() + " или " + ((Petrushka) per1).seet() + " " + it1.getName() + ".",
                            Data.text.get(2) + " " + per2.getName() + " " + ((Vishenka) per2).freedom() + ".",
                            Data.text.get(3) + " " + per1.getType() + " " + per1.getName() + " " + Data.text.get(4) + " " + ((Petrushka) per1).remind() + " " + per2.getName() + " " + Data.text.get(5) + ": " + Data.text.get(6) + " " + ((Petrushka) per1).put() + " " + Data.text.get(7) + " " + it2.getName() + ".",
                            Data.text.get(8) + " " + ((Petrushka) per1).sleep() + " " + Data.text.get(9) + ".",
                            ((Petrushka) per1).relax() + " " + pl3.getName() + ", " + Data.text.get(10) + " " + ((Petrushka) per1).shure() + ", " + Data.text.get(11) + " " + ((Vishenka) per2).donotWaste() + " " + Data.text.get(12) + ", " + ((Vishenka) per2).walk() + " " + pl1.getName() + ", " + ((Vishenka) per2).study() + "."
                    };

                    for (String line : output) {
                            try {
                                    System.out.println(line);
                            } catch (NullPointerException e) {
                                    System.out.println("Произошла ошибка: " + e.getMessage());
                            }
                    }

            } catch (Exceptions.PersonNotFound | Exceptions.PlaceNotFound | Exceptions.ItemNotFound e) {
                    System.out.println(e.getMessage());
            }
    }
}