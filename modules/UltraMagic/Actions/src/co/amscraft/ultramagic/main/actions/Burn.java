package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;
import org.bukkit.entity.Entity;


/**
 * Created by Izzy on 2017-06-06.
 */
public class Burn extends Action {
    @FieldDescription(help = "The amount of time they burn for", unit = "seconds")
    public int time = 5;//seconds


    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        if (target != null && target.getObject() instanceof Entity) {
            Entity e = (Entity) target.getObject();
            e.setFireTicks((int) (time * 20));
        }
    }

}
