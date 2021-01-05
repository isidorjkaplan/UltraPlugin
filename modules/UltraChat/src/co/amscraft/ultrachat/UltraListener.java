package co.amscraft.ultrachat;

import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collection;

public class UltraListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChatEvent(AsyncPlayerChatEvent evt) {
        if (!evt.isCancelled()) {
            String message = evt.getMessage();
            String format;
            UltraPlayer sender = UltraPlayer.getPlayer(evt.getPlayer());
            Collection<? extends Player> players;
            if (message.startsWith("!") && evt.getPlayer().hasPermission("UltraLib.chat.global")) {
                message = message.replaceFirst("!", "");
                players = Bukkit.getOnlinePlayers();
                format = "&7&l(&aglobal&7&l) &f{PLAYER}&f: {MESSAGE}";
            } else {
                Channel channel = sender.getData(ChatData.class).getChannel();
                format = channel.getFormat();
                players = channel.getBukkitRecpiants();
            }
            message = sender.getData(ChatData.class).format(message);
            evt.setFormat(Channel.format(format, message, sender).replace(message, "%2$s").replace(sender.getBukkit().getDisplayName(), "%1$s"));
            evt.setMessage(message);
            evt.getRecipients().clear();
            evt.getRecipients().addAll(players);
        }
    }


}
