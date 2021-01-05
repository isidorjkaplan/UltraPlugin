package co.amscraft.warps;

import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarpMenuCommand extends UltraCommand {

    @Override
    public String[] getAliases() {
        return new String[]{"warpmenu", "ultrawarpmenu"};
    }

    @Override
    public void run(CommandSender sender, String[] args, int i) {
        EditorSettings s = EditorSettings.getSettings(sender);
        if (args[i] != null) {
            WarpMenu menu = WarpMenu.getWarpMenu(args[i]);
            if (menu != null) {
                ((Player)sender).openInventory(menu.getGUI());
            } else {
                sender.sendMessage(s.getError() + "You entered an invalid menu: " + args[i]);
            }
        } else {
            sender.sendMessage(s.getError() + "You must enter a menu!");
        }
    }

    @Override
    public String getHelp() {
        return "<WarpMenu>";
    }
}
