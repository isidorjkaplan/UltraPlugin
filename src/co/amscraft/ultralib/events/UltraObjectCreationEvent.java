package co.amscraft.ultralib.events;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.events.utility.UltraEvent;
import org.bukkit.event.HandlerList;

/**
 * Created by Izzy on 2017-10-11.
 */
public class UltraObjectCreationEvent extends UltraEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private UltraObject object;

    public UltraObjectCreationEvent(UltraObject object) {
        this.object = object;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public UltraObject getObject() {
        return this.object;
    }
}
