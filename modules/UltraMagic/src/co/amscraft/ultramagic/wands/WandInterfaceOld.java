package co.amscraft.ultramagic.wands;

import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultralib.utils.NMSUtils;
import co.amscraft.ultralib.utils.ObjectUtils;
import co.amscraft.ultramagic.Spell;
import co.amscraft.ultramagic.UltraMagic;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by Izzy on 2017-10-26.
 */
@Deprecated
public class WandInterfaceOld {

    public static List<Spell> getSpells(ItemStack stack) {
        try {
            //spells: 0-5 1-5 2-3 5-1 1-2
            List<Spell> spells = new ArrayList<>();
            for (String id : NMSUtils.read(stack, "spells", String.class).toString().split(" ")) {
                spells.add(Spell.getSpell(Integer.parseInt(id.split("-")[1])));
            }
            return spells;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<Integer, Spell> getSpellsMap(ItemStack stack) {
        //spells: 0-5 1-5 2-3 5-1 1-2
        HashMap<Integer, Spell> map = new HashMap<Integer, Spell>();
        try {
            String read = NMSUtils.read(stack, "spells", String.class);
            if (!read.equals("")) {
                for (String id : read.toString().split(" ")) {
                    try {
                        map.put(Integer.parseInt(id.split("-")[0]), Spell.getSpell(Integer.parseInt(id.split("-")[1])));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // ObjectUtils.debug(Level.INFO, "Generating new wand map!");
            }
        } catch (Exception e) {
            ObjectUtils.debug(Level.INFO, "Error: Generating new wand map!");
        }
        return map;

    }

    public static ItemStack addSpell(ItemStack stack, Spell spell) {
        Map<Integer, Spell> spells = getSpellsMap(stack);
        for (int i = 1; i <= 54 + 1; i++) {
            if (!spells.containsKey(i)) {
                return putSpell(stack, i, spell);

            }
        }
        return stack;
    }

    public static ItemStack removeSpell(ItemStack stack, Spell spell) {
        if (getSpellsMap(stack).containsValue(spell)) {
            for (int i : getSpellsMap(stack).keySet()) {
                if (getSpellsMap(stack).get(i).equals(spell)) {
                    stack = putSpell(stack, i, null);

                }
            }
        }
        return stack;
    }

    public static ItemStack putSpell(ItemStack stack, int slot, Spell spell) {
        if (isValidWandItem(stack)) {
            if (slot < 1) {
                slot = 1;
            }
            Map<Integer, Spell> spells = getSpellsMap(stack);
            List<Integer> ints = new ArrayList<>();
            ints.addAll(spells.keySet());
            for (Integer i : ints) {
                if (spells.get(i).equals(spell)) {
                    spells.remove(i);
                }
            }
            spells.put(slot, spell);
            stack = putSpells(stack, spells);
            try {
                stack = setBound(stack, spell);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stack;
        }
        return stack;
    }

    public static ItemStack putSpells(ItemStack stack, Map<Integer, Spell> spells) {
        String map = "";
        ArrayList<Integer> ints = new ArrayList<>();
        ints.addAll(spells.keySet());
        for (Integer i : ints) {
            try {
                map += i + "-" + spells.get(i).getId() + " ";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

            return NMSUtils.write(stack, "spells", map);


    }

    public static Spell getBoundSpell(ItemStack stack) {
        try {
            return (Spell) NMSUtils.read(stack, "bound", Spell.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Spell getSpellFromIcon(ItemStack icon, UltraPlayer player) {
        if (icon.hasItemMeta()) {
            EditorSettings s = player.getData(EditorSettings.class);
            Spell spell = Spell.getSpell(icon.getItemMeta().getDisplayName().replaceFirst(s.getVariable() + "", ""));
            if (spell != null && spell.getItem(s).equals(icon)) {
                return spell;
            }
        }
        return null;
    }

    public static ItemStack setBound(ItemStack stack, Spell spell) {

            return NMSUtils.write(stack, "bound", spell.getId() + "");

    }

    public static void open(Player player, ItemStack stack) {
        if (isWand(stack)) {
            Map<Integer, Spell> spells = getSpellsMap(stack);
            int h = 0;
            for (Integer i : spells.keySet()) {
                if (i > h) {
                    h = i;
                }
            }
            if (h < 1) {
                h = 1;
            }
            Inventory inventory = Bukkit.createInventory(player, (int) (9 * (Math.ceil(Math.abs((h) / (double) 9)))), "Spells");
            for (Integer i : spells.keySet()) {
                try {
                    inventory.setItem(i - 1, spells.get(i).getItem(EditorSettings.getSettings(player)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            player.playSound(player.getLocation(), "magic.shimmer", 2, 3);
            player.openInventory(inventory);
        }
    }

    public static boolean isValidWandItem(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) {
            return false;
        }
        //if (Bukkit.getPluginManager().getPlugin("RPGItems") != null && Bukkit.getPluginManager().getPlugin("RPGItems").isEnabled()) {
            //if (think.rpgitems.item.ItemManager.toRPGItem(stack) != null) {
              //  return false;
            //}
        //}
        if (UltraMagic.isElmakersEnabled()) {
            if (com.elmakers.mine.bukkit.magic.MagicPlugin.getAPI().isWand(stack)) {
                return false;
            }
        }
        return true;
    }

    public static Spell getSpell(ItemStack stack, int slot) {
        return getSpellsMap(stack).get(slot);
    }

    public static void organize(ItemStack stack, Player player) {
        if (isWand(stack)) {
            Map<Integer, Spell> spells = getSpellsMap(stack);
            Inventory inventory = Bukkit.createInventory(player, 54, "Editing Spells");
            for (Integer i : spells.keySet()) {
                if (i < 54) {
                    try {
                        inventory.setItem(i - 1, spells.get(i).getItem(EditorSettings.getSettings(player)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            player.playSound(player.getLocation(), "magic.shimmer", 2, 3);
            player.openInventory(inventory);
        }
    }

    public static ItemStack finalizeWand(ItemStack stack, Inventory inventory) {
        Map<Integer, Spell> spells = new HashMap<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack spellItem = inventory.getItem(i);
            if (spellItem != null) {
                Spell spell = getSpellFromIcon(spellItem, UltraPlayer.getPlayer(inventory.getViewers().get(0)));
                if (spell != null) {
                    spells.put(i + 1, spell);
                }
            }
        }
        return putSpells(stack, spells);
    }


    public static boolean isWand(ItemStack stack) {
        try {
            return isValidWandItem(stack) && NMSUtils.read(stack, "spells", String.class) != null && !NMSUtils.read(stack, "spells", String.class).equals("");
        } catch (Exception e) {
            return false;
        }
    }
}
