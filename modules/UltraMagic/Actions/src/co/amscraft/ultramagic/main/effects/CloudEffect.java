package co.amscraft.ultramagic.main.effects;

import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.effects.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Izzy on 2017-10-18.
 */
public class CloudEffect extends ParticleEffect {
    @Override
    protected void run(Target target) {
        Location location = target.getLocation().clone();
        for (int i = 0; i < count; i++) {
            double x = (Math.random() * radius * 2) - radius;
            double y = (Math.random() * radius * 2) - radius;
            double z = (Math.random() * radius * 2) - radius;
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
