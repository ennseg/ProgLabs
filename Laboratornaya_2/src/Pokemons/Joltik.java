package Pokemons;

import ru.ifmo.se.pokemon.*;
import PhysAtaks.*;
import SpecAtaks.*;
import Stat.*;

public class Joltik extends Pokemon {
    public Joltik(java.lang.String name, int level) {

        super(name, level);

        setStats(50, 47, 50, 57, 50, 65);
        setType(Type.BUG,Type.ELECTRIC);

        setMove(new Rest(), new EnergyBall(), new Absorb());
    }
}

