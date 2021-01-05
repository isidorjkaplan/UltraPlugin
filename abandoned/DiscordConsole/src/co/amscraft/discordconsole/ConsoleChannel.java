package co.amscraft.discordconsole;

import co.amscraft.ultralib.UltraObject;

public class ConsoleChannel extends UltraObject {
    public static ConsoleChannel getConsole(long id) {
        return getObject(ConsoleChannel.class, "discordChannelId", id);
    }

    public long getDiscordChannelId() {
        return discordChannelId;
    }

    public Type getType() {
        return type;
    }

    private long discordChannelId;
    private Type type = Type.CONSOLE;
    public enum Type {
        CONSOLE, FTP
    }
}
