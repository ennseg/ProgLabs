package SpecAtaks;

import ru.ifmo.se.pokemon.*;

public class AcidSpray extends SpecialMove {
    public AcidSpray() {
        super(Type.POISON, 40, 100);
    }

    @Override
    protected void applySelfEffects(Pokemon p) {
        p.setCondition(new Effect().chance(1).turns(-1).stat(Stat.SPECIAL_DEFENSE, -2));
    }

    @Override
    protected String describe() {
        return "использует атаку Acid Spraym";
    }
}
