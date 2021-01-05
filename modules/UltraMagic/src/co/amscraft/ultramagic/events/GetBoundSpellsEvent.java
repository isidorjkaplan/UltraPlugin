package co.amscraft.ultramagic.events;

import co.amscraft.ultralib.events.utility.UltraEvent;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultramagic.MagicData;
import co.amscraft.ultramagic.Spell;
import org.bukkit.event.HandlerList;

import java.util.Arrays;

/**
 * Created by Izzy on 2017-10-24.
 */
public class GetBoundSpellsEvent extends UltraEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private UltraPlayer player;
    private Spell[] spells = new Spell[10];

    public GetBoundSpellsEvent(UltraPlayer player) {
        this.player = player;
        this.spells = Arrays.copyOf(player.getData(MagicData.class).getBoundRaw(), 10);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public void set(int i, Spell spell) {
        if (i < 10) {
            spells[i] = spell;
        }
    }

    public UltraPlayer getPlayer() {
        return this.player;
    }

    public Spell[] getBoundSpells() {
        return Arrays.copyOf(this.spells, 10);
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
