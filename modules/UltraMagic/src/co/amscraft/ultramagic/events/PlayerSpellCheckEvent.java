package co.amscraft.ultramagic.events;

import co.amscraft.ultralib.events.utility.UltraEvent;
import co.amscraft.ultralib.player.PlayerUtility;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultramagic.MagicData;
import co.amscraft.ultramagic.Spell;
import org.bukkit.event.HandlerList;

/**
 * Created by Izzy on 2017-10-24.
 */
public class PlayerSpellCheckEvent extends UltraEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private UltraPlayer player;
    private Spell spell;
    private boolean hasSpell;

    public PlayerSpellCheckEvent(UltraPlayer player, Spell spell) {
        this.player = player;
        this.spell = spell;
        this.hasSpell = player.getData(MagicData.class).getSpellsRaw().contains(spell) || player.getData(PlayerUtility.class).hasPermission("UltraMagic.Spell." + spell.getName());
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public boolean hasSpell() {
        return hasSpell;
    }

    public void setHasSpell(boolean hasSpell) {
        this.hasSpell = hasSpell;
    }

    public Spell getSpell() {
        return this.spell;
    }

    public UltraPlayer getPlayer() {
        return this.player;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
