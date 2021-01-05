package co.amscraft.ultramagic.main.effects;

import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.effects.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Izzy on 2017-10-18.
 */
public class SphereEffect extends ParticleEffect {
    @Override
    protected void run(Target target) {
        Location location = target.getLocation().clone();
        for (double i = 0; i <= Math.PI; i += Math.PI / count) {
            double r = Math.sin(i) * radius;
            double y = Math.cos(i) * radius;
            for (double a = 0; a < Math.PI * 2; a += Math.PI / count) {
                double x = Math.cos(a) * r;
                double z = Math.sin(a) * r;
                location.add(x, y, z);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isColoured(particle)) {
                        player.spawnParticle(particle, location, 0, R, G, B, 1);
                    } else {
                        player.spawnParticle(particle, location, 1, 0, 0, 0, speed);
                    }
                }
                location.subtract(x, y, z);
            }
        }
    }
}
