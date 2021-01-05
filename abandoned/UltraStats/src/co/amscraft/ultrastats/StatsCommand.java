package co.amscraft.ultrastats;

import co.amscraft.ultralib.commands.Component;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Izzy on 2017-11-12.
 */
public class StatsCommand extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"stats", "ustats", "us"};
    }

    @Override
    public Component[] getComponents() {
        return new Component[]{new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"info", "i"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings settings = UltraPlayer.getPlayer(sender).getData(EditorSettings.class);
                UltraPlayer player = UltraPlayer.getPlayer(sender.getName());
                try {
                    if (Bukkit.getPlayer(args[i]) != null) {
                        player = UltraPlayer.getPlayer(args[i]);
                    }
                } catch (Exception e) {

                }
                sender.sendMessage(settings.getVariable() + "Your unlocked stats: ");
                for (PlayerStat stat : player.getData(StatsData.class).getStats()) {
                    sender.sendMessage(settings.getVariable() + "  Stat" + settings.getColon() + ": " + settings.getValue() + stat.getStat().name);
                    sender.sendMessage(settings.getVariable() + "  Level" + settings.getColon() + ": " + settings.getValue() + stat.level);
                    sender.sendMessage(settings.getVariable() + "  Exp" + settings.getColon() + ": " + settings.getValue() + stat.exp + "/" + stat.getExpTillLevel());
                    sender.sendMessage(settings.getVariable() + "  Spells" + settings.getColon() + ": " + stat.getSpells());
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"list", "l"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                List<String> stats = new ArrayList<>();
                for (Stat stat : Stat.getList(Stat.class)) {
                    stats.add(stat.name);
                }
                EditorSettings settings = EditorSettings.getSettings(sender);
                sender.sendMessage(settings.getVariable() + "Stats" + settings.getColon() + ": " + settings.getValue() + stats);
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"admin"};
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
                        EditorSettings settings = UltraPlayer.getPlayer(sender).getData(EditorSettings.class);
                        Stat stat = Stat.getObject(Stat.class, "name", args[i]);
                        if (stat != null) {
                            StatsData data = UltraPlayer.getPlayer(sender.getName()).getData(StatsData.class);
                            try {
                                if (Bukkit.getPlayer(args[i + 1]) != null) {
                                    data = UltraPlayer.getPlayer(args[i + 1]).getData(StatsData.class);
                                }
                            } catch (Exception e) {

                            }
                            if (!data.hasStat(stat)) {
                                data.addStat(new PlayerStat(UltraPlayer.getPlayer(sender), stat));
                                data.save();
                                sender.sendMessage(settings.getValue() + "You have successfully added the stat '" + stat.name + "' to player '" + data.getPlayer().getBukkit().getName() + "!");
                            } else {
                                sender.sendMessage(settings.getHelp() + "That player already has that stat!");
                            }
                        } else {
                            sender.sendMessage(settings.getHelp() + "Stat '" + args[i] + "' does not exist!");
                        }
                    }
                }, new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"remove", "rm"};
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        EditorSettings settings = UltraPlayer.getPlayer(sender).getData(EditorSettings.class);
                        Stat stat = Stat.getObject(Stat.class, "name", args[i]);
                        if (stat != null) {
                            StatsData data = UltraPlayer.getPlayer(sender.getName()).getData(StatsData.class);
                            try {
                                if (Bukkit.getPlayer(args[i + 1]) != null) {
                                    data = UltraPlayer.getPlayer(args[i + 1]).getData(StatsData.class);
                                }
                            } catch (Exception e) {

                            }
                            if (data.hasStat(stat)) {
                                data.removeStat(stat);
                                data.save();
                                sender.sendMessage(settings.getValue() + "You have successfully removed the stat '" + stat.name + "' to player '" + data.getPlayer().getBukkit().getName() + "!");
                            } else {
                                sender.sendMessage(settings.getHelp() + "That player does not have that stat!");
                            }
                        } else {
                            sender.sendMessage(settings.getHelp() + "Stat '" + args[i] + "' does not exist!");
                        }
                    }

                }, new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"clear", "c"};
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        UltraPlayer player = UltraPlayer.getPlayer(sender);
                        if (Bukkit.getPlayer(args[i]) != null) {
                            player = UltraPlayer.getPlayer(args[i]);
                        }
                        player.getData(StatsData.class).stats = new ArrayList<>();
                        player.getData(StatsData.class).save();
                        EditorSettings settings = UltraPlayer.getPlayer(sender).getData(EditorSettings.class);
                        sender.sendMessage(settings.getHelp() + "Successfully cleared stats of player: " + player.getBukkit().getName());
                    }
                }};
            }
        }};
    }

    @Override
    public String getHelp() {
        return "The stats command!";
    }
}
