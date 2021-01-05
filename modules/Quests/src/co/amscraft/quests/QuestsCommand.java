package co.amscraft.quests;

import co.amscraft.quests.player.QuestInstance;
import co.amscraft.quests.player.QuestsData;
import co.amscraft.ultralib.commands.Component;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.UltraPlayer;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QuestsCommand extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"quests", "uquests", "quest"};
    }

    @Override
    public String getHelp() {
        return "The command to access quests";
    }

    @Override
    public Component[] getComponents() {
        return new Component[]{new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"take", "t"};
            }

            @Override
            public String getHelp() {
                return "<quest>";
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                Quest quest = Quest.getQuest(args[i]);
                EditorSettings s = EditorSettings.getSettings(sender);
                if (quest != null) {
                    if (quest.getRequirement().meetsRequirements(Bukkit.getPlayer(sender.getName()))) {
                        QuestsData data = UltraPlayer.getPlayer(sender).getData(QuestsData.class);
                        if (data.getQuestInstance(quest) == null) {
                            if (CitizensAPI.getNPCRegistry().getById(quest.getNPC()) == null) {
                                data.addPending(quest);
                            } else {
                                sender.sendMessage(s.getError() + "To take this quest you must talk to " + CitizensAPI.getNPCRegistry().getById(quest.getNPC()).getName());
                            }
                        } else {
                            sender.sendMessage(s.getError() + "You are already on this quest!");
                        }
                    } else {
                        sender.sendMessage(s.getError() + quest.getRequirement().getFailMessage(UltraPlayer.getPlayer(sender)));
                    }
                } else {
                    sender.sendMessage(s.getError() + "Quest '" + args[i] + "' does not exist!");
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"stats", "s", "history"};
            }

            @Override
            public String getHelp() {
                return "{player}";
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                UltraPlayer player = args[i] == null ? UltraPlayer.getPlayer(sender) : UltraPlayer.getPlayer(args[i]);
                EditorSettings s = EditorSettings.getSettings(sender);
                if (player != null) {
                    QuestsData data = player.getData(QuestsData.class);
                    sender.sendMessage(s.getHelp() + "Completed Quests" + s.getColon() + ": " + s.getValue() + (data.completed.isEmpty() ? "none" : ""));
                    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                    for (int id : data.completed.keySet()) {
                        Quest quest = Quest.getQuest(id);
                        if (quest != null) {
                            Date date = new Date(data.completed.get(id));
                            String string = sdf.format(date);
                            sender.sendMessage(s.getVariable() + quest.getName() + s.getHelp() + " on " + s.getValue() + string);
                        }
                    }
                } else {
                    sender.sendMessage(s.getError() + "Player " + args[i] + " is not online!");
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"help", "h"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                UltraPlayer player = UltraPlayer.getPlayer(sender);
                EditorSettings s = player.getData(EditorSettings.class);
                QuestsData data = player.getData(QuestsData.class);
                if (!data.quests.isEmpty()) {
                    for (QuestInstance instance : data.quests) {
                        instance.sendDisplay(player);
                    }
                } else {
                    sender.sendMessage(s.getError() + "You are not on any quests!");
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"quit"};
            }

            @Override
            public String getHelp() {
                return "<quest>";
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                Quest quest = Quest.getQuest(args[i]);
                UltraPlayer player = UltraPlayer.getPlayer(sender);
                EditorSettings s = player.getData(EditorSettings.class);
                if (quest != null) {
                    if (player.getData(QuestsData.class).getQuestInstance(quest) != null) {
                        player.getData(QuestsData.class).getQuestInstance(quest).endAttempt(false);
                    } else {
                        sender.sendMessage(s.getError() + "You are not currently on quest " + quest.getName());
                    }
                } else {
                    sender.sendMessage(s.getError() + "Quest '" + args[i] + "' does not exist!");
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"accept", "a"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                UltraPlayer player = UltraPlayer.getPlayer(sender);
                EditorSettings s = player.getData(EditorSettings.class);
                QuestsData data = player.getData(QuestsData.class);
                if (!data.getPending().isEmpty()) {
                    Quest quest = null;
                    if (data.getPending().size() == 1) {
                        quest = data.getPending().get(0);
                    }
                    if (args[i] != null) {
                        quest = Quest.getQuest(args[i]);
                        if (quest == null) {
                            sender.sendMessage(s.getError() + "Quest " + args[i] + " does not exist!");
                        } else if (!data.getPending().contains(quest)){
                            sender.sendMessage(s.getError() + "You do not have " + quest.getName() + " pending!");
                            quest = null;
                        }
                    }
                    if (quest != null) {
                        if (data.getQuestInstance(quest) == null) {
                            data.quests.add(new QuestInstance(quest, player));
                        } else {
                            sender.sendMessage(s.getError() + "You are already on quest: " + quest.getName());
                        }
                        data.getPending().clear();
                    } else {
                        sender.sendMessage(s.getError() + "You must enter which quest: ");
                        for (Quest pending : data.getPending()) {
                            sender.sendMessage(s.getColon() + " - " + s.getValue() + pending.getName());
                        }
                    }
                } else {
                    sender.sendMessage(s.getError() + "You do not currently have any quests pending");
                }
            }

            @Override
            public String getHelp() {
                return "{quest}";
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"decline"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                UltraPlayer player = UltraPlayer.getPlayer(sender);
                EditorSettings s = player.getData(EditorSettings.class);
                QuestsData data = player.getData(QuestsData.class);
                if (!data.getPending().isEmpty()) {
                    for (Quest quest : data.getPending()) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', quest.getDecline()));
                    }
                    data.getPending().clear();
                } else {
                    sender.sendMessage(s.getError() + "You do not currently have any quests pending");
                }
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
                        return new String[]{"import"};
                    }

                    @Override
                    public String getHelp() {
                        return "<file>";
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        File file = new File(args[i]);
                        EditorSettings s = EditorSettings.getSettings(sender);
                        if (file.exists()) {
                            QuestsConverter.convertQuests(file);
                            sender.sendMessage(s.getSuccess() + "Successfully imported quests!");
                        } else {
                            sender.sendMessage(s.getError() + "File does not exist: " + args[i]);
                        }
                    }
                }};
            }
        }};
    }
}
