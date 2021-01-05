package co.amscraft.traits;

import co.amscraft.ultralib.commands.Component;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.Editor;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultralib.utils.ObjectUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class VanishCommand extends UltraCommand {


    public static boolean check(String idString, CommandSender sender) {
        EditorSettings s = EditorSettings.getSettings(sender);
        try {
            int id = Integer.parseInt(idString);
            NPC npc = CitizensAPI.getNPCRegistry().getById(id);
            if (npc != null) {
                if (Vanish.hasDeta(npc)) {
                    return true;
                } else {
                    sender.sendMessage(s.getHelp() + "NPC " + npc.getName() + " does not have any vanish data!");
                    //NPC does not have vanish deta
                }
            } else {
                sender.sendMessage(s.getHelp() + "NPC " + id + " does not exist!");
                //Not an NPC
            }
        } catch (Exception e) {
            sender.sendMessage(s.getHelp() + "You must enter a number for the NPC id!");
            //Did not enter number
        }
        return false;
    }

    @Override
    public String[] getAliases() {
        return new String[]{"NPCVanish", "UV"};
    }

    @Override
    public String getHelp() {
        return "A command for controlling the Vanish settings for an NPC";
    }

    @Override
    public Component[] getComponents() {
        return new Component[]{new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"clear"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                Integer id = ObjectUtils.parse(Integer.class, args[i]);
                if (check(args[i], sender)) {
                    NPC npc = CitizensAPI.getNPCRegistry().getById(id);
                    Vanish.clear(npc);
                    sender.sendMessage(s.getHelp() + "You have cleared all vanish data for NPC: " + npc.getName());
                }

            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"list"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                if (check(args[i], sender)) {
                    NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[i]));
                    List<Vanish> list = Vanish.getVanishes(npc);
                    sender.sendMessage(s.getTitle() + "Vanishes: ");
                    for (int x = 0; x < list.size(); x++) {
                        sender.sendMessage(s.getVariable().toString() + x + s.getColon() + ": " + s.getValue() + list.get(x));
                    }
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"edit"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                if (check(args[i], sender)) {
                    NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[i]));
                    UltraPlayer player = UltraPlayer.getPlayer(sender);
                    List<Vanish> vanishes = new ArrayList<>();
                    vanishes = Vanish.getVanishes(npc);
                    if (vanishes.size() == 1) {
                        player.getData(Editor.class).getEditing().add(vanishes.get(0));
                        player.getData(Editor.class).resend();
                    } else {
                        boolean showList = false;
                        if (args.length >= i + 2) {
                            try {
                                int selected = Integer.parseInt(args[i + 1]);
                                if (vanishes.size() > selected) {
                                    player.getData(Editor.class).getEditing().add(vanishes.get(selected));
                                    player.getData(Editor.class).resend();
                                } else {
                                    showList = true;
                                }
                            } catch (NumberFormatException e) {
                                showList = true;
                            }
                        } else {
                            showList = true;
                            //You must enter a number to select
                        }
                        if (showList) {
                            sender.sendMessage(s.getHelp() + "You must enter the number of one of the following options: ");
                            Bukkit.dispatchCommand(sender, "NPCVanish list " + npc.getId());
                        }
                        //Multiple Options
                    }
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"new"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                try {
                    NPC npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(args[i]));
                    if (npc != null) {
                        UltraPlayer player = UltraPlayer.getPlayer(sender);
                        Vanish v = new Vanish(npc.getId());
                        player.getData(Editor.class).getEditing().add(v);
                        player.getData(Editor.class).resend();
                        if (!Vanish.getList(Vanish.class).contains(v)) {
                            Vanish.getList(Vanish.class).add(v);
                        }
                    } else {
                        sender.sendMessage(s.getHelp() + "NPC " + args[i] + " does not exist!");
                        //Not an NPC
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(s.getHelp() + "You must enter a number for the NPC id!");
                    //Did not enter number
                }
            }
        }};
    }
}
