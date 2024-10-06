package Pokemons;

import ru.ifmo.se.pokemon.*;
import PhysAtaks.*;
import SpecAtaks.*;
import Stat.*;

public class Galvantula extends Joltik {
    public Galvantula(String name, int level) {

        super(name, level);

        super.setStats(70, 77,60,97,60,108);
        super.setType(Type.BUG,Type.ELECTRIC);

        setMove(new Rest(), new EnergyBall(), new Absorb(), new Thunder());
    }
}
