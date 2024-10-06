package Pokemons;

import ru.ifmo.se.pokemon.*;
import PhysAtaks.*;
import SpecAtaks.*;
import Stat.*;

public class Aerodactyl extends Pokemon {
    public Aerodactyl(java.lang.String name, int level) {

        super(name, level);

        setStats(80, 105, 65, 60, 75, 130);
        setType(Type.ROCK, Type.FLYING);

        setMove(new AerialAce(), new Rest(), new DoubleTeam(), new ScaryFace());

    }
}