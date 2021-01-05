package co.amscraft.discordchat.discord;

import co.amscraft.ultralib.utils.savevar.SaveVar;
import com.earth2me.essentials.Essentials;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class DiscordUsers {
    @SaveVar
    public static HashMap<String, UUID> users = new HashMap<>();
    @SaveVar
    public static HashMap<UUID, String> displayNames = new HashMap<>();

    public static String getDisplayname(UUID player) {
        Player bukkit = Bukkit.getPlayer(player);
        if (bukkit != null) {
            String name = Essentials.getPlugin(Essentials.class).getUser(player).getDisplayName();
            if (!displayNames.containsKey(player) || !displayNames.get(player).equals(name)) {
                displayNames.put(player, name);
            }
        }
        return displayNames.get(player);

    }


    public static HashMap<String, UUID> getUsers() {
        return users;
    }


    // Discord Name, Minecraft Name
    public static HashMap<String, String> pending = new HashMap<>();

    public static HashMap<String, String> getPending() {
        return pending;
    }
}
