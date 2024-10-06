package SpecAtaks;

import ru.ifmo.se.pokemon.*;

public class Absorb extends SpecialMove {
    public Absorb() {
        super(Type.GRASS, 20, 100);
    }

    @Override
    protected void applySelfDamage(Pokemon p, double damage) {
        p.setMod(Stat.HP, (int) (-0.5*damage));
    }

    @Override
    protected String describe() {
        return "использует атаку Absorb";
    }
}
