package co.amscraft.ultramagic.main.actions;


import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * Created by Izzy on 2017-09-21.
 */
public class TeleportAction extends Action {

    public Mode mode = Mode.CASTER_TO_TARGET;

    @Override
    public void run(SpellInstance spell, Target input, Target caster) {
        caster = spell.CASTER;
        Target origin = mode.equals(Mode.CASTER_TO_TARGET) ? caster : input;
        Target target = mode.equals(Mode.CASTER_TO_TARGET) ? input : caster;
        if (origin.getObject() instanceof Entity && mode != Mode.SWITCH) {
            Location loc = target.getLocation();
            loc.setYaw(origin.getEyeLocation().getYaw());
            loc.setPitch(origin.getEyeLocation().getPitch());
            ((Entity) origin.getObject()).teleport(target.getLocation());
        } else {
            origin = spell.CASTER;
            Location targetLoc = target.getLocation();
            Location originLoc = origin.getLocation();
            if (target.getObject() instanceof Entity) {
                ((Entity) target.getObject()).teleport(originLoc);
            }
            if (origin.getObject() instanceof Entity) {
                ((Entity) origin.getObject()).teleport(targetLoc);
            }
        }
    }

    public enum Mode {
        TARGET_TO_CASTER, CASTER_TO_TARGET, SWITCH
    }

}
