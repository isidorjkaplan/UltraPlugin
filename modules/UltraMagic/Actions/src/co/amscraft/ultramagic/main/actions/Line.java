package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.AbstractProjectileAction;
import co.amscraft.ultramagic.exceptions.InvalidTargetException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;


public class Line extends AbstractProjectileAction {

    @FieldDescription(help = "The duration that the blocks will last for", unit = "seconds")
    public double duration = 2;
    @FieldDescription(help = "The block that it will create")
    public Material block = Material.AIR;
    @FieldDescription(help = "Weather or not the line stops when it selects a target or keeps going")
    public boolean stopOnHit = false;

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        Vector vector = caster.getEyeLocation().getDirection().multiply(getSpeed());
        final long CAST_TIME = System.currentTimeMillis();
        double distance = 0;
        Location location = caster.getEyeLocation().clone();
        do {
            double time = 1 / getFrequency();
            vector = updateVelocity(vector, spell, location, this.getDistance() - distance);
            Location old = location.clone();
            location.add(vector.getX() * time, vector.getY() * time, vector.getZ() * time);
            distance += old.distance(location);
            try {

                ChangeBlock.changeBlocks(spell, location, duration, this.block, (byte) 0);
                Target line = new Target(location.clone());
                this.playEffects(line);
                Target[] targets;
                if (protectSpellCaster) {
                    targets = Target.getTargetsInBox(this.getTargets(), location, getHitbox(), getHitbox(), getHitbox(), spell.CASTER, caster);
                } else {
                    targets = Target.getTargetsInBox(this.getTargets(), location, getHitbox(), getHitbox(), getHitbox(), caster);
                }
                for (Target t : targets) {
                    runActions(spell, t, line);
                }
                if (stopOnHit) {
                    break;
                }
            } catch (InvalidTargetException e) {

            }
            try {
                Thread.sleep(Math.round(1000 / getFrequency()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (distance < this.getDistance() && ((System.currentTimeMillis() - CAST_TIME) / 1000) < getLifetime());
    }
}
