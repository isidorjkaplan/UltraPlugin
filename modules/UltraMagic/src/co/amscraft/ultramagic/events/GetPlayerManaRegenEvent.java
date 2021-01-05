package co.amscraft.ultramagic.events;

import co.amscraft.ultralib.events.utility.UltraEvent;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultramagic.MagicData;
import org.bukkit.event.HandlerList;

/**
 * Created by Izzy on 2017-10-24.
 */
public class GetPlayerManaRegenEvent extends UltraEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private UltraPlayer player;
    private double manaRegen;

    public GetPlayerManaRegenEvent(UltraPlayer player) {
        this.player = player;
        this.manaRegen = player.getData(MagicData.class).getManaRegenRaw();
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }


    public double getManaRegen() {
        return manaRegen;
    }

    public void setManaRegen(double manaRegen) {
        this.manaRegen = manaRegen;
    }

    public UltraPlayer getPlayer() {
        return this.player;
    }



    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
