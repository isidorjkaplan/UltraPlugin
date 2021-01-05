package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;

public class EndSpell extends Action {
    @Override
    public void run(SpellInstance spellInstance, Target target, Target target1) {
        spellInstance.forceEnd();
    }
}
