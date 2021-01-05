package co.amscraft.ultralib.modules;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.commands.Component;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.command.CommandSender;

import java.io.File;

/**
 * Created by Izzy on 2017-11-08.
 */
public class ModuleCommand extends UltraCommand {
    @Override
    public Component[] getComponents() {
        return new Component[]{UltraCommand.getCommand("modules"), new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"commands"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                Module module = Module.getModule(args[i]);
                EditorSettings settings = UltraPlayer.getPlayer(sender).getData(EditorSettings.class);
                if (module != null) {
                    for (UltraCommand command : module.getCommands()) {
                        sender.sendMessage(settings.getVariable() + command.getAlias() + settings.getColon() + ": " + settings.getValue() + command.getHelp());
                    }
                } else {
                    sender.sendMessage(settings.getHelp() + "You did not enter a valid module! Use '/" + args[0] + " list' to get a list of valid modules!");
                }
            }

            @Override
            public String getHelp() {
                return "Get all of the commands from any particular module";
            }

        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"load", "l"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                String module = args[i];
                EditorSettings s = EditorSettings.getSettings(sender);
                File file = new File(UltraLib.getInstance().getDataFolder() + "/modules/" + module + ".jar");
                if (file.exists()) {
                    if (Module.getModule(file) == null && Module.getModule(module) == null) {
                        Module m = Module.loadModule(file);
                        if (m != null) {
                            sender.sendMessage(s.getHelp() + "Successfully loaded module: " + module);
                            //MODULE LOADED
                        } else {
                            //ERROR OCCORED
                            sender.sendMessage(s.getHelp() + "An error occored while attempting to enable module: " + module);
                        }
                    } else {
                        sender.sendMessage(s.getHelp() + "Module '" + s.getVariable() + module + s.getHelp() + "' has already been loaded!");
                        //MODULE ALREADY EXISTS
                    }
                } else {
                    sender.sendMessage(s.getHelp() + "File '" + s.getVariable() + module + ".jar" + s.getHelp() + "' does not exist!");
                    //FILE DOES NOT EXIST
                }
            }
        }};
    }

    @Override
    public String[] getAliases() {
        return new String[]{"module"};
    }

    @Override
    public String getHelp() {
        return "The command to get a list of available modules";
    }


}
