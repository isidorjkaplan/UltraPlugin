package co.amscraft.ultralib.editor;

import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.command.CommandSender;

public class SettingsCommand extends UltraCommand {

    @Override
    public String[] getAliases() {
        return new String[]{"settings", "usettings"};
    }

    @Override
    public void run(CommandSender sender, String[] args, int i) {
        Editor editor = UltraPlayer.getPlayer(sender.getName()).getData(Editor.class);
        editor.getEditing().add(UltraPlayer.getPlayer(sender.getName()).getData(EditorSettings.class));
        editor.resend();
    }

    @Override
    public String getHelp() {
        return "Change your color settings for the editor, commands, and more!";
    }
}
