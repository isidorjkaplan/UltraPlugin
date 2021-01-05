package co.amscraft.networking.commands;

import co.amscraft.networking.Server;
import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.commands.Component;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerCommand extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"server"};
    }

    @Override
    public Component[] getComponents() {
        return new Component[]{UltraCommand.getCommand("servers"), UltraCommand.getCommand("glist"), new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"msg", "tell"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                new BukkitRunnable() {
                    public void run() {
                        try {
                            String message = "";
                            for (int index = i + 1; index < args.length; index++) {
                                message += " " + args[index];
                            }
                            if (sender.hasPermission("ultralib.commands.server.msg.color")) {
                                message = ChatColor.translateAlternateColorCodes('&', message);
                            }
                            EditorSettings s = EditorSettings.getSettings(sender);
                            if (Bukkit.getPlayer(args[i]) == null) {
                                Server server = Server.getServer(args[i]);
                                if (server != null) {
                                    if (server.isConnected()) {
                                        sender.sendMessage(s.getVariable() + "Me" + s.getColon() + " -> " + s.getVariable() + args[i] + s.getColon() + ": " + s.getValue() + message);
                                        server.sendMsg(Bukkit.getPlayer(sender.getName()), args[i], message);
                                    } else {
                                        sender.sendMessage(s.getError() + "Server " + server.getName() + " is offline!");
                                    }
                                } else {
                                    sender.sendMessage(s.getError() + "Server " + args[i] + " does not exist!");
                                }
                            } else {
                                sender.sendMessage(s.getVariable() + "Me" + s.getColon() + " -> " + s.getVariable() + args[i] + s.getColon() + ": " + s.getValue() + message);
                                s = EditorSettings.getSettings(Bukkit.getPlayer(args[i]));
                                Bukkit.getPlayer(args[i]).sendMessage(s.getVariable() + sender.getName() + s.getColon() + " -> " + s.getVariable() + "Me" + s.getColon() + ": " + s.getValue() + message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.runTaskAsynchronously(UltraLib.getInstance());
            }

            @Override
            public String getHelp() {
                return "<player> <message>";
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"ip"};
            }

            @Override
            public String getHelp() {
                return "<server>";
            }


            @Override
            public void run(CommandSender sender, String[] args, int i) {
                Server server = (Server) Server.getObject(Server.class, "name", args[i]);
                EditorSettings s = EditorSettings.getSettings(sender);
                if (server != null) {
                    sender.sendMessage(s.getVariable() + server.getName() + " ip" + s.getColon() + ": " + s.getValue() + server.getWordIP());
                } else {
                    sender.sendMessage(s.getError() + "Server " + args[i] + " does not exist!");
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"alert", "bcast", "broadcast"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                String message = "";
                for (int index = i + 1; index < args.length; index++) {
                    message += " " + args[index];
                }
                EditorSettings s = EditorSettings.getSettings(sender);
                Server server = UltraObject.getObject(Server.class, "name", args[i]);
                if (server != null && server.isConnected()) {
                    server.broadcast(server.getPlayers().toArray(new String[server.getPlayers().size()]), message);
                    sender.sendMessage(s.getSuccess() + "Successfully sent the broadcast");
                } else {
                    message = args[i] + " " + message;
                    for (Server server1 : Server.getServers()) {
                        if (server1.isConnected()) {
                            server1.broadcast(server1.getPlayers().toArray(new String[server1.getPlayers().size()]), message);
                        }
                    }
                    Bukkit.broadcastMessage(s.getColon() + "(" + s.getValue() + "broadcast" + s.getColon() + ")" + s.getValue() + ChatColor.translateAlternateColorCodes('&', message));
                }

            }
        }
        };
    }


    @Override
    public String getHelp() {
        return "Use the server command";
    }
}
