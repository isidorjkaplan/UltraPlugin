package co.amscraft.converter;

import co.amscraft.objects.plugin.player.UltraPlayer;
import co.amscraft.ultralib.events.PlayerDataCreationEvent;
import co.amscraft.ultramagic.MagicData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ConverterListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerDataCreationEvent evt) {
        if (MagicData.class.isAssignableFrom(evt.getDataType())) {
            UltraPlayer.getPlayer(evt.getPlayer().getBukkit().getUniqueId()).getMagicData().sidebar = false;
        }
    }
}
