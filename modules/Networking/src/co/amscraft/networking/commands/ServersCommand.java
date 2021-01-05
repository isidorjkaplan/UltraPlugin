package co.amscraft.networking.commands;

import co.amscraft.networking.Server;
import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class ServersCommand extends UltraCommand {

    @Override
    public String[] getAliases() {
        return new String[]{"servers"};
    }

    @Override
    public void run(CommandSender sender, String[] args, int i) {
        new BukkitRunnable() {
            public void run() {
                try {
                    EditorSettings s = EditorSettings.getSettings(sender);
                    String list = s.getValue() + "[";
                    for (Server server : Server.getServers()) {
                        if (server.isConnected()) {
                            list += s.getSuccess();
                        } else {
                            list += s.getError();
                        }
                        list += server.getName() + s.getValue() + ", ";
                    }
                    if (list.endsWith(", ")) {
                        list = list.substring(0, list.length() - 2);
                    }
                    list += s.getValue() + "]";
                    sender.sendMessage(s.getVariable() + "Servers" + s.getColon() + ": " + list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(UltraLib.getInstance());

    }

    @Override
    public String getHelp() {
        return "List the different servers";
    }
}
