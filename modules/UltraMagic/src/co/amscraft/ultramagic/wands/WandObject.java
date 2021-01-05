package co.amscraft.ultramagic.wands;

import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.utils.NMSUtils;
import co.amscraft.ultralib.utils.ObjectUtils;
import co.amscraft.ultralib.utils.savevar.SaveVar;
import co.amscraft.ultramagic.Spell;
import co.amscraft.ultramagic.UltraMagic;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class WandObject {
    @SaveVar
    public static String OPEN_SOUND = "magic.shimmer";
    @SaveVar
    public static String CLOSE_SOUND = OPEN_SOUND;
    public Map<Integer, Integer> map = new HashMap<>();
    public boolean shift = true;
    public int bound = -1;
    private String name = null;
    private String format = "&7{name} &6(&7{spell}&6)";
    private Mode mode = Mode.CHEST;
    private ItemStack[] inventory;

    public static Spell getSpellFromIcon(ItemStack icon) {
        if (isWand(icon) && icon.getType() == Material.PLAYER_HEAD) {
            Spell spell = load(icon).getBound();
            if (icon.hasItemMeta() && icon.getItemMeta().getDisplayName().contains(spell.getName())) {
                return spell;
            }
        }
        return null;
    }

    public static boolean isValidWandItem(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) {
            return false;
        }
        //if (Bukkit.getPluginManager().getPlugin("RPGItems") != null && Bukkit.getPluginManager().getPlugin("RPGItems").isEnabled()) {
          //  if (think.rpgitems.item.ItemManager.toRPGItem(stack) != null) {
            //    return false;
            //}
        //}
        if (UltraMagic.isElmakersEnabled()) {
            if (com.elmakers.mine.bukkit.magic.MagicPlugin.getAPI().isWand(stack)) {
                return false;
            }
        }
        return true;
    }


    public static ItemStack delete(ItemStack stack) {
        if (isWand(stack)) {
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(WandObject.load(stack).getName());
            stack.setItemMeta(meta);
            return NMSUtils.write(stack, "wand", null);

        }
        return stack;
    }

    public static WandObject loadOrCreate(ItemStack stack) {
        WandObject object = load(stack);
        if (object == null) {
            object = new WandObject();
        }
        return object;
    }

    public static WandObject load(ItemStack stack) {
        if (isWand(stack)) {
            try {
                String string = NMSUtils.read(stack, "wand", String.class);
                return (WandObject) ObjectUtils.read(string);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean isWand(ItemStack stack) {
            String string = NMSUtils.read(stack, "wand", String.class);
            return string != null && !string.equals("");
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void addSpell(Spell spell) {
        if (!map.containsValue(spell.getId())) {
            int i = 0;
            while (map.containsKey(i)) {
                i++;
            }
            this.setSpell(i, spell);
        }
    }

    public String getFormat() {
        return ChatColor.translateAlternateColorCodes('&', this.format);
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void removeSpell(Spell spell) {
        for (int key : new HashSet<>(map.keySet())) {
            if (map.get(key).equals(spell.getId())) {
                map.remove(key);
            }
        }
    }

    public Spell getBound() {
        Spell spell = Spell.getSpell(this.bound);
        if (spell == null) {
            spell = (Spell) this.getSpells().toArray()[0];
            this.setBound(spell);
        }
        return spell;
    }

    public void setBound(Spell spell) {
        this.bound = spell.getId();
    }

    public void setSpell(int slot, Spell spell) {
        if (slot < 54) {
            this.map.put(slot, spell.getId());
        }
        this.setBound(spell);
    }

    public Collection<Spell> getSpells() {
        return this.getSpellsMap().values();
    }

    public void setSpells(Map<Integer, Spell> map) {
        this.map = new HashMap<>();
        for (int key : map.keySet()) {
            this.map.put(key, map.get(key).getId());
        }
    }

    public Map<Integer, Spell> getSpellsMap() {
        HashMap<Integer, Spell> map = new HashMap<>();
        for (int key : this.map.keySet()) {
            map.put(key, Spell.getSpell(this.map.get(key)));
        }
        return map;
    }

    public void open(Player player) {
        switch (mode) {
            case CHEST:
                openChest(player);
                break;
            case INVENTORY:
                toggleInventory(player);
                break;
        }
    }

    public void toggleMode() {
        switch (mode) {
            case INVENTORY:
                this.setMode(Mode.CHEST);
                break;
            case CHEST:
                this.setMode(Mode.INVENTORY);
                break;
        }
    }

    public boolean isOpen() {
        return inventory != null;
    }


    public void toggleInventory(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack wand = inventory.getItemInMainHand();
        // System.out.println(Arrays.asList(inventory.getContents()));
        if (isWand(wand)) {
            // System.out.println(2);
            if (!isOpen()) {
                // System.out.println(3);
                EditorSettings s = EditorSettings.getSettings(player);
                inventory.setItemInMainHand(null);
                ItemStack[] stack = inventory.getContents();
                this.inventory = Arrays.copyOf(stack, stack.length);
                inventory.clear();
                    for (int i = 1; i <=4; i++) {
                        inventory.setItem(stack.length - i, NMSUtils.write(this.inventory[stack.length - i], "expires", System.currentTimeMillis() + 360000));
                    }

                Map<Integer, Spell> spells = getSpellsMap();
                int shift = 0;
                for (int i = 0; i < stack.length- 4 - shift; i++) {
                    Spell spell = spells.get(i);
                    if (shift == 0 && inventory.getHeldItemSlot() == i) {
                        shift = 1;
                    }
                    if (spell != null) {
                        ItemStack icon = spell.getItem(s);
                        inventory.setItem(i + shift, icon);
                    }
                }
                player.playSound(player.getLocation(), OPEN_SOUND, 2, 3);
            } else {
                Spell bound = getBound();
                clearSpells();
                int shift = 0;
                ItemStack[] stacks = inventory.getContents();
                for (int i = 0; i < stacks.length - 4 - shift; i++) {
                    if (shift == 0 && i == inventory.getHeldItemSlot()) {
                        shift = 1;
                    }
                    ItemStack stack = stacks[i + shift];
                    if (isWand(stack)) {
                        Spell spell = getSpellFromIcon(stack);
                        if (spell != null) {
                            setSpell(i, spell);
                        }
                    }
                }
                inventory.clear();
                inventory.setContents(this.inventory);
                this.inventory = null;
                setBound(bound);
               // System.out.println(this.getBound());
                player.playSound(player.getLocation(), CLOSE_SOUND, 2, 3);
            }
            wand = this.save(wand);
            inventory.setItemInMainHand(wand);
        }
    }

    public void clearSpells() {
        Collection<Spell> spells = getSpells();
        for (Spell spell : spells) {
            removeSpell(spell);
        }
    }

    public void openChest(Player player) {
        int h = 0;
        for (Integer i : this.getSpellsMap().keySet()) {
            if (i > h) {
                h = i;
            }
        }
        h += 1;
        if (h < 1) {
            h = 1;
        }
        Inventory inventory = Bukkit.createInventory(player, (int) (9 * (Math.ceil(Math.abs((h) / (double) 9)))), "Spells");
        for (Integer i : this.getSpellsMap().keySet()) {
            try {
                inventory.setItem(i, this.getSpellsMap().get(i).getItem(EditorSettings.getSettings(player)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        player.playSound(player.getLocation(), OPEN_SOUND, 2, 3);
        player.openInventory(inventory);
    }

    public void organize(Player player) {
        Inventory inventory = Bukkit.createInventory(player, 54, "Editing Spells");
        for (Integer i : this.getSpellsMap().keySet()) {
            if (i < 54) {
                try {
                    inventory.setItem(i, this.getSpellsMap().get(i).getItem(EditorSettings.getSettings(player)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        player.playSound(player.getLocation(), "magic.shimmer", 2, 3);
        player.openInventory(inventory);
    }

    public void load(Inventory inventory) {
        Map<Integer, Spell> spells = new HashMap<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack spellItem = inventory.getItem(i);
            if (spellItem != null) {
                Spell spell = getSpellFromIcon(spellItem);
                if (spell != null) {
                    spells.put(i, spell);
                }
            }
        }
        this.setSpells(spells);
    }

    public String getDisplay() {
        String format = this.getFormat();
        if (this.getBound() != null) {
            format = format.replace("{spell}", this.getBound().getName());
        }
        if (this.getName() != null) {
            format = format.replace("{name}", this.getName());
        }
        return format;
    }

    public void setDisplay(ItemStack stack) {
        //System.out.println(stack);
        ItemMeta meta = stack.getItemMeta();
        //System.out.println(meta);
        if (this.getName() == null) {
            if (meta.getDisplayName() != null) {
                this.setName(meta.getDisplayName());
            } else {
                this.setName(stack.getType().toString().toLowerCase());
            }
        }
        meta.setDisplayName(getDisplay());
        stack.setItemMeta(meta);
    }

    public ItemStack save(ItemStack stack) {
        if (this.getSpells().isEmpty()) {
            return delete(stack);
        }

            stack.setAmount(1);
            setDisplay(stack);
        try {
            return NMSUtils.write(stack, "wand", ObjectUtils.write(this));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return stack;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public enum Mode {
        INVENTORY, CHEST;

        public static Mode getMode(String s) {
            if (s == null) {
                return null;
            }
            for (Mode mode : values()) {
                if (mode.toString().equalsIgnoreCase(s)) {
                    return mode;
                }
            }
            return null;
        }
    }
}
