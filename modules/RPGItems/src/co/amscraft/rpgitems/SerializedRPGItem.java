package co.amscraft.rpgitems;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.utils.NMSUtils;
import co.amscraft.ultralib.utils.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class SerializedRPGItem {
    private int id;
    private int durability;
    private UUID player;
    @FieldDescription(save = false)
    private boolean updated = false;
    @FieldDescription(save = false)
    private int amount = 1;

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated() {
        this.updated = true;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDurability(int durability) {
        this.durability = durability;
    }

    public int getAmount() {
        return amount;
    }

    public int getId() {
        return id;
    }

    public int getDurability() {
        return durability;
    }

    public void decreaseDurability() {
        if (this.durability > 0) {
            this.setUpdated();
            this.durability--;
        }
    }

    public UUID getPlayer() {
        return player;
    }

    public void setPlayer(UUID player) {
        this.setUpdated();
        this.player = player;
    }

    public boolean attemptUsage(Player player) {
        if (!getItem().isSoulbound()) {
            return true;
        }
        if (getPlayer() == null) {
            this.setPlayer(player.getUniqueId());
            return true;
        } else if (getPlayer().equals(player.getUniqueId())) {
            return true;
        }
        return false;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public static boolean hasData(ItemStack stack) {
        return NMSUtils.read(stack, "rpgitem", String.class) != null;
    }

    public static SerializedRPGItem getData(ItemStack stack) {
        if (hasData(stack)) {
            SerializedRPGItem item = (SerializedRPGItem) ObjectUtils.read((String) NMSUtils.read(stack, "rpgitem", String.class));
            if (item != null) {
                item.setAmount(stack.getAmount());
            }
            return item;
        }
        return null;
    }

    public RPGItem getItem() {
        return RPGItem.getObject(RPGItem.class, "ID", id);
    }

    public ItemStack seralize() {
        if (this.durability != -1 && this.durability <= 0) {
            return null;
        }
        RPGItem item = getItem();
        ItemStack stack = item.getItemStack();
        if (this.durability != -1) {
            ItemMeta meta = stack.getItemMeta();
            List<String> lore = meta.getLore();
            lore.add(0, ChatColor.translateAlternateColorCodes('&',"&7Durability&f: &7" + durability + "&8/&7" + item.getDurability()));
            lore.add(0, "");
            if (getItem().isSoulbound()) {
                OfflinePlayer player = Bukkit.getOfflinePlayer(getPlayer());
                String name = player != null?player.getName():"null";
                lore.add("§7Soulbound to §8" + name);
            }
            meta.setLore(lore);
            stack.setItemMeta(meta);
        }
        stack.setAmount(this.getAmount());
        try {
            stack = NMSUtils.write(stack, "rpgitem", ObjectUtils.write(this));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return stack;
    }
}
