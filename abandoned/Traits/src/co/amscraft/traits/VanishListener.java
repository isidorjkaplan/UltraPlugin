package co.amscraft.traits;

import co.amscraft.ultralib.player.PlayerUtility;
import co.amscraft.ultralib.player.UltraPlayer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class VanishListener implements Listener {
    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent evt) {
        Player player = evt.getPlayer();
        PlayerUtility utils = UltraPlayer.getPlayer(evt.getPlayer()).getData(PlayerUtility.class);
        if (utils.getCooldown("vanish") == 0) {
            utils.setCooldown("vanish", 3);
            NPCRegistry registry = CitizensAPI.getNPCRegistry();
            for (Entity e : player.getNearbyEntities(50, 50, 50)) {
                if (registry.isNPC(e) && e instanceof Player) {
                    NPC npc = registry.getNPC(e);
                    if (Vanish.hasDeta(npc)) {
                        boolean canSee = Vanish.canSee(npc, player);
                        //System.out.println(canSee + ", " + player.canSee)
                        if (!canSee && player.canSee((Player) e)) {
                            player.hidePlayer((Player) e);
                            ((Player) npc.getEntity()).hidePlayer(player);
                        } else if (canSee && !player.canSee((Player) e)) {
                            player.showPlayer((Player) e);
                            ((Player) npc.getEntity()).showPlayer(player);
                        }
                    }
                }
            }
        }
    }

}
