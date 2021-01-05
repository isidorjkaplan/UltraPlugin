package co.amscraft.pextracker;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.commands.Component;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class PexLookup extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"pexlookup"};
    }

    @Override
    public String getHelp() {
        return "The command for pex lookup of perms";
    }

    @Override
    public Component[] getComponents() {
        return new Component[]{new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"search"};
            }

            @Override
            public String getHelp() {
                return "<permission>";
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                sender.sendMessage(s.getError() + "Starting lookup... please standby!");
                new BukkitRunnable() {
                    public void run() {
                        String perm = args[i];
                        if (perm != null) {
                            Map<String, String> map = PexTracker.getPlayers(perm);
                            StringBuffer st = new StringBuffer(s.getVariable() + "Player " + s.getColon() + "(" + s.getValue() + "Source" + s.getColon() + ")" + s.getHelp() + ": " + s.getColon());
                            for (String player: map.keySet()) {
                                st.append(s.getVariable() + player + "(" + s.getValue() + map.get(player) + s.getColon() + ") " );
                            }
                            sender.sendMessage(st.toString());
                        } else {
                            sender.sendMessage(s.getError() + "You must enter a permission note");
                        }
                    }
                }.runTaskAsynchronously(UltraLib.getInstance());
            }
        }};
    }
}
