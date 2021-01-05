package co.amscraft.morph;

import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultramagic.events.PlayerSpellCheckEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class MorphListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        MorphData data = UltraPlayer.getPlayer(evt.getPlayer()).getData(MorphData.class);
        if (data.getActive() != null) {
            data.getActive().disable(evt.getPlayer());
        }
    }

    @EventHandler
    public void playerHasSpellEvent(PlayerSpellCheckEvent evt) {
        if (!evt.hasSpell()) {
            MorphData data = evt.getPlayer().getData(MorphData.class);
            if (data.getActive() != null && data.getActive().getSpells().contains(evt.getSpell())) {
                evt.setHasSpell(true);
            }
        }
    }
}
