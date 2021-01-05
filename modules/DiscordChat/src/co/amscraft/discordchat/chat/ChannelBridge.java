package co.amscraft.discordchat.chat;

import co.amscraft.discordchat.DiscordChat;
import co.amscraft.discordchat.discord.UltraBot;
import co.amscraft.ultrachat.Channel;
import co.amscraft.ultralib.UltraObject;
import org.javacord.api.entity.channel.TextChannel;

import java.util.Collection;
import java.util.HashSet;

public class ChannelBridge extends UltraObject {
    private Channel localChannel = Channel.getGeneral();
    private long discordChannel = 0;
    private boolean getsServerBroadcasts = false;



    public boolean isGetsServerBroadcasts() {
        return getsServerBroadcasts;
    }


    public Channel getLocalChannel() {
        return localChannel;
    }

    public TextChannel getDiscordChannel() {
        return DiscordChat.getBot().getDiscordChannel(discordChannel);
    }

    public static ChannelBridge getChannelBridge(long channel) {
        return getObject(ChannelBridge.class, "discordChannel", channel);
    }
    public static ChannelBridge getChannelBridge(Channel channel) {
        return getObject(ChannelBridge.class, "localChannel", channel);
    }


}
