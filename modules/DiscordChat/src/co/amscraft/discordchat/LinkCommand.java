package co.amscraft.discordchat;

import co.amscraft.discordchat.discord.DiscordUsers;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import org.bukkit.command.CommandSender;

public class LinkCommand extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"link"};
    }

    @Override
    public void run(CommandSender sender, String[] args, int i) {
        EditorSettings s = EditorSettings.getSettings(sender);
        String input = "";
        for (int j = i; j < args.length; j++) {
            input+=args[i];
            if (i+1<args.length) {
                input+=" ";
            }
        }
        if (input.length() > 0 && input.contains("#")) {
            DiscordUsers.getPending().put(input, sender.getName());
            sender.sendMessage(s.getSuccess() + "Successfully sent link request, type ?link " + sender.getName() + " on Discord to finish linking!");
        } else {
            sender.sendMessage(s.getError() + "You must enter a valid discord tag!");
        }
    }

    @Override
    public String getHelp() {
        return "<Discord>";
    }
}
