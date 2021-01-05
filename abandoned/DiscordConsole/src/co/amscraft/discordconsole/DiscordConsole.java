package co.amscraft.discordconsole;

import co.amscraft.discordchat.DiscordChat;
import co.amscraft.discordchat.ModuleListener;
import co.amscraft.discordchat.discord.UltraBot;
import co.amscraft.ultralib.modules.Module;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.io.PrintStream;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class DiscordConsole extends Module {
    @Override
    public String[] getModuleDependancies() {
        return new String[]{"DiscordChat"};
    }

    @Override
    public void onEnable() {
        UltraBot bot = DiscordChat.getBot();
        bot.getApi().addMessageCreateListener(new MessageCreateListener() {
            @Override
            public void onMessageCreate(MessageCreateEvent messageCreateEvent) {
                ConsoleListener.onDiscordMessageEvent(messageCreateEvent);
            }
        });

        Bukkit.getLogger().addHandler(new ConsoleHandler(){
            @Override
            public synchronized void publish(LogRecord record) {
                super.publish(record);
                for (ConsoleChannel ch: ConsoleChannel.getList(ConsoleChannel.class)) {
                    if (ch.getType() == ConsoleChannel.Type.CONSOLE) {
                        bot.getDiscordChannel(ch.getDiscordChannelId()).sendMessage(record.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void onDisable() {

    }
}
