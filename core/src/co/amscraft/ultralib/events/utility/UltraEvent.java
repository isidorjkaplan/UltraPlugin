package co.amscraft.ultralib.events.utility;

import co.amscraft.ultralib.UltraLib;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Izzy on 2017-10-11.
 */
public abstract class UltraEvent extends Event {
    public UltraEvent() {
        super(!Bukkit.isPrimaryThread());
    }
    public UltraEvent dispatch() {
        try {
            UltraLib.getInstance().getServer().getPluginManager().callEvent(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
}
