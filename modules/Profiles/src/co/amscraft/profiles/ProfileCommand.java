package co.amscraft.profiles;

import co.amscraft.ultralib.commands.Component;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.PlayerUtility;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ProfileCommand extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"profile"};
    }

    @Override
    public String getHelp() {
        return "Switch your profile";
    }

    @Override
    public Component[] getComponents() {
        return new Component[]{new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"list"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                List<String> profiles = new ArrayList<>();
                EditorSettings s = EditorSettings.getSettings(sender);
                UltraPlayer target = UltraPlayer.getPlayer(sender);
                if (args[i] != null) {
                    target = UltraPlayer.getPlayer(args[i]);
                    if (target == null) {
                        sender.sendMessage(s.getError() + "Player " + args[i] + " is not online!");
                        return;
                    }
                }
                ProfileData data = target.getData(ProfileData.class);
                sender.sendMessage(s.getVariable() + "Current Profile" + s.getColon() + ": " + s.getValue() + (data.getProfiles().isEmpty() ? "No Profiles" : data.getCurrentProfile().name));
                if (!data.getProfiles().isEmpty()) {
                    for (Profile profile : data.getProfiles()) {
                        profiles.add(profile.name);
                    }
                    sender.sendMessage(s.getHelp() + profiles.toString());
                }
            }

            @Override
            public String getHelp() {
                return "{player}";
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"switch"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                UltraPlayer player = UltraPlayer.getPlayer(sender);
                EditorSettings s = EditorSettings.getSettings(sender);
                Profile profile = player.getData(ProfileData.class).getProfile(args[i]);
                if (profile != null) {
                    if (!profile.isEnabled()) {
                        String fail = profile.getFailMessage();
                        if (fail == null) {
                            profile.enable();
                            player.getData(PlayerUtility.class).setCooldown("profile", 20);
                            player.getBukkit().sendMessage(s.getSuccess() + "You have switched profiles");
                        } else {
                            //TODO send player fail message
                            player.getBukkit().sendMessage(s.getError() + fail);
                        }
                    } else {
                        //TODO CURRENT PROFILE
                        player.getBukkit().sendMessage(s.getError() + "You are already on profile: " + profile.name);
                    }
                } else {
                    //TODO INVALID PROFILE
                    player.getBukkit().sendMessage(s.getError() + "Invalid profile name: " + args[i]);
                }
            }

            @Override
            public String getHelp() {
                return "<profile>";
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"rename", "name"};
            }

            @Override
            public String getHelp() {
                return "<profile> <new name>";
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                UltraPlayer player = UltraPlayer.getPlayer(sender);
                EditorSettings s = EditorSettings.getSettings(sender);
                if (args.length > i + 1) {
                    ProfileData data = player.getData(ProfileData.class);
                    Profile profile = data.getProfile(args[i]);
                    if (profile != null) {
                        if (data.getProfile(args[i + 1]) == null) {
                            profile.name = args[i + 1];
                            sender.sendMessage(s.getSuccess() + "Successfully renamed profile " + args[i] + " to " + args[i + 1]);
                        } else {
                            sender.sendMessage(s.getError() + "Profile " + args[i + 1] + " already exists");
                        }
                    } else {
                        sender.sendMessage(s.getError() + "Profile " + args[i] + " does not exist!");
                    }
                } else {
                    sender.sendMessage(s.getError() + "You did not enter enough command arguments!");
                }

            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"admin", "a"};
            }

            @Override
            public Component[] getComponents() {
                return new Component[]{new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"create", "new"};
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        UltraPlayer player = UltraPlayer.getPlayer(sender);
                        EditorSettings s = EditorSettings.getSettings(sender);
                        if (args[i] != null) {
                            UltraPlayer target = player;
                            if (args.length > i + 1) {
                                target = UltraPlayer.getPlayer(args[i + 1]);
                                if (target == null) {
                                    sender.sendMessage(s.getError() + "Player " + args[i + 1] + " is not online!");
                                    return;
                                }
                            }
                            if (target.getData(ProfileData.class).getProfile(args[i]) == null) {
                                target.getData(ProfileData.class).getCurrentProfile().save();
                                Profile profile = target.getData(ProfileData.class).newProfile(args[i]);
                                Bukkit.dispatchCommand(sender, "profile switch " + args[i]);

                            } else {
                                sender.sendMessage(s.getError() + "Profile " + args[i] + " already exists!");
                            }
                        } else {
                            sender.sendMessage(s.getError() + "You must enter a valid profile name");
                        }
                    }

                    @Override
                    public String getHelp() {
                        return "<name> {player}";
                    }
                }, new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"delete", "remove"};
                    }

                    @Override
                    public String getHelp() {
                        return "<profile> {player}";
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        UltraPlayer player = UltraPlayer.getPlayer(sender);
                        EditorSettings s = EditorSettings.getSettings(sender);
                        if (args[i] != null) {
                            UltraPlayer target = player;
                            if (args.length > i + 1) {
                                target = UltraPlayer.getPlayer(args[i + 1]);
                                if (target == null) {
                                    sender.sendMessage(s.getError() + "Player " + args[i + 1] + " is not online!");
                                    return;
                                }
                            }
                            Profile profile = target.getData(ProfileData.class).getProfile(args[i]);
                            if (profile != null) {
                                profile.delete();
                                sender.sendMessage(s.getSuccess() + "Successfully removed profile: " + profile.name);
                            } else {
                                sender.sendMessage(s.getError() + "Player " + target.getBukkit().getName() + " does not have a profile named " + args[i]);
                            }
                        } else {
                            sender.sendMessage(s.getError() + "You must enter a valid profile name");
                        }
                    }
                }, new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"lock", "l"};
                    }

                    @Override
                    public String getHelp() {
                        return "<player> <profile> {seconds}";
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        UltraPlayer target = UltraPlayer.getPlayer(args[i]);
                        EditorSettings s = EditorSettings.getSettings(sender);
                        if (target != null) {
                            if (args.length > i + 1) {
                                Profile profile = target.getData(ProfileData.class).getProfile(args[i + 1]);
                                if (profile != null) {
                                    String fail = profile.getFailMessage();
                                    if (fail == null) {
                                        if (!profile.isEnabled()) {
                                            profile.enable();
                                        }
                                        double time = 60;
                                        if (args.length > i + 2) {
                                            try {
                                                time = Double.parseDouble(args[i + 2]);
                                            } catch (NullPointerException | NumberFormatException e) {
                                                sender.sendMessage(s.getError() + "Invalid time! Using " + time + " seconds instead!");
                                            }
                                        }
                                        target.getData(PlayerUtility.class).setCooldown("profile", time);
                                        sender.sendMessage(s.getSuccess() + "Locked player " + target.getBukkit().getName() + " to profile " + profile.name + " for " + time);

                                    } else {
                                        //TODO send player fail message
                                        sender.sendMessage(s.getError() + fail);
                                    }
                                } else {
                                    //TODO INVALID PROFILE
                                    sender.sendMessage(s.getError() + "Profile " + args[i + 1] + " does not exist!");
                                }
                            } else {
                                sender.sendMessage(s.getError() + "You must enter a valid profile name!");
                                //TODO MUST ENTER PROFILE
                            }
                        } else {
                            sender.sendMessage(s.getError() + "Player " + args[i] + " is not online!");
                            //TODO NULL TARGET
                        }
                    }
                }, new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"unlock", "ul"};
                    }

                    @Override
                    public String getHelp() {
                        return "<player>";
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        UltraPlayer target = UltraPlayer.getPlayer(args[i]);
                        EditorSettings s = EditorSettings.getSettings(sender);
                        if (target != null) {
                            PlayerUtility utils = target.getData(PlayerUtility.class);
                            if (utils.getCooldown("profile") > 0) {
                                sender.sendMessage(s.getSuccess() + "You have unlocked player " + args[i] + "'s profiles! They can now switch profiles.");
                            } else {
                                sender.sendMessage(s.getError() + "Player " + args[i] + " is not locked!");
                            }
                            utils.setCooldown("profile", 0);
                        } else {
                            sender.sendMessage(s.getError() + "Player " + args[i] + " is not online!");
                            //TODO Null Target
                        }
                    }
                }};
            }
        }};
    }
}
