package co.amscraft.ultramagic.main.effects;

import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.effects.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Izzy on 2017-10-18.
 */
public class PillarEffect extends ParticleEffect {
    public double height = 5;

    @Override
    protected void run(Target target) {
        Location location = target.getLocation().clone();
        for (double i = 0; i <= height; i++) {
            for (double a = 0; a < Math.PI * 2; a += Math.PI / count) {
                double x = Math.cos(a) * radius;
                double z = Math.sin(a) * radius;
                location.add(x, i, z);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (isColoured(particle)) {
                        player.spawnParticle(particle, location, 0, R, G, B, 1);
                    } else {
                        player.spawnParticle(particle, location, 1, 0, 0, 0, speed);
                    }
                }
                location.subtract(x, i, z);
            }
        }
    }
}
