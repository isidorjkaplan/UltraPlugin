package co.amscraft.spawn;

import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class SetSpawn extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"SetSpawn"};
    }

    @Override
    public void run(CommandSender sender, String[] args, int i) {
        EditorSettings s = EditorSettings.getSettings(sender);
        if (args[i] != null) {
            Spawn spawn = Spawn.getSpawn(args[i]);
            if (spawn == null) {
                sender.sendMessage(s.getSuccess() + "Creating new spawn: " + args[i]);
                spawn = new Spawn();
            }
            spawn.setLocation(Bukkit.getPlayer(sender.getName()).getLocation());
            sender.sendMessage(s.getSuccess() + "You set the spawn to your location!");
        } else {
            sender.sendMessage(s.getError() + "You must enter the name of the spawn");
        }
    }

    @Override
    public String getHelp() {
        return "The command to set the spawn";
    }
}
