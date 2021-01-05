package co.amscraft.discordchat;

import co.amscraft.discordchat.chat.ChannelBridge;
import co.amscraft.discordchat.discord.BotConfig;
import co.amscraft.discordchat.discord.UltraBot;
import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.modules.Module;
import org.bukkit.scheduler.BukkitRunnable;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;

public class DiscordChat extends Module {
    @Override
    public String[] getModuleDependancies() {
        return new String[]{"UltraChat"};
    }

    private static UltraBot bot;

    public static UltraBot getBot() {
        return bot;
    }

    @Override
    public void onEnable() {
        bot = new UltraBot(BotConfig.API_TOKEN);
        getBot().getApi().addMessageCreateListener(new MessageCreateListener() {
            @Override
            public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
                ModuleListener.onDiscordMessageEvent(messageCreateEvent);
            }
        });
        new BukkitRunnable() {
            public void run() {
                for (ChannelBridge bridge: ChannelBridge.getList(ChannelBridge.class)) {
                    if (bridge.isGetsServerBroadcasts()) {
                        new MessageBuilder().setEmbed(new EmbedBuilder().setColor(Color.GREEN).setDescription(BotConfig.SERVER_START_MESSAGE)).send(bridge.getDiscordChannel());
                    }
                }
            }
        }.runTask(UltraLib.getInstance());
    }


    @Override
    public void onDisable() {
        for (ChannelBridge bridge: ChannelBridge.getList(ChannelBridge.class)) {
            if (bridge.isGetsServerBroadcasts()) {
                new MessageBuilder().setEmbed(new EmbedBuilder().setColor(Color.RED).setDescription(BotConfig.SERVER_STOP_MESSAGE)).send(bridge.getDiscordChannel());
            }
        }
    }
}
