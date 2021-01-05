package co.amscraft.ultramagic.main.effects;

import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.effects.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Izzy on 2017-10-18.
 */
public class SpiralEffect extends ParticleEffect {
    public double height = 5;

    @Override
    protected void run(Target target) {
        Location location = target.getLocation().clone();
        double r = 0;
        double a = 0;
        while (r <= radius) {
            double x = Math.cos(a) * radius;
            double z = Math.sin(a) * radius;
            location.add(x, 0, z);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (isColoured(particle)) {
                    player.spawnParticle(particle, location, 0, R, G, B, 1);
                } else {
                    player.spawnParticle(particle, location, 1, 0, 0, 0, speed);
                }
            }
            location.subtract(x, 0, z);
            r += 0.05;
            a += Math.PI / count;
        }
    }
}
