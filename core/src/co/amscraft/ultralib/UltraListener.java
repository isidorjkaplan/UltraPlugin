package co.amscraft.ultralib;

import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.Editor;
import co.amscraft.ultralib.events.EntityDeathByEntityEvent;
import co.amscraft.ultralib.network.NetworkListener;
import co.amscraft.ultralib.network.events.NetworkConnectionEvent;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultralib.utils.ObjectUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.TabCompleteEvent;

import java.util.logging.Level;

/**
 * Created by Izzy on 2017-10-11.
 */
public class UltraListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public static void onAsyncPlayerChatEvent(AsyncPlayerChatEvent evt) {
        if (!evt.isCancelled()) {
            UltraPlayer player = UltraPlayer.getPlayer(evt.getPlayer());
            if (player.hasData(Editor.class)) {// && player.getData(EditorSettings.class).mode == EditorSettings.Mode.CHAT) {
                evt.setCancelled(true);
                String key = evt.getMessage().split(":")[0];
                String value = null;
                if (!evt.getMessage().equals(key)) {
                    value = evt.getMessage().replace(key + ":", "");
                    while (value.startsWith(" ")) {
                        value = value.replaceFirst(" ", "");
                    }
                }
                //System.out.println(key + ", " + value);
                player.getData(Editor.class).run(key, value);
            }
        }
    }

    /*@EventHandler
    public void onInventoryClick(InventoryClickEvent evt) {
        UltraPlayer player = UltraPlayer.getPlayer(evt.getWhoClicked());
        if (player.hasData(Editor.class) && player.getData(EditorSettings.class).mode == EditorSettings.Mode.INVENTORY && evt.getInventory().getTitle().equals(player.getData(Editor.class).getTitle())) {
            evt.setCancelled(true);
            player.getData(Editor.class).passInventoryClick(evt);
        }
    }*/

    @EventHandler(priority = EventPriority.HIGHEST)
    public static void onPlayerQuit(PlayerQuitEvent evt) {
        UltraPlayer player = UltraPlayer.getPlayer(evt.getPlayer());
        if (player != null) {
            try {
                player.save();
                // System.out.println("Saved");
            } catch (Throwable e) {
                ObjectUtils.debug(Level.WARNING, "Error occored saving player: " + evt.getPlayer().getName());
                e.printStackTrace();
            }
            player.deregister();
            ObjectUtils.debug(Level.INFO, "Removed player from memory: " + evt.getPlayer().getName());
        }

    }

    @EventHandler
    public static void onNetworkConnectEvent(NetworkConnectionEvent evt) {
        if (!NetworkListener.getConfig().getStringList("whitelist").contains(evt.getConnection().getInetAddress().getHostAddress())) {
            evt.setCancelled(true);
        }
    }

    @EventHandler
    public static void onEntityDeath(EntityDeathEvent evt) {
        if (evt.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            new EntityDeathByEntityEvent(evt).dispatch();
        }
    }


    @EventHandler
    public void onPlayerAutoTabComplete(TabCompleteEvent evt) {
        if (evt.getBuffer().startsWith("/")) {
            String buffer = evt.getBuffer().replaceFirst("[/]", "");
            String command = buffer.split(" ")[0];
            if (UltraCommand.getCommand(command) != null) {
                evt.setCompletions(UltraCommand.getCommand(command).completeTab(buffer));//evt.getSender(), UltraCommand.getCommand(command).getBukkitCommand(), command, args));
            }
        } else if (UltraPlayer.getPlayer(evt.getSender()).hasData(Editor.class)) {
            evt.setCompletions(UltraPlayer.getPlayer(evt.getSender()).getData(Editor.class).getAutoComplete(evt.getBuffer().equals("") ? "" : evt.getBuffer().split(" ")[0]));
        }
    }
}
