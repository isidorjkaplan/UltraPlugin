package co.amscraft.rpgitems;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.utils.ObjectUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.logging.Level;

public class RPGItem extends UltraObject {
    @FieldDescription(help = "A one-word name identifier")
    public String name = "name";
    @FieldDescription(help = "The display-name on the item")
    private String display = "Item";
    @FieldDescription(help = "The Lore for the Item")
    private String lore = "Item Lore";
    @FieldDescription(help = "The length of each line in the lore", unit = "unicode characters")
    private int loreBuffer = 10;
    @FieldDescription(help = "The data for the item")
    private short itemData = 0;
    @FieldDescription(help = "The Material Type of the Item")
    private Material item = Material.DIAMOND_SWORD;
    @FieldDescription(help = "The damage of the sword", unit = "half-hearts")
    private int damage = -1;
    @FieldDescription(help = "The durability of the item", unit = "uses")
    private int durability = -1;
    @FieldDescription(help = "If true, the item will be undroppable")
    private boolean undroppable = false;
    @FieldDescription(help = "If the item should be soulbound or not! If it is soulbound it is bound to the first person to use it!")
    private boolean soulbound = false;
    @FieldDescription(help = "https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html", unit = "Enchantment (all caps)")
    private Map<String, Integer> enchantments = new HashMap<>();
    private Set<RPGItemPower> powers = new HashSet<>();


    @FieldDescription(help = "DEPRECATED: DO NOT USE")
    private int damageMin = -1;
    @FieldDescription(help = "DEPRECATED: DO NOT USE")
    private int damageMax = -1;

    public ItemStack getItemStack() {
        ItemStack stack = new ItemStack(item, 1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',display));
        List<String> loreList = convertLore(ChatColor.translateAlternateColorCodes('&',lore));
        if (damage > 0) {
            loreList.add(0, org.bukkit.ChatColor.translateAlternateColorCodes('&', "&7Damage&f: &7" + damage));
        }
        meta.setUnbreakable(true);
        meta.setLore(loreList);
        stack.setItemMeta(meta);
        stack.setDurability(itemData);
        for (String enchantment: enchantments.keySet()) {
            try {
                Enchantment enchant = Enchantment.getByName(enchantment);
                if (enchant != null) {
                    stack.addUnsafeEnchantment(enchant, enchantments.get(enchantment));
                } else {
                    ObjectUtils.debug(Level.WARNING,"Enchantment " + enchantment + " does not exist in RPGItem " + this.getDisplay());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stack;
    }

    public boolean isUndroppable() {
        return undroppable;
    }

    public void setUndroppable(boolean undroppable) {
        this.undroppable = undroppable;
    }

    public boolean isSoulbound() {
        return soulbound;
    }

    public void setSoulbound(boolean soulbound) {
        this.soulbound = soulbound;
    }

    private List<String> convertLore(String description) {
        List<String> lore = new ArrayList<>();
        String[] words = description.split(" ");
        String line = "";
        int length = 0;
        String color = ChatColor.GRAY + "";
        for (String word : words) {
            line += word + " ";
            length += word.length();
            if (length >= loreBuffer) {
                lore.add(color + line);
                length = 0;
                try {
                    if (line.contains("§")) {
                        color = "§" + Character.toString(line.charAt(line.lastIndexOf("§") + 1));
                    }
                } catch (Exception e) {

                }
                line = "";
            }
        }
        if (!line.equals("")) {
            lore.add(color+ line);
        }
        return lore;
    }

    public static RPGItem getItem(String name) {
        if (name == null) {
            return null;
        }
        for (RPGItem item: getList(RPGItem.class)) {
            if (item != null && item.getName() != null && item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    public SerializedRPGItem createNewItem() {
        SerializedRPGItem item = new SerializedRPGItem();
        item.setId(this.getId());
        item.setDurability(this.getDurability());
        return item;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public double getDamage() {
        //For legacy support if they have not specified a damage and have a range then use the mean of that range
        if (damage == -1 && damageMin != -1)
            setDamage((damageMin + damageMax)/2);
        //return damage
        return damage;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getLore() {
        return lore;
    }

    public void setLore(String lore) {
        this.lore = lore;
    }

    public short getItemData() {
        return itemData;
    }

    public void setItemData(short itemData) {
        this.itemData = itemData;
    }

    public Material getItem() {
        return item;
    }

    public void setItem(Material item) {
        this.item = item;
    }

    public void setDamage(int damage) {
        this.damageMin = this.damageMax = -1;
        this.damage = damage;
    }


    public int getDurability() {
        return durability;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }


    public Set<RPGItemPower> getPowers() {
        return powers;
    }

    public void setPowers(Set<RPGItemPower> powers) {
        this.powers = powers;
    }
}
