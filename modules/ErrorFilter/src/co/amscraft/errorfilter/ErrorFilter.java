package co.amscraft.errorfilter;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.modules.Module;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class ErrorFilter extends Module {
    public static List<String> errors;// = new ArrayList<>();
    public static List<String> blockedMessages;

    @Override
    public String[] getModuleDependancies() {
        return new String[0];
    }

    @Override
    public void onEnable() {
        System.setErr(new ErrorPrintStream(System.err));
        System.setOut(new OutFilteredStream(System.out));
        this.loadErrors();
        registerBukkitFilter();
    }

    private void registerBukkitFilter() {
        Filter filter = Bukkit.getLogger().getFilter();
        Bukkit.getLogger().setFilter(new Filter() {
            @Override
            public boolean isLoggable(LogRecord record) {
                for (String blocked : blockedMessages) {
                    if (record.getMessage().contains(blocked)) {
                        return false;
                    }
                }
                return filter == null || filter.isLoggable(record);
            }
        });
    }

    public void loadErrors() {
        File file = new File(this.getDataFolder() + "/config.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                List<String> errors = new ArrayList<>();
                errors.add("java.lang.IllegalArgumentException: Cannot measure distance between");
                config.set("errors", errors);
                List<String> blocked = new ArrayList<>();
                blocked.add("[Citizens] Exception while updating");
                config.set("blocked-messages", blocked);
                List<String> plugins = new ArrayList<>();
                plugins.add("UltraLib");
                config.set("plugins", plugins);
                config.save(file);
                //file.createNewFile();
                //System.out.println(file.exists());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        errors = config.getStringList("errors");
        blockedMessages = config.getStringList("blocked-messages");
        new BukkitRunnable() {
            public void run() {
                try {
                    List<String> plugins = config.getStringList("plugins");
                    for (Plugin p : Bukkit.getPluginManager().getPlugins()) {
                        if (plugins.contains(p.getName())) {
                            Filter filter = p.getLogger().getFilter();
                            p.getLogger().setFilter(new Filter() {
                                @Override
                                public boolean isLoggable(LogRecord record) {
                                    for (String blocked : blockedMessages) {
                                        if (record.getMessage().contains(blocked)) {
                                            return false;
                                        }
                                    }
                                    return filter == null || filter.isLoggable(record);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLater(UltraLib.getInstance(), 1);
    }


    /*public static void main(String[] args) {
        System.setErr(new ErrorPrintStream(System.err));
        errors.add("java.lang.NumberFormatExceptions");
        for (int i = 0; i < 4; i++) {
            try {
                if (i % 2 == 0) {
                    throw new NumberFormatException("Cannot test stupid thing");
                } else {
                    throw new NumberFormatException();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }*/

    @Override
    public void onDisable() {

    }
}
