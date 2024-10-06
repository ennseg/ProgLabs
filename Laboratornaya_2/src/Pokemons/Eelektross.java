package Pokemons;

import ru.ifmo.se.pokemon.*;
import PhysAtaks.*;
import SpecAtaks.*;
import Stat.*;

public class Eelektross extends Eelektrik {
    public Eelektross(String name, int level) {

        super(name, level);

        super.setStats(85, 115, 80, 105,80,50);
        super.setType(Type.ELECTRIC);

        setMove(new ThunderWave(), new ChargeBeam(), new AcidSpray(), new Confide());
    }
}
