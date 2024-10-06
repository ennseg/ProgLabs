package SpecAtaks;

import ru.ifmo.se.pokemon.*;

public class ChargeBeam extends SpecialMove {
    public ChargeBeam(){
        super(Type.ELECTRIC, 50, 90);
    }

    @Override
    protected void applySelfEffects(Pokemon p) {
        p.setCondition(new Effect().chance(0.7).turns(-1).stat(Stat.SPECIAL_ATTACK, 1));
    }

    @Override
    protected String describe() {
        return "использует атаку Charge Beam";
    }
}
