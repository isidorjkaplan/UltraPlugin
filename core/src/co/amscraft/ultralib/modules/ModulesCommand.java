package co.amscraft.ultralib.modules;

import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.command.CommandSender;

/**
 * Created by Izzy on 2017-11-22.
 */
public class ModulesCommand extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"modules"};
    }

    @Override
    public void run(CommandSender sender, String[] args, int i) {
        EditorSettings settings = UltraPlayer.getPlayer(sender.getName()).getData(EditorSettings.class);
        String modules = "";
        for (Module m : Module.getModules()) {
            modules += m.getName() + ", ";
        }
        sender.sendMessage(settings.getVariable() + "Loaded Modules (" + Module.getModules().size() + ")" + settings.getColon() + ": " + settings.getValue() + modules);
    }

    @Override
    public String getHelp() {
        return "List all of the loaded modules";
    }
}
