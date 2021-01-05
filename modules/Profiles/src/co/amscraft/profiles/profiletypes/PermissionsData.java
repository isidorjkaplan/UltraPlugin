package co.amscraft.profiles.profiletypes;

import co.amscraft.profiles.ProfileType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PermissionsData extends ProfileType {
    public List<String> permissions = new ArrayList<>();
    public List<String> groups = new ArrayList<>();
    public String prefix = "\"\"";
    public String suffix = "\"\"";

    @Override
    public void save() {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex reload");
        File file = new File("plugins/PermissionsEx/permissions.yml");
        if (file.exists()) {
            String path = "users." + this.getPlayer().getBukkit().getUniqueId().toString();
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            groups = config.getStringList(path + ".group");
            prefix = config.getString(path + ".options.prefix");
            suffix = config.getString(path + ".options.suffix");
            permissions = config.getStringList(path + ".permissions");
        }
    }

    @Override
    public void enable() {
        String player = this.getPlayer().getBukkit().getName();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + player + " delete");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + player + " delete");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + player + " prefix " + (prefix != null ? prefix : "\"\""));
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + player + " suffix " + (suffix != null ? suffix : "\"\""));
        if (!groups.isEmpty()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + player + " group set " + groups.get(0));
            for (int i = 1; i < groups.size(); i++) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + player + " group add " + groups.get(i));
            }
        }
        for (String permission : permissions) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex user " + player + " add " + permission);
        }
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "nm reload");

    }

    @Override
    public boolean isEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PermissionsEx");
    }
}
