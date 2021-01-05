package co.amscraft.converter;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.modules.Module;
import co.amscraft.ultrastats.Stat;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Izzy on 2017-11-08.
 */
public class UltraPluginConverter extends Module {
    @Override
    public String[] getModuleDependancies() {
        return new String[]{"UltraMagic", "UltraStats"};
    }

    public void onEnable() {
        new BukkitRunnable() {
            public void run() {
                try {
                    SpellConveter.convertSpells();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    StatsConverter.convertStats();
                    for (Stat stat : Stat.getList(Stat.class)) {
                        if (stat.killMultiplier == 0) {
                            stat.killMultiplier = 5;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLater(UltraLib.getInstance(), 1);
    }

    @Override
    public void onDisable() {

    }
}
