package co.amscraft.ultramagic.events;

import co.amscraft.ultralib.events.utility.UltraEvent;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultramagic.MagicData;
import org.bukkit.event.HandlerList;

/**
 * Created by Izzy on 2017-10-24.
 */
public class GetPlayerManaEvent extends UltraEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private UltraPlayer player;
    private int mana;

    public GetPlayerManaEvent(UltraPlayer player) {
        this.player = player;
        this.mana = player.getData(MagicData.class).getMaxManaRaw();
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }


    public UltraPlayer getPlayer() {
        return this.player;
    }

    public int getMaxMana() {
        return this.mana;
    }

    public void setMaxMana(int mana) {
        if (mana >= 0) {
            this.mana = mana;
        }
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
