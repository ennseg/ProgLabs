package Stat;

import ru.ifmo.se.pokemon.*;

public class Rest extends StatusMove {
    public Rest() {
        super(Type.PSYCHIC, 0,0);
    }

    @Override
    protected boolean checkAccuracy(Pokemon att, Pokemon def) {
        return true;
    }

    @Override
    protected void applySelfEffects(Pokemon p) {
        p.setCondition(new Effect().turns(2).condition(Status.SLEEP));
        p.setMod(Stat.HP, (int) -(p.getStat(Stat.HP) - p.getHP()));
    }

    @Override
    protected String describe() {
        return "использует атаку Rest";
    }
}
