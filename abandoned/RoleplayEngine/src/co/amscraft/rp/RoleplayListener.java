package co.amscraft.rp;

import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultralib.utils.NMSUtils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class RoleplayListener implements Listener {
    @EventHandler
    public void playerBookOpenEvent(PlayerInteractEvent evt) {
        ItemStack stack = evt.getPlayer().getInventory().getItemInMainHand();
        if (stack.getType()  == Material.WRITTEN_BOOK) {
            try {
                String uuid = NMSUtils.read(stack, "RPCard", String.class);
                if (uuid != null) {
                    UltraPlayer player = UltraPlayer.getPlayer(UUID.fromString(uuid));
                    if (player != null) {
                        ItemStack book = player.getData(RoleplayData.class).getCard(EditorSettings.getSettings(evt.getPlayer()));
                        if (!book.equals(stack)) {
                            evt.getPlayer().getInventory().setItemInMainHand(book);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
