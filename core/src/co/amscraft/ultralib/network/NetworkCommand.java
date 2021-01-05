package co.amscraft.ultralib.network;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.commands.Component;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.Editor;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.network.exceptions.PacketTimedOutException;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultralib.utils.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class NetworkCommand extends UltraCommand {

    public static boolean canNetwork(CommandSender sender) {
        return sender.equals(Bukkit.getConsoleSender()) || NetworkListener.getConfig().getStringList("players").contains(Bukkit.getPlayer(sender.getName()).getUniqueId().toString());
    }

    @Override
    public String[] getAliases() {
        return new String[]{"network"};
    }

    @Override
    public Component[] getComponents() {
        return new Component[]{new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"export"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                if (canNetwork(sender)) {
                    if (args.length >= i + 4) {
                        Class<? extends UltraObject> type = UltraObject.getClass(args[i + 2]);
                        if (type != null) {
                            UltraObject object = ObjectUtils.parse(type, args[i + 3]);
                            if (object != null || args[i + 3].equalsIgnoreCase("all")) {
                                String ip = args[i];
                                int port = Integer.parseInt(args[i + 1]);
                                //List<NetworkConnection> connections = NetworkConnection.getConnections(ip, port);
                                NetworkConnection connection = NetworkConnection.getOrCreate(ip, port);
                                if (connection != null) {
                                    if (args.length >= i + 5) {
                                        connection.setEncryption(args[i + 4]);
                                    }
                                    if (connection.getEncryption() != null) {
                                        try {
                                            if (object != null) {
                                                connection.upload(object);
                                                sender.sendMessage(s.getSuccess() + "Successfully sent a packet containing " + type.getSimpleName().toLowerCase() + ": " + object);
                                            } else {
                                                for (UltraObject object1 : UltraObject.getList(type)) {
                                                    connection.upload(object1);
                                                }
                                                sender.sendMessage(s.getSuccess() + "Successfully sent " + UltraObject.getList(type).size() + " packets!");
                                            }
                                        } catch (IllegalArgumentException e) {
                                            sender.sendMessage(s.getError() + "You must enter a valid encryption key!");
                                        }
                                    } else {
                                        sender.sendMessage(s.getError() + "You must enter an encryption key!");
                                    }
                                    final NetworkConnection connection1 = connection;
                                    new BukkitRunnable() {
                                        public void run() {
                                            connection1.close();
                                        }
                                    }.runTaskLater(UltraLib.getInstance(), 20 * 60 * 3);
                                } else {
                                    sender.sendMessage(s.getError() + "The remote server rejected the connection!");
                                }
                            } else {
                                sender.sendMessage(s.getError() + "The " + type.getSimpleName() + " " + args[i + 3] + " does not exist!");
                            }
                        } else {
                            sender.sendMessage(s.getError() + "The UltraObject type " + args[i + 2] + " does not exist!");
                        }
                    } else {
                        sender.sendMessage(s.getError() + "You did not enter all the command arguments!");
                    }
                } else {
                    sender.sendMessage(s.getError() + "You are not authorized to use network ports!");
                }

            }

            @Override
            public String getHelp() {
                return "<server> <port> <UltraObjectType> <UltraObject> {remote_encryption_key}";
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"import"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                if (args[i] == null || !args[i].equalsIgnoreCase("all")) {
                    Editor editor = UltraPlayer.getPlayer(sender).getData(Editor.class);
                    editor.getEditing().add(UltraObject.class);
                    editor.getEditing().add(UltraObject.networkReceivedObjects);
                    editor.resend();
                } else {
                    EditorSettings s = EditorSettings.getSettings(sender);
                    new Thread() {
                        public void run() {
                            for (
                                    UltraObject object : UltraObject.networkReceivedObjects) {
                                object.save();
                            }
                            sender.sendMessage(s.getSuccess() + "Successfully saved " + UltraObject.networkReceivedObjects + " objects!");
                        }
                    }.start();
                    sender.sendMessage(s.getSuccess() + "Began importing objects!");
                    UltraObject.networkReceivedObjects.clear();
                }
            }

            @Override
            public String getHelp() {
                return "{all}";
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"list"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                String string = s.getVariable() + "Current network connections" + s.getColon() + ": " + s.getValue();
                if (NetworkConnection.connections.isEmpty()) {
                    string += "No Open Connections";
                }
                sender.sendMessage(string);
                for (NetworkConnection connection : NetworkConnection.connections) {
                    sender.sendMessage(s.getColon() + " - " + connection);
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"close"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                String ip = args[i];
                for (NetworkConnection connection1 : NetworkConnection.connections) {
                    if (connection1.getSocket().getInetAddress().getHostAddress().equals(ip)) {

                        sender.sendMessage(s.getSuccess() + "Closed connection " + connection1.getSocket());
                        connection1.close();
                        return;
                    }
                }
                sender.sendMessage(s.getError() + "Connection to " + ip + " does not exist!");


            }

            @Override
            public String getHelp() {
                return "<ip>";
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"authorize", "auth"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                if (canNetwork(sender)) {
                    FileConfiguration config = NetworkListener.getConfig();
                    UUID uuid = null;
                    if (Bukkit.getPlayer(args[i]) != null) {
                        uuid = Bukkit.getPlayer(args[i]).getUniqueId();
                    }
                    if (uuid == null) {
                        for (String s1 : config.getStringList("players")) {
                            if (Bukkit.getOfflinePlayer(UUID.fromString(s1)).getName().equalsIgnoreCase(args[i])) {
                                uuid = UUID.fromString(s1);
                                break;
                            }
                        }
                    }
                    if (uuid != null) {
                        List<String> players = config.getStringList("players");
                        if (players.contains(uuid.toString())) {
                            players.remove(uuid.toString());
                            sender.sendMessage(s.getSuccess() + "Successfully removed network authorization!");
                        } else {
                            players.add(uuid.toString());
                            sender.sendMessage(s.getSuccess() + "Successfully added network authorization!");
                        }
                        config.set("players", players);
                        try {
                            config.save(new File(UltraLib.getInstance().getDataFolder() + "/network.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        sender.sendMessage(s.getError() + "Player " + args[i] + " not found!");
                    }
                } else {
                    sender.sendMessage(s.getError() + "You are not authorized to network!");
                }
            }

            @Override
            public String getHelp() {
                return "<Player>";
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"ipwhitelist", "whitelist", "ipwl"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                if (canNetwork(sender)) {
                    String ip = args[i];
                    if (ip != null) {
                        List<String> whitelist = NetworkListener.getConfig().getStringList("whitelist");
                        if (whitelist.contains(ip)) {
                            whitelist.remove(ip);
                            sender.sendMessage(s.getSuccess() + "Successfully removed IP: " + ip);
                        } else {
                            whitelist.add(ip);
                            sender.sendMessage(s.getSuccess() + "Successfully added ip: " + ip);
                        }
                        FileConfiguration config = NetworkListener.getConfig();
                        config.set("whitelist", whitelist);
                        try {
                            config.save(new File(UltraLib.getInstance().getDataFolder() + "/network.yml"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        sender.sendMessage(s.getError() + "You must enter an ip!");
                    }

                } else {
                    sender.sendMessage(s.getError() + "You are not authorized to network!");
                }
            }

            @Override
            public String getHelp() {
                return "<IP>";
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"key"};
            }

            @Override
            public Component[] getComponents() {
                return new Component[]{new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"get"};
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        EditorSettings s = EditorSettings.getSettings(sender);
                        if (canNetwork(sender)) {
                            sender.sendMessage(s.getVariable() + "Encryption Key" + s.getColon() + ": " + s.getValue() + NetworkConnection.getEncryptionKeyAsString(NetworkConnection.getEncryptionKey()));
                        } else {
                            sender.sendMessage(s.getError() + "You are not authorized to network!");
                        }
                    }
                }, new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"regen"};
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        EditorSettings s = EditorSettings.getSettings(sender);
                        if (canNetwork(sender)) {
                            NetworkConnection.regenEncryptionKey();
                            sender.sendMessage(s.getSuccess() + "Regenerated the encryption key!");
                            Bukkit.dispatchCommand(sender, "network key get");
                        } else {
                            sender.sendMessage(s.getError() + "You are not authorized to network!");
                        }
                    }
                }};
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"ping"};
            }

            @Override
            public String getHelp() {
                return "<ip> <port>";
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                new BukkitRunnable() {
                    public void run() {
                        EditorSettings s = EditorSettings.getSettings(sender);
                        if (canNetwork(sender)) {
                            String ip = args[i];
                            if (ip != null) {
                                int port = Integer.parseInt(args[i + 1]);
                                NetworkConnection connection = NetworkConnection.getOrCreate(ip, port);
                                try {
                                    sender.sendMessage(s.getSuccess() + "Response received in " + connection.ping() + " seconds");
                                } catch (PacketTimedOutException e) {
                                    sender.sendMessage(s.getError() + "The ping request timed out!");
                                }
                            } else {
                                sender.sendMessage(s.getError() + "You must enter an ip!");
                            }
                        } else {
                            sender.sendMessage(s.getError() + "You are not authorized to use network ports!");
                        }
                    }
                }.runTaskAsynchronously(UltraLib.getInstance());
            }
        }};
    }

    @Override
    public String getHelp() {
        return "Accessing the network commands!";
    }
}
