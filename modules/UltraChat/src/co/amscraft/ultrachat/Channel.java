package co.amscraft.ultrachat;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Channel extends UltraObject {

    @FieldDescription(help = "The format for the channel. Variables are: {CHANNEL}, {USERNAME}, {PLAYER}, {MESSAGE}")
    public String format = "&7&l(&a{CHANNEL}&7&l) &f{PLAYER}&f: {MESSAGE}";
    public String name = "channel";
    public String permission = "ultralib.commands.channel.switch";
    public boolean general = false;
    public int playerLimit = -1;

    private boolean locked = false;

    public Channel() {
        super();
    }

    public Channel(String name) {
        this();
        this.name = name;
    }

    public static Channel getChannel(String name) {
        if (name == null) {
            return null;
        }
        for (Channel channel : getChannels()) {
            if (channel.getName().equalsIgnoreCase(name)) {
                return channel;
            }
        }
        return null;
    }

    public static Set<Channel> getChannels() {
        return UltraObject.getList(Channel.class);
    }

    public static Channel getGeneral() {
        for (Channel c : getChannels()) {
            if (c.isGeneral()) {
                return c;
            }
        }
        Channel channel = new Channel("general");
        channel.setGeneral();
        channel.save();
        return channel;

    }

    public static Channel getChannel(int id) {
        return UltraObject.getObject(Channel.class, "ID", id);
    }

    public static String format(String format, String message, UltraPlayer sender) {
        return ChatColor.translateAlternateColorCodes('&', format
                .replace("{USERNAME}", sender.getBukkit().getName())
                .replace("{PLAYER}", sender.getBukkit().getDisplayName())
                .replace("{CHANNEL}", sender.getData(ChatData.class).getChannel().getName()))
                .replace("{MESSAGE}", sender.getData(ChatData.class).getColor() + message);
    }

    public String format(String username, String displayName, String message) {
        return ChatColor.translateAlternateColorCodes('&', format
                .replace("{USERNAME}", username)
                .replace("{PLAYER}", displayName)
                .replace("{CHANNEL}", this.getName()))
                .replace("{MESSAGE}", message);
    }

    public static void broadcastGlobally(String string) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(string);
        }
    }

    public String getName() {
        return this.name;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public List<UltraPlayer> getPlayers() {
        List<UltraPlayer> players = new ArrayList<>();
        for (UltraPlayer player : UltraPlayer.getPlayers()) {
            if (player.getData(ChatData.class).getChannel().getId() == this.getId()) {
                players.add(player);
            }
        }
        return players;
    }

    public List<UltraPlayer> getRecipients() {
        List<UltraPlayer> players = new ArrayList<>();
        for (UltraPlayer player : UltraPlayer.getPlayers()) {
            ChatData data = player.getData(ChatData.class);
            if (data.isSpying() || data.getChannel().getId() == this.getId()) {
                players.add(player);
            }
        }
        return players;
    }

    public List<Player> getBukkitRecpiants() {
        List<Player> list = new ArrayList<>();
        for (UltraPlayer player : getRecipients()) {
            list.add(player.getBukkit());
        }
        return list;
    }

    public void broadcast(String message) {
        for (UltraPlayer player : getRecipients()) {
            player.getBukkit().sendMessage(message);
        }
    }

    public boolean isFull() {
        return this.playerLimit != -1 && this.getPlayers().size() >= this.playerLimit;
    }

    public String getPermission() {
        return this.permission;
    }

    public boolean isGeneral() {
        return this.general;
    }

    public void setGeneral() {
        for (Channel channel : Channel.getChannels()) {
            if (channel.isGeneral()) {
                channel.general = false;
            }
        }
        this.general = true;
    }

    public String getFormat() {
        return this.format;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
