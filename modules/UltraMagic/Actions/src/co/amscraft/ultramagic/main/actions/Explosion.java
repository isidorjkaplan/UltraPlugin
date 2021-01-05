package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;
import org.bukkit.Location;

/**
 * Created by Izzy on 2017-06-06.
 */
public class Explosion extends Action {
    @FieldDescription(help = "The size of the explosion")
    public int size = 4;


    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        //  co.amscraft.ultramagic.main.actions.GiveItem.;
        if (target != null) {
            try {
                Location location = target.getLocation();
                location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), (float) this.size, true, false);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        //nextAction(context.target, context.origin, this, context.spellInstance);
        //nextAction(context);
    }

}
