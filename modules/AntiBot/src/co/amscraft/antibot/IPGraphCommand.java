package co.amscraft.antibot;

import co.amscraft.ultralib.commands.Component;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class IPGraphCommand extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"IP", "lookup"};
    }

    @Override
    public Component[] getComponents() {
        return new Component[]{new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"path"};
            }

            @Override
            public String getHelp() {
                return "<player> <target>";
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                OfflinePlayer p1 = Bukkit.getOfflinePlayer(args[i]);
                if (args.length > i + 1) {
                    OfflinePlayer p2 = Bukkit.getOfflinePlayer(args[i + 1]);
                    if (p1 != null && p2 != null && p1.hasPlayedBefore() && p2.hasPlayedBefore()) {
                        if (!p1.getUniqueId().equals(p2.getUniqueId())) {
                            IPGraph graph = IPGraph.getGraph();
                            IPGraph.Path path = graph.getPath(p1.getUniqueId(), p2.getUniqueId());
                            if (path != null) {
                                Bukkit.getPlayer(sender.getName()).spigot().sendMessage(path.toTextComponent(s));
                            } else {
                                sender.sendMessage(s.getError() + "There is no path connection between " + s.getValue() + args[i] + s.getError() + " and " + s.getValue() + args[i + 1]);
                            }
                        } else {
                            sender.sendMessage(s.getError() + "You cannot compare the same person!");
                        }

                    } else {
                        sender.sendMessage(s.getError() + "Player " + args[i] + " does not exist");
                    }
                } else {
                    sender.sendMessage(s.getError() + "You must enter another player!");
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"lookup", "l"};
            }

            @Override
            public String getHelp() {
                return "<player> {distance}";
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[i]);
                if (target != null) {
                    int disance = 0;
                    if (args.length > i + 1) {
                        try {
                            disance = Integer.parseInt(args[i + 1]);
                        } catch (Exception e) {
                            sender.sendMessage(s.getError() + args[i + 1] + " is not a valid number");
                        }
                    }
                    Set<IPGraph.Path> list = IPGraph.getGraph().getAllPathsList(target.getUniqueId(), disance);
                    if (!list.isEmpty()) {
                        for (IPGraph.Path path : list) {
                            Bukkit.getPlayer(sender.getName()).spigot().sendMessage(path.toTextComponent(s));
                        }
                    } else {
                        sender.sendMessage(s.getError() + "Player has no alts within that scope!");
                    }
                } else {
                    sender.sendMessage(s.getError() + "Player does not exist!");
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"all"};
            }

            @Override
            public String getHelp() {
                return "<scope> <online,true|false>";
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                if (args.length > i + 1) {
                    int scope = Integer.parseInt(args[i]);
                    boolean online = Boolean.parseBoolean(args[i + 1].toLowerCase());
                    IPGraph graph = IPGraph.getGraph();
                    sender.sendMessage(s.getError() + "WARNING" + s.getColon() + ": " + s.getValue() + "This will take a few minutes to generate the paths! Do not rerun the command!");
                    new Thread() {
                        public void run() {
                            Set<IPGraph.Path> paths;
                            if (online)

                            {
                                paths = new HashSet<>();
                                for (Player player : Bukkit.getOnlinePlayers()) {
                                    paths.addAll(graph.getAllPathsList(player.getUniqueId(), scope));
                                }
                            } else

                            {
                                paths = graph.getAllPaths(scope);
                            }
                            if (paths != null)

                            {
                                for (IPGraph.Path path : paths) {
                                    if (path != null)
                                        sender.spigot().sendMessage(path.toTextComponent(s));
                                }
                                sender.sendMessage(s.getSuccess() + "SUCCESS" + s.getColon() + ": " + s.getValue() + "Finished searching the list");
                            } else

                            {
                                sender.sendMessage(s.getError() + "There are no alts in that scope!");
                            }
                        }
                    }.start();

                } else {
                    sender.sendMessage(s.getError() + "You must enter a scope and boolean value about weather to search online players only");
                }

            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"purge"};
            }

            @Override
            public String getHelp() {
                return "<ip>";
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                String ip = args[i];
                IPGraph graph = IPGraph.getGraph();
                EditorSettings s = EditorSettings.getSettings(sender);
                if (graph.purge(ip)) {
                    sender.sendMessage(s.getSuccess() + "Purged IP " + ip);
                } else {
                    sender.sendMessage(s.getError() + "The graph does not contain IP " + ip);
                }

            }
        }};
    }

    @Override
    public String getHelp() {
        return "The command to interface with IP addresses";
    }

}
