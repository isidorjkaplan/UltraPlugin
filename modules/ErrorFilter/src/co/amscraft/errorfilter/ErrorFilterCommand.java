package co.amscraft.errorfilter;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.commands.Component;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.modules.Module;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

public class ErrorFilterCommand extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"error"};
    }

    @Override
    public Component[] getComponents() {
        return new Component[]{new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"reload"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                Module.getModule(ErrorFilter.class).loadErrors();
                sender.sendMessage(EditorSettings.getSettings(sender).getHelp() + "Reloaded errors");
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"Broadcast"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                String s = "";
                for (String arg : args) {
                    s += arg + " ";
                }
                UltraLib.getInstance().getLogger().log(Level.INFO, s);
            }
        }};
    }

    @Override
    public String getHelp() {
        return "The command to access the error filter from UltraLib";
    }
}
