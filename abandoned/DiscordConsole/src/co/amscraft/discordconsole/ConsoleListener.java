package co.amscraft.discordconsole;

import co.amscraft.discordchat.discord.DiscordUsers;
import co.amscraft.ultralib.UltraLib;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.util.UUID;

public class ConsoleListener implements Listener {
    public static void onDiscordMessageEvent(MessageCreateEvent evt) {
        TextChannel channel = evt.getChannel();
        ConsoleChannel ch = ConsoleChannel.getConsole(channel.getId());
        Message message = evt.getMessage();
        MessageAuthor author = message.getAuthor();
        if (ch != null && author.isUser() && !author.isBotOwner()) {
            User user = author.asUser().get();
            if (!user.isBot()) {
                String tag = user.getDiscriminatedName();
                UUID uuid = DiscordUsers.getUsers().get(tag);
                if (uuid != null) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                    if (player.isOp()) {
                        if (ch.getType() == ConsoleChannel.Type.CONSOLE) {
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), message.getContent());
                                    channel.sendMessage("Sent command as CONSOLE!");
                                }
                            }.runTask(UltraLib.getInstance());
                        } else {
                            //TODO FTP
                        }
                    } else {
                        if (player.isOnline()) {
                            channel.sendMessage("You do not have permission to use Console!");
                        }
                    }
                } else {
                    channel.sendMessage("You are not registered to use console!");
                }
            }
        }
    }
}
