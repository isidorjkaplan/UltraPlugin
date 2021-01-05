package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.ParentAction;
import org.bukkit.Location;

/**
 * Created by Izzy on 2017-06-17.
 */
public class TargetOffset extends ParentAction {
    public double xOffset = 0;
    public double yOffset = 0;
    public double zOffset = 0;
    public float pitchOffset = 0;
    public float yawOffset = 0;
    public double direction = 0;
    public boolean random = false;

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        if (target != null) {
            Location location = target.getLocation().clone().add(random ? Math.random() * xOffset : xOffset, random ? Math.random() * yOffset : yOffset, random ? Math.random() * zOffset : zOffset);
            location.setPitch(location.getPitch() + (float) (random ? Math.random() * pitchOffset : pitchOffset));
            location.setYaw(location.getYaw() + (float) (random ? Math.random() * yawOffset : yawOffset));
            double y = Math.sin(Math.toRadians(location.getPitch())) * -direction;
            double radius = Math.cos(Math.toRadians(location.getPitch())) * direction;
            double a = Math.toRadians(location.getYaw() + 90);
            double x = Math.cos(a) * radius;
            double z = Math.sin(a) * radius;
            location.add(x, y, z);
            try {
                caster = new Target(location);
                this.playEffects(target);
                runActions(spell, caster, target);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}
