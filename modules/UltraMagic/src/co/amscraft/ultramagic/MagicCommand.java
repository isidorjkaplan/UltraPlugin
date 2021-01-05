package co.amscraft.ultramagic;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.commands.Component;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.Editor;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.PlayerUtility;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultralib.utils.ObjectUtils;
import co.amscraft.ultramagic.wands.Wand;
import co.amscraft.ultramagic.wands.WandObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Izzy on 2017-11-05.
 */
public class MagicCommand extends UltraCommand {
    @Override
    public Component[] getComponents() {
        return new Component[]{new Component() {

            @Override
            public String getHelp() {
                return "<spell>";
            }

            @Override
            public String[] getAliases() {
                return new String[]{"bind", "b"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                Spell spell = ObjectUtils.parse(Spell.class, args[i]);
                UltraPlayer player = UltraPlayer.getPlayer(sender.getName());
                EditorSettings s = player.getData(EditorSettings.class);
                if (spell != null) {
                    if (player.getData(MagicData.class).hasSpell(spell)) {
                        ItemStack stack = player.getBukkit().getInventory().getItemInMainHand();
                        if (stack != null && stack.getType() != Material.AIR) {
                            if (WandObject.isValidWandItem(stack)) {
                                if (stack.getAmount() == 1) {
                                    WandObject wand = WandObject.loadOrCreate(stack);
                                    if (args.length == i + 1) {
                                        wand.addSpell(spell);
                                        player.getBukkit().getInventory().setItemInMainHand(wand.save(stack));
                                        sender.sendMessage(s.getSuccess() + "You have successfully bound spell '" + spell.name + "'");
                                    } else {
                                        wand.setSpell(Integer.parseInt(args[i + 1]) - 1, spell);
                                        player.getBukkit().getInventory().setItemInMainHand(wand.save(stack));
                                        sender.sendMessage(s.getError() + "You have successfully bound spell '" + spell.name + "' at slot: " + args[i + 1]);
                                    }
                                } else {
                                    sender.sendMessage(s.getError() + "You must have only one item in your hand to bind spells");
                                }
                            } else {
                                sender.sendMessage(s.getError() + "You cannot bind spells to this item!");
                            }
                        } else {
                            player.getData(MagicData.class).getBoundRaw()[player.getBukkit().getInventory().getHeldItemSlot()] = spell;
                            sender.sendMessage(s.getSuccess() + "You have successfully bound spell '" + spell.name + "'");
                        }
                        //sender.sendMessage(s.getHelp() + "You have successfully bound spell '" + spell.name + "'");
                    } else {
                        sender.sendMessage(s.getError() + "You do not have this spell unlocked!");
                    }
                } else {
                    sender.sendMessage(s.getError() + args[i] + " is not a valid spell!");
                }

            }

        }, new Component() {

            @Override
            public String[] getAliases() {
                return new String[]{"unbind", "ub"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                Spell spell = ObjectUtils.parse(Spell.class, args[i]);
                UltraPlayer player = UltraPlayer.getPlayer(sender.getName());
                EditorSettings s = UltraPlayer.getPlayer(sender.getName()).getData(EditorSettings.class);
                ItemStack stack = player.getBukkit().getInventory().getItemInMainHand();
                if (spell != null) {
                    if (WandObject.isWand(stack)) {
                        WandObject wand = WandObject.load(stack);
                        if (wand.getSpells().contains(spell)) {
                            wand.removeSpell(spell);
                            stack = wand.save(stack);
                            player.getBukkit().getInventory().setItemInMainHand(stack);
                            sender.sendMessage(s.getSuccess() + "You have successfully removed spell '" + spell.name + "'");
                        } else {
                            sender.sendMessage(s.getError() + "You do not have spell '" + spell.name + "' bound to this item!");
                        }
                    } else {
                        player.getData(MagicData.class).getBoundRaw()[player.getBukkit().getInventory().getHeldItemSlot()] = null;
                        sender.sendMessage(s.getSuccess() + "You have successfully unbound spell '" + spell.name + "'");
                    }
                } else {
                    if (!WandObject.isWand(stack)) {
                        player.getData(MagicData.class).getBoundRaw()[player.getBukkit().getInventory().getHeldItemSlot()] = null;
                        sender.sendMessage(s.getSuccess() + "You have successfully unbound slot: " + player.getBukkit().getInventory().getHeldItemSlot() + 1);
                    } else {
                        sender.sendMessage(s.getError() + args[i] + " is not a valid spell!");
                    }
                }
            }

            @Override
            public String getHelp() {
                return "{spell}";
            }
        }, new Component() {

            @Override
            public Component[] getComponents() {
                return new Component[0];
            }

            @Override
            public String[] getAliases() {
                return new String[]{"list", "l"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                List<String> strings = new ArrayList<>();
                List<Spell> spells = new ArrayList<>();
                if (args[i] != null && args[i].equals("all") || sender.equals(Bukkit.getConsoleSender())) {
                    spells.addAll(Spell.getSpells());
                } else if (args[i] != null && Bukkit.getPlayer(args[i]) != null) {
                    spells.addAll(UltraPlayer.getPlayer(Bukkit.getPlayer(args[i])).getData(MagicData.class).getSpells());
                } else {
                    spells.addAll(UltraPlayer.getPlayer(sender.getName()).getData(MagicData.class).getSpells());
                }
                for (Spell spell : spells) {
                    strings.add(spell.name);
                }
                sender.sendMessage(EditorSettings.getSettings(sender).getValue() + strings.toString());
            }

            @Override
            public String getHelp() {
                return "{all,player}";
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"stats"};
            }

            @Override
            public String getHelp() {
                return "{player}";
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                UltraPlayer target = UltraPlayer.getPlayer(args[i]);
                if (target == null) {
                    target = UltraPlayer.getPlayer(sender);
                }
                target.getData(MagicData.class).sendStats(sender);
            }
        }, new Component() {

            @Override
            public String[] getAliases() {
                return new String[]{"sidebar", "bar", "display"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                if (args[i] == null || args[i].equals("null")) {
                    UltraPlayer.getPlayer(sender.getName()).getData(MagicData.class).setSidebar(!UltraPlayer.getPlayer(sender.getName()).getData(MagicData.class).isSidebar());
                } else {
                    if (args[i].equalsIgnoreCase("on") || args[i].equalsIgnoreCase("true")) {
                        UltraPlayer.getPlayer(sender.getName()).getData(MagicData.class).setSidebar(true);
                    } else if (args[i].equalsIgnoreCase("off") || args[i].equalsIgnoreCase("false")) {
                        UltraPlayer.getPlayer(sender.getName()).getData(MagicData.class).setSidebar(false);
                    } else {
                        sender.sendMessage(UltraPlayer.getPlayer(sender.getName()).getData(EditorSettings.class).getValue() + "Improper command usage! /magic sidebar [on/off]");
                    }
                }
                if (!UltraPlayer.getPlayer(sender.getName()).getData(MagicData.class).isSidebar()) {
                    new BukkitRunnable() {
                        public void run() {
                            UltraPlayer.getPlayer(sender.getName()).getData(PlayerUtility.class).clearSidebar();
                        }
                    }.runTaskLater(UltraLib.getInstance(), 2);
                }
                sender.sendMessage(UltraPlayer.getPlayer(sender.getName()).getData(EditorSettings.class).getSuccess() + "Successfully " + (UltraPlayer.getPlayer(sender.getName()).getData(MagicData.class).isSidebar() ? "enabled" : "disabled") + " the sidebar!");
            }

            @Override
            public String getHelp() {
                return "{on/off/true/false}";
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"wand", "w"};
            }

            @Override
            public Component[] getComponents() {
                return new Component[]{new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"organize"};
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        ItemStack stack = Bukkit.getPlayer(sender.getName()).getInventory().getItemInMainHand();
                        if (WandObject.isWand(stack)) {
                            WandObject.load(stack).organize(Bukkit.getPlayer(sender.getName()));
                        } else {
                            sender.sendMessage(EditorSettings.getSettings(sender).getError() + "You must be holding a wand to organize!");
                        }
                        //WandInterface.organize(Bukkit.getPlayer(sender.getName()).getInventory().getItemInMainHand(), Bukkit.getPlayer(sender.getName()));
                    }
                }, new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"mode"};
                    }

                    @Override
                    public String getHelp() {
                        return "{chest/inventory}";
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        EditorSettings s = EditorSettings.getSettings(sender);
                        ItemStack stack = Bukkit.getPlayer(sender.getName()).getInventory().getItemInMainHand();
                        if (WandObject.isWand(stack)) {
                            WandObject wand = WandObject.load(stack);
                            if (i+1 < args.length) {
                                WandObject.Mode mode = WandObject.Mode.getMode(args[i+1]);
                                if (mode != null) {
                                    wand.setMode(mode);
                                    sender.sendMessage(s.getSuccess() + "Successfully set the mode to " + mode);
                                } else {
                                    sender.sendMessage(s.getError() + "You must choose one of the following modes" + s.getColon() + ": " + s.getValue() + Arrays.asList(WandObject.Mode.values()));
                                }
                            } else {
                                wand.toggleMode();
                                sender.sendMessage(s.getSuccess() + "Toggled modes to " + wand.getMode());
                            }
                            stack = wand.save(stack);
                            Bukkit.getPlayer(sender.getName()).getInventory().setItemInMainHand(stack);
                        } else {
                            sender.sendMessage(s.getError() + "You must be holding a wand to organize!");
                        }
                    }
                }, new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"fill"};
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        UltraPlayer player = UltraPlayer.getPlayer(sender.getName());
                        EditorSettings s = player.getData(EditorSettings.class);
                        ItemStack stack = Bukkit.getPlayer(sender.getName()).getInventory().getItemInMainHand();
                        if (WandObject.isValidWandItem(stack)) {
                            if (stack.getAmount() == 1) {
                                List<Spell> spells = new ArrayList<>();
                                if (args[i] != null && args[i].equalsIgnoreCase("all") && UltraPlayer.getPlayer(sender).hasPermission("ultralib.commands.magic.wand.fill.all")) {
                                    spells.addAll(Spell.getSpells());
                                } else {
                                    spells.addAll(UltraPlayer.getPlayer(sender.getName()).getData(MagicData.class).getSpells());
                                }
                                WandObject wand = WandObject.loadOrCreate(stack);
                                for (Spell spell : spells) {
                                    if (!wand.getSpells().contains(spell)) {
                                        wand.addSpell(spell);
                                    }
                                }
                                stack = wand.save(stack);
                                Bukkit.getPlayer(sender.getName()).getInventory().setItemInMainHand(stack);
                                sender.sendMessage(s.getSuccess() + "Successfully added all of your unlocked spells to that item!");
                            } else {
                                sender.sendMessage(s.getError() + "You must have only one item in your hand");
                            }
                        } else {
                            sender.sendMessage(s.getError() + "That is not a valid wand item!");
                        }

                    }
                }, new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"shift"};
                    }

                    @Override
                    public String getHelp() {
                        return "{true/false} - Weather or not you can toggle spells by shifting while holding the wand";
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        Player player = Bukkit.getPlayer(sender.getName());
                        ItemStack stack = player.getInventory().getItemInMainHand();
                        EditorSettings s = EditorSettings.getSettings(sender);
                        if (stack != null && WandObject.isWand(stack)) {
                            WandObject object = WandObject.load(stack);
                            boolean shift = !object.shift;
                            if (args[i] != null && (args[i].equalsIgnoreCase("true") || args[i].equalsIgnoreCase("false"))) {
                                shift = Boolean.parseBoolean(args[i].toLowerCase());
                            }
                            object.shift = shift;
                            stack = object.save(stack);
                            player.getInventory().setItemInMainHand(stack);
                            sender.sendMessage(s.getSuccess() + "Successfully set wand shifting to: " + shift);
                        } else {
                            sender.sendMessage(s.getError() + "You must be holding a wand to run this command!");
                        }
                    }
                }, new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"name", "nom"};
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        Player player = Bukkit.getPlayer(sender.getName());
                        ItemStack stack = player.getInventory().getItemInMainHand();
                        EditorSettings s = EditorSettings.getSettings(sender);
                        if (stack != null && WandObject.isWand(stack)) {
                            WandObject object = WandObject.load(stack);
                            if (args[i] != null) {
                                String name = args[i];
                                for (int index = i + 1; index < args.length; index++) {
                                    name += " " + args[index];
                                }
                                if (name.length() < 20) {
                                    if (sender.hasPermission("ultralib.commands.magic.wand.name.color")) {
                                        name = ChatColor.translateAlternateColorCodes('&', name);
                                    }
                                    object.setName(name);
                                    player.getInventory().setItemInMainHand(object.save(stack));
                                    sender.sendMessage(s.getSuccess() + "Successfully set wand name to: " + object.getName());
                                } else {
                                    sender.sendMessage(s.getError() + "Your name is to long!");
                                }
                            } else {
                                sender.sendMessage(s.getError() + "You must enter a spell name!");
                            }
                        } else {
                            sender.sendMessage(s.getError() + "You must be holding a wand to run this command!");
                        }
                    }

                    @Override
                    public String getHelp() {
                        return "<name>";
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
                                return new String[]{"copy", "c"};
                            }

                            @Override
                            public void run(CommandSender sender, String[] args, int i) {
                                EditorSettings s = EditorSettings.getSettings(sender);
                                if (args[i] != null) {
                                    ItemStack stack = Bukkit.getPlayer(sender.getName()).getInventory().getItemInMainHand();
                                    if (WandObject.isWand(stack)) {
                                        Wand.getOrCreateWand(args[i]).save(WandObject.load(stack));
                                        sender.sendMessage(s.getSuccess() + "Successfully copied your held item to wand " + args[i]);
                                    } else {
                                        sender.sendMessage(s.getError() + "You must be holding a wand to preform this command");
                                    }
                                } else {
                                    sender.sendMessage(s.getError() + "You must enter a wand name to save it to");
                                }
                            }

                            @Override
                            public String getHelp() {
                                return "<wand> - Saves a spell to a wand schematic";
                            }
                        }, new Component() {
                            @Override
                            public String[] getAliases() {
                                return new String[]{"load"};
                            }

                            public void run(CommandSender sender, String[] args, int i) {
                                EditorSettings s = EditorSettings.getSettings(sender);
                                if (args[i] != null) {
                                    Wand wand = Wand.getWand(args[i]);
                                    if (wand != null) {
                                        ItemStack stack = Bukkit.getPlayer(sender.getName()).getInventory().getItemInMainHand();
                                        if (WandObject.isValidWandItem(stack)) {
                                            stack = wand.getWand().save(stack);
                                            Bukkit.getPlayer(sender.getName()).getInventory().setItemInMainHand(stack);
                                            sender.sendMessage(s.getSuccess() + "Successfully loaded the item!");
                                            if (args.length > i + 1 && UltraPlayer.getPlayer(sender).hasPermission("ultralib.commands.magic.admin.add")) {
                                                UltraPlayer target = UltraPlayer.getPlayer(args[i + 1]);
                                                if (target != null) {
                                                    MagicData data = target.getData(MagicData.class);
                                                    for (Spell spell : wand.getWand().getSpells()) {
                                                        if (!data.hasSpell(spell)) {
                                                            data.addSpell(spell);
                                                        }
                                                    }
                                                    target.getBukkit().getInventory().addItem(stack);
                                                    sender.sendMessage(s.getSuccess() + "Successfully added all the spells and wand to player " + target.getBukkit().getName());
                                                } else {
                                                    sender.sendMessage(s.getError() + "Unable to copy spells to player " + args[i + 1] + " because they are not online!");
                                                }
                                            }
                                        } else {
                                            sender.sendMessage(s.getError() + "You cannot load spells onto that item!");
                                        }
                                    } else {
                                        sender.sendMessage(s.getError() + "The wand you requested does not exist!");
                                    }
                                } else {
                                    sender.sendMessage(s.getError() + "You must enter a wand name to load");
                                }
                            }

                            @Override
                            public String getHelp() {
                                return "<wand> {player} Warning: It will add all the spells on the wand to that player if you choose a player. Leave blank to just load to held item";
                            }
                        }, new Component() {
                            @Override
                            public String[] getAliases() {
                                return new String[]{"import"};
                            }

                            @Override
                            public void run(CommandSender sender, String[] args, int i) {
                                EditorSettings s = EditorSettings.getSettings(sender);
                                if (UltraMagic.isElmakersEnabled()) {
                                    if (args[i] != null) {
                                        ItemStack stack = Bukkit.getPlayer(sender.getName()).getInventory().getItemInMainHand();
                                        if (com.elmakers.mine.bukkit.magic.MagicPlugin.getAPI().isWand(stack)) {
                                            Wand.getOrCreateWand(args[i]).convertOldMagic(stack);
                                            sender.sendMessage(s.getSuccess() + "Successfully imported your held item to wand " + args[i]);
                                        } else {
                                            sender.sendMessage(s.getError() + "You must be holding a wand to preform this command");
                                        }
                                    } else {
                                        sender.sendMessage(s.getError() + "You must enter a wand name to save it to");
                                    }
                                } else {
                                    sender.sendMessage(s.getError() + "Elmakers magic plugin is not enabled!");
                                }
                            }

                            @Override
                            public String getHelp() {
                                return "<wand> - Imports from an Elmakers wand -- Only copies Umagic Spells";
                            }
                        }, new Component() {
                            @Override
                            public String[] getAliases() {
                                return new String[]{"list"};
                            }

                            @Override
                            public void run(CommandSender sender, String[] args, int i) {
                                EditorSettings s = EditorSettings.getSettings(sender);
                                sender.sendMessage(s.getVariable() + "Wands" + s.getColon() + ": " + s.getValue() + Wand.getList(Wand.class));
                            }
                        }, new Component() {
                            @Override
                            public String[] getAliases() {
                                return new String[]{"delete"};
                            }

                            @Override
                            public void run(CommandSender sender, String[] args, int i) {
                                EditorSettings s = EditorSettings.getSettings(sender);
                                if (args[i] != null) {
                                    Wand wand = Wand.getWand(args[i]);
                                    if (wand != null) {
                                        sender.sendMessage(s.getSuccess() + "Successfully deleted wand" + wand.name);
                                        wand.delete();
                                    } else {
                                        sender.sendMessage(s.getError() + "The wand you requested does not exist!");
                                    }
                                } else {
                                    sender.sendMessage(s.getError() + "You must enter a wand name to load");
                                }
                            }

                            @Override
                            public String getHelp() {
                                return "<wand>";
                            }
                        }, new Component() {
                            @Override
                            public String[] getAliases() {
                                return new String[]{"icon"};
                            }

                            @Override
                            public String getHelp() {
                                return "<item>:<data>";
                            }

                            @Override
                            public void run(CommandSender sender, String[] args, int i) {
                                Player player = Bukkit.getPlayer(sender.getName());
                                EditorSettings s = EditorSettings.getSettings(sender);
                                ItemStack stack = player.getInventory().getItemInMainHand();
                                if (stack != null) {
                                    if (args[i] != null) {
                                        String item = args[i].split(":")[0];
                                        Material material = Material.getMaterial(item.toUpperCase());
                                        if (material != null) {
                                            String dataString = args[i].replaceFirst(item + ":", "");
                                            int data = 0;
                                            try {
                                                data = Integer.parseInt(dataString);
                                            } catch (Exception e) {

                                            }
                                            if (data != 0) {
                                                ItemMeta meta = stack.getItemMeta();
                                                meta.setUnbreakable(true);
                                                stack.setItemMeta(meta);
                                                stack.setDurability((short) data);
                                            }
                                            stack.setType(material);
                                            player.getInventory().setItemInMainHand(stack);
                                            sender.sendMessage(s.getSuccess() + "Successfully set the icon to: " + material + ":" + data);
                                        } else {
                                            //TODO null item type
                                            sender.sendMessage(s.getError() + "You must enter a valid material");
                                        }

                                    } else {
                                        sender.sendMessage(s.getError() + "You must enter the proper input");
                                        //TODO no arguments
                                    }
                                } else {
                                    //TODO stack is null
                                    sender.sendMessage(s.getError() + "You must be holding an item to preform this command!");
                                }
                            }
                        }, new Component() {
                            @Override
                            public String[] getAliases() {
                                return new String[]{"format"};
                            }

                            @Override
                            public void run(CommandSender sender, String[] args, int i) {
                                EditorSettings s = EditorSettings.getSettings(sender);
                                Player player = Bukkit.getPlayer(sender.getName());
                                ItemStack stack = player.getInventory().getItemInMainHand();
                                if (stack != null && WandObject.isWand(stack)) {
                                    String format = args[i];
                                    for (int index = i + 1; index < args.length; index++) {
                                        format += " " + args[index];
                                    }
                                    if (format.contains("{name}") && format.contains("{spell}")) {
                                        format = ChatColor.translateAlternateColorCodes('&', format);
                                        WandObject wand = WandObject.load(stack);
                                        wand.setFormat(format);
                                        player.getInventory().setItemInMainHand(wand.save(stack));
                                    } else {
                                        sender.sendMessage(s.getError() + "The format must contain {spell} and {name} placehholders");
                                    }
                                } else {
                                    sender.sendMessage(s.getError() + "You must be holding a wand to preform this command");
                                }
                            }

                            @Override
                            public String getHelp() {
                                return "<format>, must contain {name} and {spell}";
                            }
                        }};
                    }
                }};
            }
        }, new Component() {

            @Override
            public Component[] getComponents() {
                return new Component[]{new Component() {

                    @Override
                    public String[] getAliases() {
                        return new String[]{"add", "a"};
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        Spell spell = Spell.getSpell(args[i]);
                        if (spell != null) {
                            if (args.length - i == 1) {
                                UltraPlayer.getPlayer(sender.getName()).getData(MagicData.class).addSpell(spell);
                                sender.sendMessage(UltraPlayer.getPlayer(sender.getName()).getData(EditorSettings.class).getSuccess() + "Successfully added spell: " + spell.name);
                            } else {
                                if (Bukkit.getPlayer(args[i + 1]) != null) {
                                    UltraPlayer.getPlayer(args[i + 1]).getData(MagicData.class).addSpell(spell);
                                    sender.sendMessage(UltraPlayer.getPlayer(sender.getName()).getData(EditorSettings.class).getSuccess() + "Successfully added spell '" + spell.name + "' to player: " + args[i + 1]);
                                } else {
                                    sender.sendMessage(UltraPlayer.getPlayer(sender.getName()).getData(EditorSettings.class).getError() + "Player '" + args[i + 1] + "' is not online!");
                                }
                            }
                        } else {
                            sender.sendMessage(UltraPlayer.getPlayer(sender.getName()).getData(EditorSettings.class).getError() + "Spell '" + args[i] + "' does not exist!");
                        }
                    }

                    @Override
                    public String getHelp() {
                        return "<spell> {player}";
                    }
                }, new Component() {
                    @Override
                    public String getHelp() {
                        return "<spell> {player}";
                    }

                    @Override
                    public String[] getAliases() {
                        return new String[]{"remove", "rm"};
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        Spell spell = Spell.getSpell(args[i]);
                        if (spell != null) {
                            if (args.length - i == 1) {
                                UltraPlayer.getPlayer(sender.getName()).getData(MagicData.class).removeSpell(spell);
                                sender.sendMessage(UltraPlayer.getPlayer(sender.getName()).getData(EditorSettings.class).getSuccess() + "Successfully removed spell: " + spell.name);
                            } else {
                                if (Bukkit.getPlayer(args[i + 1]) != null) {
                                    UltraPlayer.getPlayer(args[i + 1]).getData(MagicData.class).removeSpell(spell);
                                    sender.sendMessage(UltraPlayer.getPlayer(sender.getName()).getData(EditorSettings.class).getSuccess() + "Successfully removed spell '" + spell.name + "' from player: " + args[i + 1]);
                                } else {
                                    sender.sendMessage(UltraPlayer.getPlayer(sender.getName()).getData(EditorSettings.class).getError() + "Player '" + args[i + 1] + "' is not online!");
                                }
                            }
                        } else {
                            sender.sendMessage(UltraPlayer.getPlayer(sender.getName()).getData(EditorSettings.class).getError() + "Spell '" + args[i] + "' does not exist!");
                        }
                    }
                }, new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"cast", "c"};
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        Spell spell = Spell.getSpell(args[i]);
                        UltraPlayer target = UltraPlayer.getPlayer(sender);
                        try {
                            if (args.length == i + 2) {
                                target = UltraPlayer.getPlayer(args[i + 1]);
                            }
                        } catch (Exception e) {

                        }
                        EditorSettings settings = UltraPlayer.getPlayer(sender).getData(EditorSettings.class);
                        if (spell != null) {
                            try {
                                new SpellInstance(new Target(target.getBukkit()), spell);
                                sender.sendMessage(settings.getSuccess() + "You have successfully cast spell: " + spell.getName());
                            } catch (Exception e) {
                                e.printStackTrace();
                                sender.sendMessage(settings.getError() + "An error occored when attempting to use this command. Please report this to the plugin developer at once should the error persist.");
                            }
                        } else {
                            sender.sendMessage(settings.getError() + "Spell '" + args[i] + "' does not exist!");
                        }
                    }

                    @Override
                    public String getHelp() {
                        return "<spell>";
                    }
                }, new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"set"};
                    }

                    @Override
                    public Component[] getComponents() {
                        return new Component[]{new Component() {
                            @Override
                            public String[] getAliases() {
                                return new String[]{"mana"};
                            }

                            @Override
                            public void run(CommandSender sender, String[] args, int i) {
                                EditorSettings s = EditorSettings.getSettings(sender);
                                UltraPlayer target = UltraPlayer.getPlayer(sender);
                                if (args.length > i + 1) {
                                    target = UltraPlayer.getPlayer(args[i + 1]);
                                    if (target == null) {
                                        sender.sendMessage(s.getError() + "Player '" + args[i + 1] + "' is not online!");
                                    }
                                }
                                try {
                                    Integer mana = Integer.parseInt(args[i]);
                                    if (mana > 0) {
                                        MagicData data = target.getData(MagicData.class);
                                        data.setMaxMana(mana);
                                        sender.sendMessage(s.getSuccess() + "Successfully set " + target.getBukkit().getName() + "'s max mana to: " + mana);
                                        data.save();
                                    } else {
                                        sender.sendMessage(s.getError() + "You must enter a value greater than 0");
                                    }
                                } catch (NumberFormatException e) {
                                    sender.sendMessage(s.getError() + "You must enter an integer number!");
                                }
                            }
                        }, new Component() {
                            @Override
                            public String[] getAliases() {
                                return new String[]{"bar"};
                            }

                            @Override
                            public Component[] getComponents() {
                                return new Component[]{new Component() {
                                    @Override
                                    public String[] getAliases() {
                                        return new String[]{"color"};
                                    }

                                    @Override
                                    public void run(CommandSender sender, String[] args, int i) {
                                        BarColor color = ObjectUtils.parse(BarColor.class, args[i]);
                                        if (color != null) {
                                            UltraPlayer target = UltraPlayer.getPlayer(args[i]);
                                            UltraPlayer player = UltraPlayer.getPlayer(sender);
                                            if (target == null) {
                                                target = player;
                                            }
                                            target.getData(MagicData.class).setManaBarColor(color);
                                            sender.sendMessage(EditorSettings.getSettings(sender).getSuccess() + "Successfully changed '" + target.getBukkit().getName() + "'s bar color to: " + color);
                                        } else {
                                            EditorSettings s = EditorSettings.getSettings(sender);
                                            sender.sendMessage(s.getVariable() + "Invalid color" + s.getColon() + ": " + s.getValue() + Arrays.asList(BarColor.values()));
                                        }
                                    }
                                }, new Component() {
                                    @Override
                                    public String[] getAliases() {
                                        return new String[]{"type"};
                                    }

                                    @Override
                                    public void run(CommandSender sender, String[] args, int i) {
                                        BarStyle style = ObjectUtils.parse(BarStyle.class, args[i]);
                                        if (style != null) {
                                            UltraPlayer target = UltraPlayer.getPlayer(args[i]);
                                            UltraPlayer player = UltraPlayer.getPlayer(sender);
                                            if (target == null) {
                                                target = player;
                                            }
                                            target.getData(MagicData.class).setManaBarStyle(style);
                                            sender.sendMessage(EditorSettings.getSettings(sender).getSuccess() + "Successfully changed '" + target.getBukkit().getName() + "'s bar style to: " + style);
                                        } else {
                                            EditorSettings s = EditorSettings.getSettings(sender);
                                            sender.sendMessage(s.getVariable() + "Invalid style" + s.getColon() + ": " + s.getValue() + Arrays.asList(BarStyle.values()));
                                        }
                                    }
                                }};
                            }
                        }};
                    }
                }, new Component() {
                    @Override
                    public String[] getAliases() {
                        return new String[]{"edit"};
                    }

                    @Override
                    public void run(CommandSender sender, String[] args, int i) {
                        UltraPlayer player = UltraPlayer.getPlayer(args[i]);
                        if (player == null) {
                            player = UltraPlayer.getPlayer(sender);
                        }
                        if (player != null) {
                            Editor editor = UltraPlayer.getPlayer(sender).getData(Editor.class);
                            editor.getEditing().add(player.getData(MagicData.class));
                            editor.resend();
                        }
                    }

                    @Override
                    public String getHelp() {
                        return "<player>";
                    }
                }};
            }

            @Override
            public String[] getAliases() {
                return new String[]{"admin", "a"};
            }

        }};
    }

    @Override
    public String[] getAliases() {
        return new String[]{"magic", "um", "libmagic", "umagic"};
    }

    @Override
    public String getHelp() {
        return null;
    }


}
