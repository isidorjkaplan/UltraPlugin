package co.amscraft.ultramagic.main.actions;


import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.SpellThread;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.AbstractProjectileAction;
import co.amscraft.ultramagic.exceptions.InvalidTargetException;
import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Created by Izzy on 2017-06-02.
 */
public class CustomProjectileAction extends AbstractProjectileAction {


    @Override
    public void run(SpellInstance spell, Target target, Target origin) {
        Location loc = target != null ? target.getEyeLocation() : origin.getEyeLocation();
        if (this.getYaw() != 0) {
            loc.setYaw(getYaw());
        }
        if (this.getPitch() != 0) {
            loc.setPitch(getPitch());
        }
        SpellThread thread = new SpellThread() {
            public void cast() {
                tic(loc, spell);
            }
        };
        spell.getThreads().add(thread);
        thread.runTaskAsynchronously(UltraLib.getInstance());
    }


    private void tic(Location location, final SpellInstance spell) {
        long start = System.currentTimeMillis();
        double distance = 0;
        Target target = null;
        boolean run;
        Vector velocity = location.getDirection().multiply(getSpeed());
        do {
            Location old = location.clone();
            velocity = this.updateVelocity(velocity, spell, location, this.getDistance() - distance);
            location.add(velocity.getX() / getFrequency(), velocity.getY() / getFrequency(), velocity.getZ() / getFrequency());
            location.setDirection(velocity);
            //this.updateLocation(location, spell.CASTER, start, spell);
            distance += old.distance(location);
            try {
                target = Target.getTarget(getTargets(), location, this.getHitbox(), spell.CASTER);
            } catch (InvalidTargetException e) {

            }
            try {
                this.playEffects(new Target(location.clone()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            run = (target == null && distance < this.getDistance()) && System.currentTimeMillis() - start <= (getLifetime() * 1000) && (getBlockAction() == BlockAction.IGNORE || (getBlockAction() == BlockAction.STOP && !location.getBlock().getType().isSolid()));
            if (run) {
                try {
                    Thread.sleep(Math.round(1000 / getFrequency()));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        while (run);
        finish(spell, target, location);
    }


    private void finish(SpellInstance spell, Target target, Location location) {
        if (target == null && isMis()) {
            try {
                target = new Target(location);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (target != null) {
            try {
                runActions(spell, target, new Target(location));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
