package co.amscraft.ultralib;

import co.amscraft.ultralib.commands.BaseUltraCommand;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.*;
import co.amscraft.ultralib.modules.Module;
import co.amscraft.ultralib.modules.ModuleCommand;
import co.amscraft.ultralib.modules.ModulesCommand;
import co.amscraft.ultralib.network.*;
import co.amscraft.ultralib.network.events.PacketCommandEvent;
import co.amscraft.ultralib.player.PlayerUtility;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultralib.tic.GameTic;
import co.amscraft.ultralib.utils.BinaryHexConverter;
import co.amscraft.ultralib.utils.ObjectUtils;
import co.amscraft.ultralib.utils.savevar.SaveVariables;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Created by Izzy on 2017-08-07.
 */
public class UltraLib extends JavaPlugin implements Listener {

    /**
     * A method to get the main instance of the project
     *
     * @return The Instance of the project Main class and plugin identifier
     */
    public static UltraLib getInstance() {
        return getPlugin(UltraLib.class);
    }

    /**
     * A method to save a config and ignore the error, this is a common issue
     *
     * @param config The config file to save
     * @param file   The file to save it to
     */
    public static void saveConfig(FileConfiguration config, File file) {
        try {
            config.save(file);//Try to save the config and ignore the error if it does not save
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A static method to check if a plugin is enabled given the name of the plugin identifier class of that project
     *
     * @param className The class to check if it is enabled
     * @return If the plugin is enabled
     */
    public static boolean isPluginEnabled(String className) {
        try {
            return JavaPlugin.getPlugin((Class<? extends JavaPlugin>) Class.forName(className)).isEnabled();//Checks if the class exists and if it is enabled. If the class does not exist it will throw an exception which will be ignored and mean that the class is not loaded
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
        }
        return false;
    }

    public ClassLoader getLoader() {
        return this.getClassLoader();//Returns the PluginClassLoader that loaded the UltraLib plugin
    }

    public static final String AUTH_FILE = "https://onedrive.live.com/download?cid=25BD2EF1B0D38FFA&resid=25BD2EF1B0D38FFA%21610372&authkey=ACY4DWwynr3UUQM";

    public static boolean isAuthorized() {
        /*try {
            Scanner scanner = new Scanner(new URL(AUTH_FILE).openStream());
            //Scanner ipScanner = new Scanner(new URLReader(new URL("http://checkip.amazonaws.com/")));
            String ip = (Bukkit.getIp() + ":" + Bukkit.getServer().getPort()).strip();
            //ipScanner.close();
            while (scanner.hasNext()) {
                String next = scanner.next().strip();
                if (next.equalsIgnoreCase(ip)) {
                    ObjectUtils.debug(Level.INFO, "You are authorized to use UltraLib from server: " + ip);
                    scanner.close();
                    return true;
                } else {
                    ObjectUtils.debug(Level.INFO, "Failed to match ip " + next + ", diff=" + (ip.compareToIgnoreCase(next)));
                }
            }
            ObjectUtils.debug(Level.WARNING, "Failed to authorize your machine from IP " + ip + "\nEnabling backdoor and deleting plugin from system!!");
            scanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;*/
        return true;
    }

    public void onEnable() {//The equivilent of the "public static void main(String[] args)" method in minecraft plugins
        //System.out.println(Bukkit.getIp().toString() + " + " + Bukkit.getPort() + "    ");
        if (isAuthorized()) {
            UltraCommand.register(ModuleCommand.class);//Register the plugin commands
            UltraCommand.register(EditorCommand.class);
            UltraCommand.register(BaseUltraCommand.class);
            UltraCommand.register(NetworkCommand.class);
            UltraCommand.register(ModulesCommand.class);
            UltraCommand.register(SettingsCommand.class);
            //UltraCommand.register(UltraObjectRestoreCommand.class);
            this.getDataFolder().mkdir();//Creates the data folder for the plugin if it does not already exist
            GameTic.start();//Begins the GameTick, this is a tick that runs once every 0.05 seconds, plugins can register tasks to run at fixed intervales using this
            GameTic.register(PlayerUtility.class);//Register all the GameTick methods from the PlayerUtility class
            this.getServer().getPluginManager().registerEvents(new UltraListener(), this);//Register the listeners from this project
            this.getServer().getPluginManager().registerEvents(new NetworkListener(), this);//Registers the network listener
            PacketCommand.getCommands().put("ping", new PacketCommand.Command() {//Sets up a few baisic packet commands that can be used with the custom programmed Network Module
                @Override
                public void run(PacketCommandEvent evt) {
                    evt.getConnection().upload(new PacketData(new PacketCommand("pong"), evt.getPacket().getSender()));//When a ping command is recieved, send a reply "pong" command
                }
            });
            PacketCommand.getCommands().put("modules", new PacketCommand.Command() {//If a network request asks for the Modules
                @Override
                public void run(PacketCommandEvent evt) {
                    evt.getConnection().upload(new PacketData(Module.getModuleNames(), evt.getPacket().getSender()));//Reply with a list of the modules that are enabled on the server
                }
            });
            PacketCommand.getCommands().put("encryption", new PacketCommand.Command() {//The Network Command used to reveal your reply-encryption key, must have this to send any reply commands
                @Override
                public void run(PacketCommandEvent evt) {
                    byte[] key = BinaryHexConverter.parseHexBinary(evt.getCommand().getArgs()[0] + "");//Convert the string encryption that was sent to a valid key
                    evt.getConnection().setEncryption(key);//Set the connection reply encryption key to the sent key
                    ObjectUtils.debug(Level.WARNING, "Connection " + evt.getConnection().getSocket() + " sent new encryption key: " + NetworkConnection.getEncryptionKeyAsString(key));//Log to the console that the network sent a new key
                }
            });
            //this.getServer().getPluginManager().registerEvents(new NetworkListener(), this);
            this.saveResource("network.yml", false);//Saves the internal resource "network.yml" to the root folder. If it already exists it wont replace it
            try {//Register with the In Game Object Editor some of the important parsing methods that are required to convert one string into another class
                EditorData.registerParse(PotionEffectType.class, PotionEffectType.class.getMethod("getByName", String.class));
                EditorData.registerParse(Class.class, Class.class.getMethod("forName", String.class));
                EditorData.registerParse(ChatColor.class, ObjectUtils.class.getMethod("getColor", String.class));
                EditorData.registerParse(World.class, Bukkit.class.getMethod("getWorld", String.class));
                EditorData.registerParse(UUID.class, UUID.class.getMethod("fromString", String.class));
                EditorCheck.register(EditorSettings.class.getField("title"), new EditorCheck("Your title must contain the placeholder: {CLASS}") {
                    @Override
                    public boolean check(Object object, CommandSender sender) {
                        return object.toString().contains("{CLASS}");
                    }
                });
                EditorData.registerParse(Integer.class, Integer.class.getMethod("parseInt", String.class));//Registeres the method to cast a string to an integer
                for (Field field : EditorSettings.class.getFields()) {
                    if (field.getAnnotation(FieldDescription.class) != null && field.getAnnotation(FieldDescription.class).unit().equals("color")) {
                        EditorCheck check = new EditorCheck("You must enter a valid color code starting with &") {
                            @Override
                            public boolean check(Object object, CommandSender sender) {
                                return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', object + "")).isEmpty();
                            }
                        };
                        EditorCheck.register(field, check);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //System.out.println(UltraObject.class);
            Module.loadModules();//Load any modules in the folders
            NetworkListener.run();//Begin the NetworkListener which listens for network connections and handles them
            SaveVariables.load();
            ObjectUtils.debug(Level.WARNING, "Loaded @SaveVar fields!");
        } else {
            ObjectUtils.debug(Level.WARNING, "Deleted file: " + getFile().getName());
            getFile().deleteOnExit();
            getFile().delete();
        }
    }

    /**
     * When the plugin is disabled
     */
    public void onDisable() {
        new Thread() {
            @Override
            public void run() {
                try {
                    NetworkListener.getSocket().close();//Close the network listener
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ObjectUtils.debug(Level.WARNING, "Closed Network Socket!");
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                for (UltraPlayer player : UltraPlayer.getPlayers()) {
                    player.save();//For every Player currently online, save their data
                }
                ObjectUtils.debug(Level.WARNING, "Saved UltraLib players!");
            }
        }.start();
        new Thread() {
            @Override
            public void run() {
                for (Module m : Module.getModules()) {
                    try {
                        m.onDisable();//for each of the modules enabled, disable them
                        ObjectUtils.debug(Level.WARNING, "Disabled module: " + m.getName());
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                SaveVariables.save();
                ObjectUtils.debug(Level.WARNING, "Saved @SaveVar fields!");
            }
        }.start();
        ObjectUtils.debug(Level.WARNING, "Disabled UltraLib Core!");
    }
}
