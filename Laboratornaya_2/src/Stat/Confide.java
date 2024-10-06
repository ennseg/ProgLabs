package Stat;

import ru.ifmo.se.pokemon.*;

public class Confide extends StatusMove {
    public Confide() {
        super(Type.NORMAL,0,0);
    }

    @Override
    protected boolean checkAccuracy(Pokemon att, Pokemon def) {
        return true;
    }

    @Override
    protected void applyOppEffects(Pokemon p) {
        p.setCondition(new Effect().chance(1).turns(-1).stat(Stat.SPECIAL_ATTACK, -1));
    }

    @Override
    protected String describe() {
        return "использует атаку Confide";
    }
}
