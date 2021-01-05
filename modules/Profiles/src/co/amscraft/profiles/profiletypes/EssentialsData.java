package co.amscraft.profiles.profiletypes;

import co.amscraft.profiles.ProfileType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;

public class EssentialsData extends ProfileType {
    public String nickname = "";
    public boolean godmode = false;
    public double money = 0;
    public boolean afk = false;
    public boolean tptoggle = false;
    public HashMap<String, BukkitData.Location> homesMap = new HashMap<>();


    @Override
    public void save() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "essentials load");
        File file = new File("plugins/Essentials/userdata/" + this.getPlayer().getBukkit().getUniqueId().toString() + ".yml");
        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            homesMap.clear();
            if (config.get("homes") != null) {
                for (String home : config.getConfigurationSection("homes").getKeys(false)) {
                    final String key = "homes." + home;
                    String world = config.getString(key + ".world");
                    double x = config.getDouble(key + ".x");
                    double y = config.getDouble(key + ".y");
                    double z = config.getDouble(key + ".z");
                    float pitch = (float) config.getDouble(key + ".pitch");
                    float yaw = (float) config.getDouble(key + ".yaw");
                    homesMap.put(home, new BukkitData.Location(Bukkit.getWorld(world), x, y, z, yaw, pitch));
                }
            }
            for (Field field : this.getClass().getFields()) {
                if (config.get(field.getName()) != null) {
                    try {
                        field.set(this, config.get(field.getName()));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void enable() {
        File file = new File("plugins/Essentials/userdata/" + this.getPlayer().getBukkit().getUniqueId().toString() + ".yml");
        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (Field field : this.getClass().getFields()) {
                try {
                    config.set(field.getName(), field.get(this));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            config.set("homesMap", null);
            config.set("homes", null);
            for (String home : homesMap.keySet()) {
                String key = "homes." + home;
                config.set(key + ".world", homesMap.get(home).world);
                config.set(key + ".x", homesMap.get(home).x);
                config.set(key + ".y", homesMap.get(home).y);
                config.set(key + ".z", homesMap.get(home).z);
                config.set(key + ".pitch", homesMap.get(home).pitch);
                config.set(key + ".yaw", homesMap.get(home).yaw);
            }
            try {
                config.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "essentials load");
        }

    }

    @Override
    public boolean isEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("Essentials");
    }
}
