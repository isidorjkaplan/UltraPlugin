package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.player.PlayerUtility;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;
import co.amscraft.ultramagic.events.SpellCastEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DisableCasting extends Action implements Listener {
    @FieldDescription(unit = "seconds", help = "How long should they have their spells disabled for")
    private double disable = 60;

    @EventHandler
    public static void onPlayerSpellCastEvent(SpellCastEvent evt) {
        if (evt.getCaster().getObject() instanceof Player && UltraPlayer.getPlayer((Player) evt.getCaster().getObject()).getData(PlayerUtility.class).getCooldown("DisableCasting") > 0) {
            evt.setCancelled(true);
            evt.setFailMessage("You are unable to cast spells right now!");
        }
    }

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        if (target.getObject() instanceof Player) {
            UltraPlayer.getPlayer((Player) target.getObject()).getData(PlayerUtility.class).setCooldown("DisableCasting", disable);
        }
    }
}
