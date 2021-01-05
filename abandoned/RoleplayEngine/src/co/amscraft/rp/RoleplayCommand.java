package co.amscraft.rp;

import co.amscraft.ultralib.commands.Component;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultralib.utils.ObjectUtils;
import io.netty.util.internal.ObjectUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class RoleplayCommand extends UltraCommand {

    @Override
    public String[] getAliases() {
        return new String[]{"RP"};
    }

    @Override
    public Component[] getComponents() {
        return new Component[]{new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"set"};
            }

            @Override
            public Component[] getComponents() {
                return new Component[]{new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"age"};
                    }

                    @Override
                    public String getHelp() {
                        return "<player> <day/month/year>";
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        EditorSettings s = EditorSettings.getSettings(sender);
                        UltraPlayer target = UltraPlayer.getPlayer(args[i]);
                        if (target != null) {
                            String string = args[i+1];
                            if (string != null) {
                                try {
                                    String[] strings = string.split("/");
                                    int day = Integer.parseInt(strings[0]);
                                    int year = Integer.parseInt(strings[2]);
                                    String month = strings[1];
                                    target.getData(RoleplayData.class).setAge(day, month, year);
                                    sender.sendMessage(s.getSuccess() + "Successfully set " + args[i] + "'s birthday to " + day + "/" + month + "/" + year);
                                } catch (NumberFormatException e) {
                                    sender.sendMessage(s.getError() + "You must enter a valid day and year in the form of an integer");
                                } catch (IndexOutOfBoundsException e) {
                                    sender.sendMessage(s.getError() + "You must enter a month, day, and year in the form day/month/year, i.e 25/December/2001");
                                }
                            }
                        } else {
                            sender.sendMessage(s.getError() + "Player " + args[i] + " is not online!");
                        }
                    }
                },new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"backstory"};
                    }

                    @Override
                    public String getHelp() {
                        return "<player> <backstory pastebin key>";
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        EditorSettings s = EditorSettings.getSettings(sender);
                        UltraPlayer target = UltraPlayer.getPlayer(args[i]);
                        if (target != null) {
                            if (args.length > i+1) {
                                String key = getPastebinKey(args[i+1]);
                                if (key != null) {
                                    RoleplayData data = target.getData(RoleplayData.class);
                                    data.setBackstory(key);
                                    sender.sendMessage(s.getSuccess() + "Successfully set " + target.getBukkit().getName() + "'s backstory to:" + key);
                                } else {
                                    sender.sendMessage(s.getError() + "The key you entered does not point to a valid pastebin post!");
                                }
                            } else {
                                sender.sendMessage(s.getError() + "You must enter a key!");
                            }
                        } else {
                            sender.sendMessage(s.getError() + "Player " + args[i] + " is not online!");
                        }
                    }
                },new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"power"};
                    }

                    @Override
                    public String getHelp() {
                        return "<player> <RoleplayPower> <Level|null>";
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        EditorSettings s = EditorSettings.getSettings(sender);
                        UltraPlayer target = UltraPlayer.getPlayer(args[i]);
                        if (target != null) {
                            if (args.length > i+1) {
                                RoleplayPower power = RoleplayPower.getPower(args[i+1]);
                                if (power != null) {
                                    if (args.length > i + 2) {
                                        RoleplayPower.PowerLevel level = RoleplayPower.PowerLevel.getPowerLevel(args[i+2]);
                                        if (level != null || args[i+2].equals("null")) {
                                            RoleplayData data = target.getData(RoleplayData.class);
                                            data.setPower(power, level);
                                            sender.sendMessage(s.getSuccess() + "Successfully set power " + s.getVariable() + power.getName() + s.getSuccess() + " to level " + s.getValue() + level);
                                        } else {
                                            sender.sendMessage(s.getError() + "You must enter one of the following power levels" + s.getColon() + ": " + s.getValue() + Arrays.asList(RoleplayPower.PowerLevel.values()));
                                        }
                                    } else {
                                        sender.sendMessage(s.getError() + "You must enter one of the following powers" + s.getColon() + ": " + s.getValue() + Arrays.asList(RoleplayPower.PowerLevel.values()));
                                    }
                                } else {
                                    sender.sendMessage(s.getError() + "You did not enter a valid power!");
                                }
                            } else {
                                sender.sendMessage(s.getError() + "You must enter one of the following powers" + s.getColon() + ":" + s.getValue() + RoleplayPower.getList(RoleplayPower.class));
                            }
                        } else {
                            sender.sendMessage(s.getError() + "Player " + args[i] + " is not online!");
                        }
                    }
                }, new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"forum"};
                    }

                    @Override
                    public String getHelp() {
                        return "<player> <backstory link>";
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        EditorSettings s = EditorSettings.getSettings(sender);
                        UltraPlayer target = UltraPlayer.getPlayer(args[i]);
                        if (target != null) {
                            if (args.length > i+1) {
                                String link = args[i+1];
                                    RoleplayData data = target.getData(RoleplayData.class);
                                    data.setLink(link);
                                    sender.sendMessage(s.getSuccess() + "Successfully set " + target.getBukkit().getName() + "'s profile to:" + link);

                            } else {
                                sender.sendMessage(s.getError() + "You must enter a key!");
                            }
                        } else {
                            sender.sendMessage(s.getError() + "Player " + args[i] + " is not online!");
                        }
                    }
                }};
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"relation"};
            }

            @Override
            public String getHelp() {
                return "<player> <relation>";
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                if (args[i] != null && Bukkit.getPlayer(args[i]) != null) {
                        RoleplayData.Relation r = RoleplayData.Relation.getRelation(args.length>i+1?args[i + 1]:"");
                        if (r != null) {
                            Player target = Bukkit.getPlayer(args[i]);
                            //RoleplayData tData = target.getData(RoleplayData.class);
                            RoleplayData data = UltraPlayer.getPlayer(sender).getData(RoleplayData.class);
                            data.setRelationship(target.getUniqueId(), r);
                            sender.sendMessage(s.getSuccess() + "You have set " + s.getVariable() + target.getName() + s.getSuccess() + " to a " + s.getValue() + r.toString().toLowerCase());
                        } else {
                            sender.sendMessage(s.getError() + "You must enter one of the following relations" + s.getColon() + ": " + s.getValue() + Arrays.asList(RoleplayData.Relation.values()));
                        }
                } else {
                    sender.sendMessage(s.getError() + "Player " + args[i] + " is not online!");
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"card", "book"};
            }

            @Override
            public String getHelp() {
                return "{player}";
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                UltraPlayer target;
                if (args[i] == null) {
                    target = UltraPlayer.getPlayer(sender);
                } else {
                    target = UltraPlayer.getPlayer(args[i]);
                }
                if (target != null) {
                    RoleplayData data = target.getData(RoleplayData.class);
                    ItemStack book = data.getCard(s);
                    Bukkit.getPlayer(sender.getName()).getInventory().addItem(book);
                    sender.sendMessage(s.getSuccess() + "Successfully given Player Card");
                } else {
                    sender.sendMessage(s.getError() + "Player " + args[i] + " is not online!");
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"info"};
            }
            @Override
            public String getHelp() {
                return "<player> {page}";
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                UltraPlayer target = UltraPlayer.getPlayer(args[i]);
                if (target != null) {
                    try {
                        int page = 1;
                        if (i+1 < args.length) {
                            page = Integer.parseInt(args[i+1]);
                        }
                        RoleplayData data = target.getData(RoleplayData.class);
                        data.sendCard(sender, page);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(s.getError() + "You must enter a valid number");
                    }
                } else {
                    sender.sendMessage(s.getError() + "Player " + args[i] + " is not online!");
                }
            }
        }};
    }

    private static String getPastebinKey(String URL) {
        String key = URL.substring(URL.lastIndexOf('/')+1);
        return ObjectUtils.getUrlContents("https://pastebin.com/raw/" + key) != null?key:null;
    }


    @Override
    public String getHelp() {
        return "The command to edit your roleplay information";
    }
}
