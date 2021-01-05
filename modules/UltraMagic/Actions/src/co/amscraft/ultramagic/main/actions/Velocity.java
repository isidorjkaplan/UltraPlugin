package co.amscraft.ultramagic.main.actions;


import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.AbstractProjectileAction;
import co.amscraft.ultramagic.actions.Action;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;


/**
 * Created by Izzy on 2017-06-04.
 */
public class Velocity extends Action {
    @FieldDescription(unit = "m/s")
    public double velocity = 3;
    public AbstractProjectileAction.ControlMode controlMode = AbstractProjectileAction.ControlMode.DISABLED;

    @Override
    public void run(SpellInstance spell, Target target, Target origin) {
        if (target != null && target.getObject() instanceof Entity) {
            Entity entity = (Entity) target.getObject();
            Location entityLocation = entity.getLocation();
            Vector vector = null;
            //&& spell.CASTER.getObject() instanceof Player && ((Player) spell.CASTER.getObject()).isSneaking()
            if (controlMode == AbstractProjectileAction.ControlMode.ALWAYS || (controlMode == AbstractProjectileAction.ControlMode.SHIFTING && (spell.CASTER.getObject() instanceof Player && ((Player) spell.CASTER.getObject()).isSneaking()))) {
                vector = origin.getEyeLocation().getDirection();
            } else {
                vector = entityLocation.toVector().subtract(origin.getLocation().toVector());
                vector = entityLocation.clone().setDirection(vector).getDirection();

            }
            vector = vector.multiply(velocity);
            entity.setVelocity(entity.getVelocity().add(vector));
        }
    }
}
