package co.amscraft.ontime;

import co.amscraft.ultralib.commands.Component;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OntimeCommand extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"ontime"};
    }

    @Override
    public void run(CommandSender sender, String[] args, int i) {
        UltraPlayer player = UltraPlayer.getPlayer(sender);
        UltraPlayer target = player;
        if (args[i] != null) {
            target = UltraPlayer.getPlayer(args[i]);
        }
        EditorSettings s = player.getData(EditorSettings.class);
        if (target != null) {
            OntimeData data = target.getData(OntimeData.class);
            sender.sendMessage(s.getVariable() + "Total Ontime" + s.getColon() + ": " + s.getValue() + OntimeData.format(data.getTotalOntime()));
            sender.sendMessage(s.getVariable() + "Daily Ontime" + s.getColon() + ": " + s.getValue() + OntimeData.format(data.getDailyOntime()));
            sender.sendMessage(s.getVariable() + "Current Session" + s.getColon() + ": " + s.getValue() + OntimeData.format(data.getCurrentOntime()));
        } else {
            sender.sendMessage(s.getError() + "Player " + args[i] + " is not online!");
        }
        super.run(sender, args, i);
    }

    @Override
    public Component[] getComponents() {
        return new Component[]{new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"reward"};
            }

            @Override
            public Component[] getComponents() {
                return new Component[]{new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"add", "a"};
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        EditorSettings s = EditorSettings.getSettings(sender);
                        try {
                            int time = Integer.parseInt(args[i]);
                            HashMap<Integer, List<String>> map = OnTime.getOntimeConfig().dailyRewards;
                            List<String> list = map.getOrDefault(time, new ArrayList<>());
                            String command = "";
                            for (int index = i + 1; index < args.length; index++) {
                                command += args[index] + " ";
                            }
                            if (command.endsWith(" ")) {
                                command = command.substring(0, command.length() - 1);
                            }
                            if (!command.equals("")) {
                                list.add(command);
                                map.put(time, list);
                                OnTime.saveConfig();
                                sender.sendMessage(s.getSuccess() + "You have added the command!");
                            } else {
                                sender.sendMessage(s.getError() + "You must enter a command!");
                            }
                        } catch (NumberFormatException | NullPointerException | IndexOutOfBoundsException e) {
                            sender.sendMessage(s.getError() + "You must enter /ontime reward add <time in seconds> <command>, you may use <player> as an argument");
                        }
                    }

                    @Override
                    public String getHelp() {
                        return "<time> <command>: You may use <player> as an argument";
                    }
                }, new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"remove", "rm"};
                    }


                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        EditorSettings s = EditorSettings.getSettings(sender);
                        try {
                            int time = Integer.parseInt(args[i]);
                            HashMap<Integer, List<String>> map = OnTime.getOntimeConfig().dailyRewards;
                            String command = "";
                            for (int index = i + 1; index < args.length; index++) {
                                command += args[index] + " ";
                            }
                            if (command.endsWith(" ")) {
                                command = command.substring(0, command.length() - 1);
                            }
                            List<String> list = map.getOrDefault(time, new ArrayList<>());
                            if (list.contains(command)) {
                                list.remove(command);
                                map.put(time, list);
                                if (list.isEmpty()) {
                                    map.remove(time);
                                }
                                OnTime.saveConfig();
                                sender.sendMessage(s.getSuccess() + "You have successfully removed the command!");
                            } else {
                                sender.sendMessage(s.getError() + "That command is not on the list");
                            }
                        } catch (NumberFormatException | NullPointerException | IndexOutOfBoundsException e) {
                            sender.sendMessage(s.getError() + "You must enter /ontime reward remove <time in seconds> <command>, you may use <player> as an argument");
                        }
                    }

                    @Override
                    public String getHelp() {
                        return "<time> <command>: You may use <player> as an argument";
                    }

                }, new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"list"};
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        EditorSettings s = EditorSettings.getSettings(sender);
                        HashMap<Integer, List<String>> map = OnTime.getOntimeConfig().dailyRewards;
                        if (args[i] == null) {

                            for (int key : map.keySet()) {
                                sender.sendMessage(s.getVariable() + key + s.getHelp() + " seconds");
                                for (String cmd : map.get(key)) {
                                    sender.sendMessage(s.getColon() + " - " + s.getValue() + cmd);
                                }
                            }
                        } else {
                            try {
                                int key = Integer.parseInt(args[i]);
                                if (!map.containsKey(key)) {
                                    sender.sendMessage(s.getError() + "There are no commands at that time!");
                                } else {
                                    sender.sendMessage(s.getVariable() + key + s.getHelp() + " seconds");
                                    for (String cmd : map.get(key)) {
                                        sender.sendMessage(s.getColon() + " - " + s.getValue() + cmd);
                                    }
                                }
                            } catch (NumberFormatException e) {
                                sender.sendMessage(s.getError() + "Enter a valid time!");
                            }
                        }
                    }

                    @Override
                    public String getHelp() {
                        return "{time}";
                    }
                }};
            }
        }};
    }

    @Override
    public String getHelp() {
        return "{player}";
    }
}
