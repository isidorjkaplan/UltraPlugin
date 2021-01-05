package co.amscraft.ultramagic;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.editor.EditorCheck;
import co.amscraft.ultralib.editor.EditorData;
import co.amscraft.ultralib.modules.Module;
import co.amscraft.ultralib.utils.ObjectUtils;
import co.amscraft.ultralib.utils.savevar.SaveVariables;
import co.amscraft.ultramagic.wands.Wand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import java.io.File;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by Izzy on 2017-10-01.
 */
public class UltraMagic extends Module {

    private static boolean editingSpellAliases = false;

    public static void createSpellAliases() {
        if (!editingSpellAliases) {
            editingSpellAliases = true;
            File file = new File("plugins/Magic/spells.yml");
            if (file.exists()) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                for (String name : config.getKeys(false)) {
                    if (name.startsWith("U-") && config.get(name + ".catagory") != null && config.getString(name + ".catagory").equals("Umagic")) {
                        config.set(name, null);
                    }
                }
                for (Spell spell : Spell.getSpells()) {
                    String name = "U-" + spell.getName();
                    config.set(name + ".icon", "ink_sack:6");
                    config.set(name + ".catagory", "Umagic");
                    List<HashMap<String, String>> list = new ArrayList<>();
                    list.add(new HashMap());
                    list.get(0).put("class", "Command");
                    config.set(name + ".actions.cast", list);
                    config.set(name + ".icon_url", !(spell.icon == null || spell.icon.equals("")) ? spell.icon : "http://textures.minecraft.net/texture/505b13370fac6a8caa93b6ccb225b11d49ae9eb0e8d7fb84c628c82e234328");
                    config.set(name + ".description", spell.description);
                    config.set(name + ".parameters.cooldown", spell.cooldown * 1000);
                    config.set(name + ".parameters.command", "umagic admin cast " + spell.getName() + " @t");
                    config.set(name + ".parameters.console", true);
                    config.set(name + ".parameters.target", "self");
                    config.set(name + ".costs.mana", spell.mana);

                }
                try {
                    config.save(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!isElmakersEnabled()) {
                    //  com.elmakers.mine.bukkit.action.builtin.CommandAction

                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "magic:magic load");
                } else {
                    com.elmakers.mine.bukkit.magic.MagicPlugin.getAPI().reload();
                }
                ObjectUtils.debug(Level.INFO, "Successfully loaded Magic aliases");
            }
            editingSpellAliases = false;
        } else {
            ObjectUtils.debug(Level.WARNING, "Tried to double save spells.yml interface!!!");
        }


    }

    public static boolean isElmakersEnabled() {
        return Bukkit.getPluginManager().getPlugin("Magic") != null && Bukkit.getPluginManager().getPlugin("Magic").isEnabled();
    }

    @Override
    public String[] getModuleDependancies() {
        return new String[0];
    }

    public void onEnable() {
        try {
            EditorData.registerParse(Spell.class, Spell.class.getMethod("getSpell", int.class));
            EditorData.registerParse(Spell.class, Spell.class.getMethod("getSpell", String.class));
            EditorData.registerParse(Wand.class, Wand.class.getMethod("getWand", String.class));
            EditorCheck.register(Spell.class.getField("name"), new EditorCheck("You cannot enter the name of another spell!") {
                @Override
                public boolean check(Object object, CommandSender sender) {
                    for (Spell spell : Spell.getSpells()) {
                        if (spell.name.equalsIgnoreCase(object.toString())) {
                            return false;
                        }
                    }
                    return true;
                }
            });

            try {
                File folder = new File(this.getDataFolder() + "/actions");
                folder.mkdir();
                for (File file : folder.listFiles()) {
                    if (file.getName().endsWith(".jar")) {
                        ObjectUtils.debug(Level.INFO, "Found actions jar file: " + file.getName());
                        Module.addURL((URLClassLoader) UltraLib.getInstance().getLoader(), file.toURI().toURL(), this);
                        for (String string : getClasseNames(file.getPath())) {
                        /*if (Action.class.isAssignableFrom(Class.forName(string))) {
                            Action.register((Class<? extends Action>) Class.forName(string));
                        } else if (EffectAction.class.isAssignableFrom(Class.forName(string))) {
                            //EditorData.registerConstructors(EffectAction.class, Class.forName(string).getConstructors());
                        }*/
                            if (Listener.class.isAssignableFrom(Class.forName(string))) {
                                try {
                                    UltraLib.getInstance().getServer().getPluginManager().registerEvents((Listener) Class.forName(string).newInstance(), UltraLib.getInstance());
                                    ObjectUtils.debug(Level.INFO, "Registered Listener: " + Class.forName(string).getSimpleName());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                SaveVariables.addClass(Class.forName(string));
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                        ObjectUtils.debug(Level.INFO, "Successfully loaded actions jar file: " + file.getName());
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            //UltraLib.getInstance().getServer().getPluginManager().registerEvents(new MagicListener(), UltraLib.getInstance());
            //registerUltraMagic();
        } catch (Exception e) {
            e.printStackTrace();
        }
        createSpellAliases();
    }

    @Override
    public void onDisable() {

    }

}
