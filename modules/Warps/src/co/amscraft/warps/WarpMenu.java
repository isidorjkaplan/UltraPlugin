package co.amscraft.warps;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.utils.NMSUtils;
import co.amscraft.ultralib.utils.ObjectUtils;
import co.amscraft.ultralib.utils.savevar.SaveVar;
import io.netty.util.internal.ObjectUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class WarpMenu  extends UltraObject {
    @SaveVar
    public static final String MENU_NAME = "UltraWarps";
    public static String getMenuName() {
        return ChatColor.translateAlternateColorCodes('&',MENU_NAME);
    }

    @FieldDescription(help = "The key is the position in the GUI, the integer value is the warp")
    public Set<Warp> warps = new HashSet<>();
    private String name;



    public static class Warp {
        private int slot;
        private String warp;
        private Material item;
        private byte itemData;
        private String display;
        private String lore = "";

        @Override
        public int hashCode() {
            return slot;
        }

        @Override
        public String toString() {
            return slot + "{" + warp + "}";
        }

        public int getSlot() {
            return slot;
        }

        public Material getItemType() {
            return item;
        }

        public ItemStack getItemStack() {
            ItemStack stack = ObjectUtils.getItemStack(item, 1, ChatColor.translateAlternateColorCodes('&', display), ChatColor.translateAlternateColorCodes('&', lore), (byte)itemData);
            stack = NMSUtils.write(stack, "warp", this.warp);
            return stack;
        }

        public String getWarp() {
            return warp;
        }
    }


    public Inventory getGUI() {
        int size = 0;
        for (Warp warp: warps) {
            if (warp.getSlot() > size) {
                size = warp.getSlot();
            }
        }
        Inventory inventory = Bukkit.createInventory(null, (size + (9-size%9)), getMenuName());

        for (Warp warp: warps) {
            inventory.setItem(warp.getSlot(), warp.getItemStack());
        }
        return inventory;
    }

    public static WarpMenu getWarpMenu(String name) {
        return getObject(WarpMenu.class, "name", name);
    }

    @Override
    public String toString() {
        return name;
    }
}
