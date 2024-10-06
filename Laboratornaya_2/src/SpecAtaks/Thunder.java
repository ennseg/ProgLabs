package SpecAtaks;

import ru.ifmo.se.pokemon.*;

public class Thunder extends SpecialMove {
    public Thunder() {
        super(Type.ELECTRIC, 110, 70);
    }

    @Override
    protected void applyOppEffects(Pokemon p) {
        p.setCondition(new Effect().chance(0.3).turns(0).condition(Status.PARALYZE));
    }

    @Override
    protected String describe() {
        return "использует атаку Thunder";
    }
}
