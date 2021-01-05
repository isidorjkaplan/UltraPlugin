package co.amscraft.spawn;

import co.amscraft.ultralib.player.PlayerUtility;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpawnListener implements Listener {
    @EventHandler
    public static void onPlayerRespawnEvent(PlayerRespawnEvent evt) {
        //System.out.println("Respawning");
        if (evt.getPlayer().isOnline()) {
            UltraPlayer player = UltraPlayer.getPlayer(evt.getPlayer());
            if (player != null) {
                //System.out.println(0);
                Set<String> perms = new HashSet<>(player.getData(PlayerUtility.class).getAllPermissions());
                int priority = 0;
                //System.out.println(1);
                for (Spawn spawn : Spawn.getList(Spawn.class)) {
                    if (spawn.getPriority() > priority) {
                        if (perms.contains(spawn.getPermission().toLowerCase())) {
                            //          System.out.println(spawn);
                            evt.setRespawnLocation(spawn.getLocation().toLocation());
                            priority = spawn.getPriority();
                        }
                    }
                }
            }
        }
    }
}