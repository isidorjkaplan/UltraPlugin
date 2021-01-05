package co.amscraft.profiles.profiletypes;

import co.amscraft.profiles.ProfileType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class QuestsData extends ProfileType {
    public List<String> currentQuests = new ArrayList<>();
    public List<Integer> currentStages = new ArrayList<>();
    public List<String> completedQuests = new ArrayList<>();
    public int questpoints;

    @Override
    public void save() {
        //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "essentials load");
        File file = new File("plugins/Quests/data/" + this.getPlayer().getBukkit().getUniqueId().toString() + ".yml");
        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            if (config.get("currentQuests") instanceof List)
                this.currentQuests = config.getStringList("currentQuests");
            if (config.get("currentStages") instanceof List)
                this.currentStages = (List<Integer>) config.getList("currentStages");
            if (config.get("completed-quests") instanceof List)
                this.completedQuests = config.getStringList("completed-quests");
            this.questpoints = config.getInt("quest-points");
        }
    }

    @Override
    public void enable() {
        File file = new File("plugins/Quests/data/" + this.getPlayer().getBukkit().getUniqueId().toString() + ".yml");
        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            if (!this.currentQuests.isEmpty())
                config.set("currentQuests", this.currentQuests);
            else
                config.set("currentQuests", "none");
            //config.set("currentQuests", this.currentQuests);
            if (!this.currentStages.isEmpty())
                config.set("currentStages", this.currentStages);
            else
                config.set("currentStages", "none");
            //config.set("currentStages", this.currentStages);
            if (!this.completedQuests.isEmpty())
                config.set("completed-quests", this.completedQuests);
            else
                config.set("completed-quests", "none");
            config.set("quest-points", this.questpoints);
            try {
                config.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "questsadmin reload");
        }
    }

    @Override
    public boolean isEnabled() {
        return false; //Bukkit.getPluginManager().isPluginEnabled("Quests");
    }
}
