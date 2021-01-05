package co.amscraft.morph;

import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.command.CommandSender;

public class MorphCommand extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"morph"};
    }

    @Override
    public void run(CommandSender sender, String[] args, int i) {
        EditorSettings s = EditorSettings.getSettings(sender);
        MorphData data = UltraPlayer.getPlayer(sender).getData(MorphData.class);
        if (args[i] == null || args[i].equalsIgnoreCase("off")) {
            if (data.getActive() != null) {
                data.setActive(null);
                sender.sendMessage(s.getSuccess() + "You have successfully disabled your morph");
            } else {
                sender.sendMessage(s.getError() + "You are not currently morphed! Enter /morph <morph> to change or /morph list for a list of available morphs");
            }
        } else {
            Morph morph = Morph.getMorph(args[i]);
            if (morph != null) {
                if (data.hasMorph(morph)) {
                    data.setActive(morph);
                    sender.sendMessage(s.getSuccess() + "Successfully morphed into " + morph.getName());
                } else {
                    sender.sendMessage(s.getError() + "You must have this morph unlocked to use it!");
                }
            } else {
                sender.sendMessage(s.getError() + "Morph " + args[i] + " does not exist!");
            }
        }

    }

    @Override
    public String getHelp() {
        return "The main command for morphing";
    }
}
