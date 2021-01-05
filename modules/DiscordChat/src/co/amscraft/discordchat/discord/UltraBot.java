package co.amscraft.discordchat.discord;

import co.amscraft.discordchat.DiscordChat;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.Channel;
import org.javacord.api.entity.channel.TextChannel;

import java.util.Optional;

public class UltraBot {
    public UltraBot(String token) {
        api = new DiscordApiBuilder().setToken(token).login().join();
    }
    private DiscordApi api;

    public DiscordApi getApi() {
        return api;
    }

    public TextChannel getDiscordChannel(long id) {
        Optional<Channel> channelOptional = this.getApi().getChannelById(id);
        if (channelOptional.isPresent()) {
            Optional<TextChannel> textChannel = channelOptional.get().asTextChannel();
            if (textChannel.isPresent()) {
                return textChannel.get();
            }
        }
        return null;
    }
}
