package co.amscraft.discordchat;

import co.amscraft.discordchat.chat.ChannelBridge;
import co.amscraft.discordchat.discord.DiscordUsers;
import co.amscraft.ultrachat.Channel;
import co.amscraft.ultrachat.ChatData;
import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.BroadcastMessageEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.MessageAuthor;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;

import java.awt.*;
import java.util.*;

public class ModuleListener implements Listener {

    public static final String LINK_COMMAND = "?link";
    public static final String ONLINE_COMMAND = "?list";

    public static void onDiscordMessageEvent(MessageCreateEvent event) {
        TextChannel channel = event.getChannel();
        Message message = event.getMessage();
        MessageAuthor author = message.getAuthor();
        ChannelBridge bridge = ChannelBridge.getChannelBridge(channel.getId());
        if (bridge != null) {
            if (author.isUser() && !author.asUser().get().isBot()) {
                User user = author.asUser().get();
                String tag = user.getDiscriminatedName();
                String tagUser = user.getMentionTag();
                if (message.getContent().startsWith(LINK_COMMAND)) {
                    if (DiscordUsers.getPending().containsKey(tag)) {
                        String name = message.getContent().replace(LINK_COMMAND + " ", "");
                        if (DiscordUsers.getPending().get(tag).equalsIgnoreCase(name)) {
                            DiscordUsers.getUsers().put(tag, Bukkit.getOfflinePlayer(name).getUniqueId());
                            channel.sendMessage(tagUser + ", You have successfully linked to player " + name);
                        } else {
                            channel.sendMessage(tagUser + ", Your in game discord-request is wrong. Please ensure you entered '/link " + tag + "' in game");
                        }
                    } else {
                        channel.sendMessage(tagUser + ", You do not have any minecraft player link's pending! Log on in game and use /link " + tag);
                    }
                } else if (message.getContent().replace(" ", "").toLowerCase().startsWith(ONLINE_COMMAND)) {
                    //message.delete();
                    UUID player = DiscordUsers.getUsers().get(tag);
                    if (player != null) {
                        //"```" + message + "```"
                        Channel local = bridge.getLocalChannel();
                        String players = "Online Players: " + Bukkit.getOnlinePlayers().size() +
                                "\nCurrent Channel: " + local.getPlayers().size() +
                                "\n```";
                        for (UltraPlayer uP : local.getPlayers()) {
                            Player bukkit = uP.getBukkit();
                            players += ChatColor.stripColor(bukkit.getDisplayName()) + " (" + bukkit.getName() + ")\n";
                        }
                        if (local.getPlayers().isEmpty()) {
                            players += "Channel Empty\n";
                        }
                        players += "```";
                        new MessageBuilder().setEmbed(new EmbedBuilder().setColor(Color.RED).setDescription(players)).send(channel);
                        //broadcast(players);
                    } else {
                        channel.sendMessage("You must link a profile to use this command! Use /link <discordname> in game!");
                    }
                } else {
                    message.delete();
                    UUID player = DiscordUsers.getUsers().get(tag);
                    if (player != null) {
                        Channel local = bridge.getLocalChannel();
                        String name = DiscordUsers.getDisplayname(player);
                        String formattedMessage = local.format(name, name, message.getContent());
                        local.broadcast(formattedMessage);
                        channel.sendMessage(discordFormat(formattedMessage, message.getContent()));
                    } else {
                        channel.sendMessage(tagUser + ", you must register a Minecraft profile to use minecraft-discord chat by using /link " + tag + " in minecraft!");
                    }

                }
            }
        }
    }

    public static String discordFormat(String minecraft, String message) {
        return ChatColor.stripColor(minecraft.replace(message, "```" + message + "```"));
    }

    @EventHandler
    public static void onAsyncPlayerChatEvent(AsyncPlayerChatEvent evt) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!evt.isCancelled()) {
                    Channel channel = UltraPlayer.getPlayer(evt.getPlayer()).getData(ChatData.class).getChannel();
                    ChannelBridge bridge = ChannelBridge.getChannelBridge(channel);
                    if (bridge != null) {
                        TextChannel textChannel = bridge.getDiscordChannel();
                        textChannel.sendMessage(ChatColor.stripColor(channel.format(evt.getPlayer().getName(), evt.getPlayer().getDisplayName(), "```" + ChatColor.stripColor(evt.getMessage()) + "```")));
                    }
                }
            }
        }.runTaskAsynchronously(UltraLib.getInstance());

    }

    public static void broadcast(MessageBuilder message) {
        for (ChannelBridge bridge : UltraObject.getList(ChannelBridge.class)) {
            if (bridge.isGetsServerBroadcasts()) {
                message.send(bridge.getDiscordChannel());
            }
        }
    }

    public static void broadcast(String string) {
        broadcast(new MessageBuilder().setEmbed(new EmbedBuilder().setColor(Color.ORANGE).setDescription(ChatColor.stripColor(string))));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerBroadcast(BroadcastMessageEvent evt) {
        broadcast(evt.getMessage());
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent evt) {
        broadcast("(-)" + evt.getPlayer().getDisplayName());
    }

}
