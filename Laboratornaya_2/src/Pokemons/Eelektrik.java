package Pokemons;

import ru.ifmo.se.pokemon.*;
import PhysAtaks.*;
import SpecAtaks.*;
import Stat.*;

public class Eelektrik extends Tynamo {
    public Eelektrik(String name, int level) {

        super(name, level);

        super.setStats(65, 85, 70, 75,70,40);
        super.setType(Type.ELECTRIC);

        setMove(new ThunderWave(), new ChargeBeam(), new AcidSpray());
    }
}
