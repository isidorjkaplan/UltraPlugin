package co.amscraft.networking.commands;

import co.amscraft.networking.Server;
import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class ListCommand extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"glist"};
    }

    @Override
    public void run(CommandSender sender, String[] args, int i) {
        new BukkitRunnable() {
            public void run() {
                EditorSettings s = EditorSettings.getSettings(sender);
                sender.sendMessage(s.getVariable() + "Servers" + s.getColon() + ": ");
                sender.sendMessage(s.getVariable() + "Local" + s.getColon() + ": " + s.getValue() + Server.getLocalPlayers());
                for (Server server : Server.getServers()) {
                    sender.sendMessage(s.getVariable() + server.getName() + s.getColon() + ": " + s.getValue() + (server.isConnected() ? server.getPlayers() : "Server Offline"));
                }
            }
        }.runTaskAsynchronously(UltraLib.getInstance());

    }

    @Override
    public String getHelp() {
        return "Use the server command";
    }
}
