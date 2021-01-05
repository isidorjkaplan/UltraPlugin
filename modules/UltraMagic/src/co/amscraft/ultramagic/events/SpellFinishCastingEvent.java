package co.amscraft.ultramagic.events;

import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.events.utility.UltraEvent;
import co.amscraft.ultramagic.SpellInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * Created by Izzy on 2017-10-19.
 */
public class SpellFinishCastingEvent extends UltraEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private SpellInstance instance;
    private String hitMessage = "{CASTER} has cast {SPELL} on you!";
    private String casterMessage = "You have finished casting {SPELL}!";

    public SpellFinishCastingEvent(SpellInstance spell) {
        this.instance = spell;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public SpellInstance getSpell() {
        return this.instance;
    }

    public String getHitMessage() {
        return hitMessage;
    }

    public void setHitMessage(String hitMessage) {
        this.hitMessage = hitMessage;
    }

    public String formatMessage(String message, Player player) {
        EditorSettings s = EditorSettings.getSettings(player);
        return s.getHelp() + message
                .replace("{CASTER}", s.getVariable() + this.getSpell().CASTER.getName() + s.getHelp())
                .replace("{SPELL}", s.getValue() + this.getSpell().getSpell().getName() + s.getHelp());
    }

    public String getCasterMessage() {
        return this.casterMessage;
    }

    public void setCasterMessage(String casterMessage) {
        this.casterMessage = casterMessage;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
