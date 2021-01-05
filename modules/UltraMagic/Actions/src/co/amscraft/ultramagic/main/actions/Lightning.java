package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;

/**
 * Created by Izzy on 2017-10-15.
 */
public class Lightning extends Action {
    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        target.getLocation().getWorld().strikeLightning(target.getLocation());
        this.playEffects(target);
    }

}
