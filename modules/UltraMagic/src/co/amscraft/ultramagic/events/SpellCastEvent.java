package co.amscraft.ultramagic.events;

import co.amscraft.ultralib.events.utility.CancelableEvent;
import co.amscraft.ultramagic.Spell;
import co.amscraft.ultramagic.Target;
import org.bukkit.event.HandlerList;

/**
 * Created by Izzy on 2017-10-19.
 */
public class SpellCastEvent extends CancelableEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private Spell spell;
    private Target caster;
    private String failMessage = "Your spell failed to cast!";

    public SpellCastEvent(Spell spell, Target caster) {
        this.spell = spell;
        this.caster = caster;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public String getFailMessage() {
        return this.failMessage;
    }

    public void setFailMessage(String message) {
        this.failMessage = message;
    }

    public Target getCaster() {
        return this.caster;
    }

    public Spell getSpell() {
        return this.spell;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
