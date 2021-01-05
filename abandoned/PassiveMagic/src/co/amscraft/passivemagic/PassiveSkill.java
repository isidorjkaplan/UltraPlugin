package co.amscraft.passivemagic;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultramagic.Spell;

public class PassiveSkill extends UltraObject {
    private Spell spell;
    private double delay;
    public enum Condition {
        NONE, SHIFTING, IN_WATER, IN_AIR, JUMP, DOUBLE_JUMP, SPRINT, SWIM
    }
}
