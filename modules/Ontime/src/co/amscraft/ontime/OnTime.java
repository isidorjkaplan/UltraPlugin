package co.amscraft.ontime;

import co.amscraft.ultralib.modules.Module;
import co.amscraft.ultralib.utils.ObjectUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class OnTime extends Module {


    private static OntimeConfig ontimeConfig;

    public static OntimeConfig getOntimeConfig() {
        return ontimeConfig;
    }

    public static void saveConfig() {
        File file = new File(Module.getModule(OnTime.class).getDataFolder() + "/config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        try {
            ObjectUtils.write(config, ontimeConfig);
            config.save(file);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String[] getModuleDependancies() {
        return new String[0];
    }

    @Override
    public void onEnable() {
        File file = new File(this.getDataFolder() + "/config.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        if (!file.exists()) {
            try {
                // file.createNewFile();
                ObjectUtils.write(config, new OntimeConfig());
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        ontimeConfig = (OntimeConfig) ObjectUtils.read(config);
    }

    @Override
    public void onDisable() {

    }
}
