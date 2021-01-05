package co.amscraft.ultralib.player;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.events.PlayerDataCreationEvent;
import co.amscraft.ultralib.utils.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by Izzy on 2017-08-07.
 * The class that represents an actual player, including all of the data they contain
 */
public class UltraPlayer {
    /**
     * The static map of all the players online, stored by their Unique User ID's
     */
    private static HashMap<UUID, UltraPlayer> players = new HashMap<>();
    /**
     * The console Player, it stores most of the same data as a normal player
     */
    private static UltraPlayer console = null;
    /**
     * A map that stores all the data the player contains by type in a map
     */
    private HashMap<Class<? extends PlayerData>, PlayerData> data = new HashMap<>();

    /**
     * The constructor to create a player based on their UUID
     *
     * @param uuid
     */
    private UltraPlayer(UUID uuid) {
        players.put(uuid, this);
    }

    /**
     * A static method to get a player object from their UUID, will read from the file and import that player's data
     * if the player was previously offline, will also catch any accidental data storage if the player is online and was not supposed to be
     *
     * @param uuid The UUID of the player
     * @return The object representing that player, or Null if the player was not online
     */
    public static UltraPlayer getPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            if (players.containsKey(uuid)) {
                return players.get(uuid);
            }
            return new UltraPlayer(uuid);
        } else if (players.containsKey(uuid)) {
            //players.get(uuid).save();
            ObjectUtils.debug(Level.WARNING, "Removing UltraPlayer of OfflinePlayer: " + Bukkit.getOfflinePlayer(uuid).getName());
            players.remove(uuid);
        }
        return null;
    }

    /**
     * A method to get the player from a CommandSender object
     *
     * @param sender The CommandSender
     * @return The player attached to that object
     */
    public static UltraPlayer getPlayer(CommandSender sender) {
        return UltraPlayer.getPlayer(sender.getName());
    }

    /**
     * The UltraPlayer object associated with a Minecraft Player
     *
     * @param player The Minecraft Player Object
     * @return The Ultra Player Object
     */
    public static UltraPlayer getPlayer(Player player) {
        return getPlayer(player.getUniqueId());
    }

    /**
     * A method to get a player by name
     *
     * @param name The name of the player
     * @return The player that is associated with that name, or  null if no player by that name exists
     */
    public static UltraPlayer getPlayer(String name) {
        if (name.equals("CONSOLE")) {
            return getConsole();
        }
        return Bukkit.getPlayer(name) != null ? UltraPlayer.getPlayer(Bukkit.getPlayer(name).getUniqueId()) : null;
    }

    /**
     * A list of all the player's online
     *
     * @return A list of all the player's online
     */
    public static List<UltraPlayer> getPlayers() {
        List<UltraPlayer> list = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            list.add(getPlayer(player));
        }
        return list;
    }

    /**
     * A method to get the console UltraPlayer
     *
     * @return The console UltraPlayer
     */
    public static UltraPlayer getConsole() {
        if (console == null) {
            console = new UltraPlayer(UUID.fromString("00000000-0000-0000-0000-000000000000"));
            console.save();
        }
        return console;
    }

    /**
     * A method to check if a player has a given permission node
     *
     * @param permission The permission node to check for
     * @return If the player has access to that permission node
     */
    public boolean hasPermission(String permission) {
        if (this == UltraPlayer.getConsole()) {
            return true;
        }
        if (UltraLib.isPluginEnabled("ru.tehkode.permissions.bukkit.PermissionsEx")) {
            ru.tehkode.permissions.bukkit.PermissionsEx.getUser(this.getBukkit().getName()).has(permission);
        }
        return this.getBukkit().hasPermission(permission);
    }

    /**
     * The method to get a collection of all the data this player contains
     *
     * @return The data this player contains
     */
    public Collection<PlayerData> getData() {
        return this.data.values();
    }

    /**
     * The function to set a player-data to be associated with this player
     *
     * @param object The player data to attach to this player
     */
    public void setData(PlayerData object) {
        if (object != null) {
            //this.removeData(object.getClass());
            this.data.put(object.getClass(), object);
        }
    }

    /**
     * A method to get the config file for this player
     *
     * @return The config file for this player
     */
    public FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(this.getFile());
    }

    /**
     * A method to get the file of this player
     *
     * @return The file of this player
     */
    public File getFile() {
        return new File(UltraLib.getInstance().getDataFolder() + "/players/" + this.getId().toString() + ".yml");
    }

    /**
     * A method to get the UUID of this player
     *
     * @return The UUID of this player
     */
    public UUID getId() {
        if (console == this) {
            return UUID.fromString("00000000-0000-0000-0000-000000000000");
        }
        for (UUID uuid : players.keySet()) {
            if (players.get(uuid) == this) {
                return uuid;
            }
        }
        return null;
    }

    /**
     * A method to get the Bukkit data of this player
     *
     * @return The bukkit data of this player
     */
    public Player getBukkit() {
        if (console == this) {
            return null;
        }
        return Bukkit.getPlayer(this.getId());
    }

    /**
     * A function to get a data of a given type for this player, will create it if the player does not contain data of this type
     *
     * @param type   The type you want to get
     * @param <Data> The type you want to get
     * @return The PlayerData of that type for this player
     */
    public <Data extends PlayerData> Data getData(Class<Data> type) {
        if (this.data.containsKey(type)) {
            return (Data) this.data.get(type);
        }
        PlayerDataCreationEvent event = new PlayerDataCreationEvent(this, type);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCanceled()) {
            try {
                Data data;
                if (getConfig().get("data." + type.getSimpleName()) != null) {
                    data = (Data) ObjectUtils.read(getConfig().getConfigurationSection("data." + type.getSimpleName()));
                    //System.out.println("Loaded data: " + data);
                } else {
                    data = type.newInstance();
                }
                if (data != null) {
                    this.data.put(type, data);
                }
                return data;
            } catch (Exception e) {
                try {
                    Data data = type.newInstance();
                    this.data.put(type, data);
                } catch (InstantiationException e1) {
                    e1.printStackTrace();
                } catch (IllegalAccessException e1) {
                    e1.printStackTrace();
                }

                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * A method to check if this player has data of a given type
     *
     * @param type The type you are checking for
     * @return If the player currently has data of that type
     */
    public boolean hasData(Class<? extends PlayerData> type) {
        //System.out.println(this.getData());
        return this.data.containsKey(type);
    }

    /**
     * The function to remove data of a given type from this player
     *
     * @param type The type to purge
     */
    public void removeData(Class<? extends PlayerData> type) {
        this.data.remove(type);
        FileConfiguration config = this.getConfig();
        config.set("data." + type.getSimpleName(), null);
        UltraLib.saveConfig(config, this.getFile());
    }

    /**
     * A method to deregister this player from the global list of players, called when they log off
     */
    public void deregister() {
        players.remove(this.getId());
    }

    /**
     * A method to save this player's data to it's file
     */
    public void save() {
        FileConfiguration config = this.getConfig();
        for (PlayerData data : this.getData()) {
            try {
                data.save(config);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            config.save(this.getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (this != console) {
            ObjectUtils.debug(Level.WARNING, "Saved player: " + this.getBukkit().getName());
        }
    }
}
