package co.amscraft.ultrachat;

import co.amscraft.ultralib.commands.Component;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ChannelCommand extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"channel", "c", "chat"};
    }

    @Override
    public Component[] getComponents() {
        return new Component[]{new Component() {

            @Override
            public String getHelp() {
                return "<channel>";
            }

            @Override
            public String[] getAliases() {
                return new String[]{"switch", "s", "join"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                Channel channel = Channel.getChannel(args[i]);
                if (channel != null) {
                    UltraPlayer player = UltraPlayer.getPlayer(sender);
                    if (!channel.getPlayers().contains(player)) {
                        boolean bypass = UltraPlayer.getPlayer(sender).hasPermission("ultralib.commands.channel.switch.bypass");
                        if (UltraPlayer.getPlayer(sender).hasPermission(channel.getPermission()) || bypass) {
                            if (!channel.isLocked() || bypass) {
                                if (!channel.isFull() || bypass) {
                                    player.getData(ChatData.class).setChannel(channel);
                                    sender.sendMessage(s.getSuccess() + "You have joined channel: " + channel.getName());
                                    for (UltraPlayer p : UltraPlayer.getPlayers()) {
                                        EditorSettings s1 = p.getData(EditorSettings.class);
                                        p.getBukkit().sendMessage(s1.getVariable() + player.getBukkit().getName() + s1.getHelp() + " has joined channel " + s1.getValue() + channel.getName());
                                    }
                                } else {
                                    sender.sendMessage(s.getError() + "Channel " + channel.getName() + " is at maximum capacity!");
                                }
                            } else {
                                sender.sendMessage(s.getError() + "The channel you are trying to enter is locked!");
                            }
                        } else {
                            sender.sendMessage(s.getError() + "You do not have permission to enter this channel!");
                        }
                    } else {
                        sender.sendMessage(s.getError() + "You are already in channel " + channel.getName());
                    }
                } else {
                    sender.sendMessage(s.getError() + "Channel " + args[i] + " does not exist!");
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"list", "l"};
            }

            @Override
            public String getHelp() {
                return "{channel}";
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                if (args[i] == null) {
                    sender.sendMessage(s.getVariable() + "Channels" + s.getColon() + ": " + s.getValue() + Channel.getChannels());
                } else {
                    Channel channel = Channel.getChannel(args[i]);
                    if (channel != null) {
                        List<String> players = new ArrayList<String>();
                        for (UltraPlayer player : channel.getPlayers()) {
                            players.add(player.getBukkit().getName());
                        }
                        sender.sendMessage(s.getHelp() + "Players in channel " + s.getVariable() + channel.getName() + s.getColon() + ": " + players.toString());
                    } else {
                        sender.sendMessage(s.getError() + "Channel " + args[i] + " does not exist!");
                    }
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"format"};
            }

            @Override
            public String getHelp() {
                return "{true/false}";
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                boolean format = (args[i] != null ? args[i].equalsIgnoreCase("true") : !UltraPlayer.getPlayer(sender).getData(ChatData.class).format);
                UltraPlayer.getPlayer(sender).getData(ChatData.class).format = format;
                sender.sendMessage(s.getSuccess() + "You have " + (format ? "enabled" : "disabled") + " the chat auto-punctuate!");
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"spy"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                if (args[i] != null && args[i].equalsIgnoreCase("list")) {
                    List<String> spies = new ArrayList<>();
                    for (UltraPlayer spy : ChatData.getSpies()) {
                        spies.add(spy.getBukkit().getName());
                    }
                    sender.sendMessage(s.getVariable() + "Spies" + s.getColon() + ": " + s.getValue() + spies);
                } else {
                    boolean spy = (args[i] != null ? args[i].equalsIgnoreCase("on") : !UltraPlayer.getPlayer(sender).getData(ChatData.class).isSpying());
                    UltraPlayer.getPlayer(sender).getData(ChatData.class).spy = spy;
                    sender.sendMessage(s.getSuccess() + "You have " + (spy ? "enabled" : "disabled") + " spy mode!");
                }
            }

            @Override
            public String getHelp() {
                return "<on/off/list>";
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"color", "c"};
            }

            @Override
            public String getHelp() {
                return "<color|clear> {player}";
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                UltraPlayer target = UltraPlayer.getPlayer(sender);
                if (args.length > i + 1) {
                    target = UltraPlayer.getPlayer(args[i + 1]);
                    if (target == null) {
                        sender.sendMessage(s.getError() + "Player " + args[i + 1] + " is not online!");
                        return;
                    }
                }
                if (args[i] != null) {
                    ChatData data = target.getData(ChatData.class);
                    if (args[i].equalsIgnoreCase("clear")) {
                        data.color = "";
                        sender.sendMessage(s.getSuccess() + "Successfully cleared " + target.getBukkit().getName() + "'s chat color");
                    } else if (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', args[i])).isEmpty()) {
                        data.color = args[i];
                        sender.sendMessage(s.getSuccess() + "Successfully changed " + target.getBukkit().getName() + "'s chat color to: " + data.getColor() + "this");
                    } else {
                        //TODO Entered non color code
                        sender.sendMessage(s.getError() + "You must enter only color codes!");
                    }
                } else {
                    sender.sendMessage(s.getError() + "You must enter a color code");
                    //TODO Entered blank
                }
            }
        }};
    }

    @Override
    public String getHelp() {
        return "The command to use channels";
    }
}



