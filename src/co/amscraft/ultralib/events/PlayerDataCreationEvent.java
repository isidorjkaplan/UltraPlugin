package co.amscraft.ultralib.events;

import co.amscraft.ultralib.events.utility.UltraEvent;
import co.amscraft.ultralib.player.PlayerData;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.event.HandlerList;

/**
 * Created by Izzy on 2017-08-31.
 */
public class PlayerDataCreationEvent extends UltraEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean canceled = false;
    private Class<? extends PlayerData> dataType;
    private UltraPlayer player;

    public PlayerDataCreationEvent(UltraPlayer player, Class<? extends PlayerData> dataType) {
        this.player = player;
        this.dataType = dataType;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    public Class<? extends PlayerData> getDataType() {
        return this.dataType;
    }

    public UltraPlayer getPlayer() {
        return this.player;
    }
}
