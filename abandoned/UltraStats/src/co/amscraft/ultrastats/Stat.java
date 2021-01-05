package co.amscraft.ultrastats;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.modules.Module;
import co.amscraft.ultramagic.Spell;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Izzy on 2017-11-12.
 */
public class Stat extends UltraObject {
    public HashMap<Integer, List<Spell>> spells = new HashMap<>();
    public String name = "Stat Name";
    public int minMana = 100;
    public int maxMana = 300;
    public int maxLevel = 100;
    public double difficulty = 1;
    public double killMultiplier = 1;

    public static HashMap<EntityType, Double> getDefaultExpYields() {
        HashMap<EntityType, Double> map = new HashMap<>();
        Module module = Module.get(UltraStats.class);
        File file = new File(module.getDataFolder() + "/exp.yml");
        if (!file.exists()) {
            InputStream stream = module.getClass().getResourceAsStream("/exp.yml");
            Scanner scanner = new Scanner(stream);
            List<String> list = new ArrayList<>();
            while (scanner.hasNext()) {
                list.add(scanner.nextLine());
            }
            try {
                FileUtils.writeLines(file, list);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (EntityType type : EntityType.values()) {
            if (!config.getKeys(false).contains(type.name())) {
                config.set(type.name(), 50);
            }
        }
        try {
            config.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String key : config.getKeys(false)) {
            map.put(EntityType.valueOf(key), config.getDouble(key));
        }

        return map;
    }

    public List<Spell> getSpells(int level) {
        List<Spell> list = new ArrayList<>();
        for (Integer i : spells.keySet()) {
            if (i <= level) {
                list.addAll(spells.get(i));
            }
        }
        return list;
    }

    public List<Spell> getSpells() {
        return getSpells(maxLevel);
    }

    public int getMana(int level) {
        double gained = minMana - maxMana;
        gained = gained * (level / 100.0);
        gained += minMana;
        return (int) Math.round(gained);
    }
    // public HashMap<EntityType, Double> expYields = null;

    public Stat getStat(String name) {
        return UltraObject.getObject(Stat.class, "name", name);
    }

    public String toString() {
        return this.name;
    }


}
