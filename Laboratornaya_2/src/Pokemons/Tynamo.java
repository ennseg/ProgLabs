package Pokemons;

import ru.ifmo.se.pokemon.*;
import PhysAtaks.*;
import SpecAtaks.*;
import Stat.*;

public class Tynamo extends Pokemon {
    public Tynamo(String name, int level) {

        super(name, level);

        setStats(35, 55,40,45,40,60);
        setType(Type.ELECTRIC);

        setMove(new ThunderWave(), new ChargeBeam());
    }
}

