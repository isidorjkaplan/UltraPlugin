package co.amscraft.ultralib.events;

import co.amscraft.ultralib.events.utility.UltraEvent;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.event.HandlerList;

/**
 * Created by Izzy on 2017-10-07.
 */
public class EditorRunResponseEvent extends UltraEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean canceled = false;
    private String key;
    private String value;
    private UltraPlayer player;

    public EditorRunResponseEvent(UltraPlayer player, String key, String value) {
        this.player = player;
        this.key = key;
        this.value = value;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public UltraPlayer getPlayer() {
        return this.player;
    }
}
