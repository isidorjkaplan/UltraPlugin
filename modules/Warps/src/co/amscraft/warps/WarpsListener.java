package co.amscraft.warps;

import co.amscraft.ultralib.utils.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class WarpsListener implements Listener {
    @EventHandler
    public static void onInventoryClickEvent(InventoryClickEvent evt) {
        if (evt.getView().getTitle().equals(WarpMenu.getMenuName())) {
            ItemStack stack = evt.getCurrentItem();
            if (stack != null) {
                String warp = NMSUtils.read(stack, "warp", String.class);
                if (warp != null) {
                    evt.setCancelled(true);
                    Bukkit.dispatchCommand(evt.getWhoClicked(), "warp " + warp);
                }
            }
        }
    }
}
