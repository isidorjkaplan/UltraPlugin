package co.amscraft.ultralib.player;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.utils.ObjectUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.UUID;

/**
 * Created by Izzy on 2017-08-21.
 * An abstract class which represents data inside of a player
 */
public abstract class PlayerData {
    @FieldDescription(save = false, show = false)
    /**
     * The UUID of the player who this data represents, keeping this stored at runtime but not saving it allows for quicker calculations of the getPlayer() method at runtime
     */
    private UUID uuid = null;

    /**
     * The PlayerData has a no-args constructor
     */
    public PlayerData() {

    }

    /**
     * A method to save the data to a string
     *
     * @return The data represented as a string
     */
    public String saveToString() {
        YamlConfiguration config = new YamlConfiguration();
        this.save(config);
        return config.saveToString();
    }

    /**
     * A method to save this data to a file config
     *
     * @param config The config to save this data to
     */
    public void save(FileConfiguration config) {
        config.set("data." + this.getDataName(), null);
        ConfigurationSection section = config.createSection("data." + this.getDataName());
        try {
            ObjectUtils.write(section, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A method to save this player data to it's default config
     */
    public void save() {
        FileConfiguration config = this.getPlayer().getConfig();
        this.save(config);
        try {
            config.save(this.getPlayer().getFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A method that accesses the player. The first time called it runs at O(N) but after that it runs at O(1)
     *
     * @return The player this data represents
     */
    public UltraPlayer getPlayer() {
        if (this.uuid == null) {
            if (UltraPlayer.getConsole().getData().contains(this)) {
                return UltraPlayer.getConsole();
            }
            for (UltraPlayer player : UltraPlayer.getPlayers()) {
                if (player.hasData(this.getClass()) && player.getData(this.getClass()) == this) {
                    this.uuid = player.getId();
                    break;
                }
            }
        }
        return UltraPlayer.getPlayer(this.uuid);
    }

    /**
     * The name of this data type
     *
     * @return The name of this data type
     */
    public String getDataName() {
        return this.getClass().getSimpleName();
    }

    /**
     * To represent this object as
     *
     * @return This object as a string
     */
    @Override
    public String toString() {
        return ObjectUtils.toString(this);
    }

}
