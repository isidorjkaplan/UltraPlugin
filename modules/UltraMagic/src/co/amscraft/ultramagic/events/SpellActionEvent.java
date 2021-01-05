package co.amscraft.ultramagic.events;

import co.amscraft.ultralib.events.utility.CancelableEvent;
import co.amscraft.ultralib.events.utility.UltraEvent;
import co.amscraft.ultramagic.Spell;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;

public class SpellActionEvent extends CancelableEvent {
    private SpellInstance instance;
    private Action action;
    private Target target;
    private Target caster;
    private String errorMessage = "An action in the spell you cast was disabled!";

    public SpellActionEvent(SpellInstance spell, Action action, Target target, Target caster) {
        this.instance = spell;
        this.action = action;
        this.target = target;
        this.caster = caster;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = ChatColor.translateAlternateColorCodes('&',errorMessage);
    }



    public String getErrorMessage() {
        return errorMessage;
    }

    public Target getTarget() {
        return target;
    }

    public Target getCaster() {
        return caster;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public void setCaster(Target caster) {
        this.caster = caster;
    }

    public SpellInstance getInstance() {
        return instance;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    private static final HandlerList HANDLERS = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
