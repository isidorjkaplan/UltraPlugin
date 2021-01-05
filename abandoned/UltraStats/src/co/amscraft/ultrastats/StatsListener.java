package co.amscraft.ultrastats;

import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.events.GetPlayerManaEvent;
import co.amscraft.ultramagic.events.PlayerSpellCheckEvent;
import co.amscraft.ultramagic.events.SpellCastEvent;
import co.amscraft.ultramagic.events.SpellFinishCastingEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * Created by Izzy on 2017-11-12.
 */
public class StatsListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpellCastEvent(SpellCastEvent evt) {
        if (evt.isCancelled() && evt.getFailMessage().equals("You must have the spell unlocked to cast it!")) {
            if (evt.getCaster().getObject() instanceof Player) {
                UltraPlayer player = UltraPlayer.getPlayer((Player) evt.getCaster().getObject());
                StatsData data = player.getData(StatsData.class);
                if (data.getSpells().contains(evt.getSpell())) {
                    evt.setCancelled(false);
                }
            }
        }
    }

    @EventHandler
    public void onSpellFinishCastingEvent(SpellFinishCastingEvent evt) {
        if (evt.getSpell().CASTER.getObject() instanceof Player) {
            UltraPlayer player = UltraPlayer.getPlayer((Player) evt.getSpell().CASTER.getObject());
            StatsData data = player.getData(StatsData.class);
            for (PlayerStat stat : data.getStats()) {
                if (stat.getSpells().contains(evt.getSpell().getSpell()) || !data.getSpells().contains(evt.getSpell().getSpell())) {
                    for (Target t : evt.getSpell().getTargets()) {
                        if (t.getObject() instanceof LivingEntity) {
                            stat.addExp(stat.getEntityEXPYield((LivingEntity) t.getObject()));
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSpellCheckEvent(PlayerSpellCheckEvent evt) {
        if (evt.getPlayer().getData(StatsData.class).getSpells().contains(evt.getSpell())) {
            evt.setHasSpell(true);
        }
    }

    @EventHandler
    public void onManaCheckEvent(GetPlayerManaEvent evt) {
        evt.setMaxMana(evt.getPlayer().getData(StatsData.class).getMaxMana());
    }
}
