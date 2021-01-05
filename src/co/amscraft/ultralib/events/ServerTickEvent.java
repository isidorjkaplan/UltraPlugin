package co.amscraft.ultralib.events;

import co.amscraft.ultralib.events.utility.UltraEvent;
import org.bukkit.event.HandlerList;

/**
 * Created by Izzy on 2017-10-11.
 */
public class ServerTickEvent extends UltraEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
