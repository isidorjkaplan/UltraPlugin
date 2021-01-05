package co.amscraft.rpgitems;

import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.PlayerUtility;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultralib.tic.ServerTic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.entity.*;

import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class RPGListener implements Listener {
    @EventHandler
    public static void onPlayerInteractEvent(PlayerInteractEvent evt) {
        for (RPGItemPower.Action type: RPGItemPower.Action.values()) {
            if (evt.getAction().toString().contains(type.toString())) {
                runRPGItemTick(evt.getPlayer(), type);
            }
        }
    }

    @EventHandler
    public void onProjectileFireEvent(EntityShootBowEvent evt) {
        if (evt.getEntity() instanceof Player && ((Player) evt.getEntity()).getInventory().getItemInMainHand().equals(evt.getBow())) {
            SerializedRPGItem serializedRPGItem = SerializedRPGItem.getData(evt.getBow());
            if (serializedRPGItem != null) {
                serializedRPGItem.decreaseDurability();
                if (serializedRPGItem.isUpdated()) {
                    ((Player) evt.getEntity()).getInventory().setItemInMainHand(serializedRPGItem.seralize());
                }
            }
        }
    }


    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent evt) {
        ItemStack stack = evt.getItemDrop().getItemStack();
        SerializedRPGItem serializedRPGItem = SerializedRPGItem.getData(stack);
        if (serializedRPGItem != null && serializedRPGItem.getItem().isUndroppable()) {
            evt.setCancelled(true);
            evt.getPlayer().sendMessage(EditorSettings.getSettings(evt.getPlayer()).getError() + "You cannot drop this item!");
        }
    }

    @EventHandler
    public static void onPlayerDamageEvent(EntityDamageByEntityEvent evt) {
        if (evt.getDamager() instanceof Player) {
            Player player = ((Player) evt.getDamager()).getPlayer();
            ItemStack held = player.getInventory().getItemInMainHand();
            SerializedRPGItem serializedRPGItem = SerializedRPGItem.getData(held);
            if (serializedRPGItem != null) {
                serializedRPGItem.decreaseDurability();
                evt.setDamage(serializedRPGItem.getItem().getRandomDamage());
                ((Player)evt.getDamager()).getInventory().setItemInMainHand(serializedRPGItem.seralize());
            }
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent evt) {
        if (!evt.isCancelled() && evt.isSneaking()) {
            runRPGItemTick(evt.getPlayer(), RPGItemPower.Action.SHIFT);
        }
    }

    @EventHandler
    public void onPlayerPickupItemEvent(EntityPickupItemEvent evt) {
        ItemStack stack = evt.getItem().getItemStack();
        SerializedRPGItem rpgitem = SerializedRPGItem.getData(stack);
        if (rpgitem != null && (!(evt.getEntity() instanceof Player) || !rpgitem.attemptUsage((Player)evt.getEntity()))) {
            evt.setCancelled(true);
            if (evt.getEntity() instanceof Player) {
                UltraPlayer player = UltraPlayer.getPlayer(evt.getEntity());
                if (player != null) {
                    player.getData(PlayerUtility.class).sendActionbar(player.getData(EditorSettings.class).getError() + "You do not have permission to pickup this item!");
                }
            }
        }
    }

    public static void runRPGItemTick(Player player, SerializedRPGItem serializedRPGItem, RPGItemPower.Action type) {
        if (serializedRPGItem != null) {
            if (serializedRPGItem.attemptUsage(player)) {
                RPGItem item = serializedRPGItem.getItem();
                for (RPGItemPower power : item.getPowers()) {
                    if (power.getAction() == type) {
                        if (power.cast(player) && power.isUseDurability()) {
                            serializedRPGItem.decreaseDurability();
                        }
                    }
                }
            } else {
                UltraPlayer.getPlayer(player).getData(PlayerUtility.class).sendActionbar(EditorSettings.getSettings(player).getError() + "You do not have permission to use this item!");
            }
        }
    }

    /* if (serializedRPGItem.isUpdated()) {
                    inventory.setItem(slot, serializedRPGItem.seralize());
                }*/

    public static void runRPGItemTick(Player player, int slot, RPGItemPower.Action type) {
        Inventory inventory = player.getInventory();
        ItemStack held = inventory.getItem(slot);
        SerializedRPGItem serializedRPGItem = SerializedRPGItem.getData(held);
        runRPGItemTick(player, serializedRPGItem, type);
        if (serializedRPGItem != null && serializedRPGItem.isUpdated()) {
            inventory.setItem(slot, serializedRPGItem.seralize());
        }
    }

    public static void runRPGItemTickOffhand(Player player, RPGItemPower.Action type) {
        PlayerInventory inventory = player.getInventory();
        SerializedRPGItem item = SerializedRPGItem.getData(inventory.getItemInOffHand());
        if (type == RPGItemPower.Action.LEFT_CLICK) {
            type = RPGItemPower.Action.RIGHT_CLICK;
        } else if (type == RPGItemPower.Action.RIGHT_CLICK) {
            type = RPGItemPower.Action.LEFT_CLICK;
        }
        runRPGItemTick(player, item, type);
        if (item != null && item.isUpdated()) {
            inventory.setItemInOffHand(item.seralize());
        }
    }

    public static void runRPGItemTick(Player player, RPGItemPower.Action type) {
        PlayerInventory inventory = player.getInventory();
        runRPGItemTick(player, ((PlayerInventory) inventory).getHeldItemSlot(), type);
        runRPGItemTickOffhand(player, type);
        for (int i = 2; i <=5; i++) {
            runRPGItemTick(player, inventory.getSize() - i, type);
        }
    }

    @ServerTic(delay = 1, isAsync = false)
    public static void onServerTick() {
        for (Player player: Bukkit.getOnlinePlayers()) {
            runRPGItemTick(player, RPGItemPower.Action.TICK);
        }
    }

    @EventHandler
    public void onPlayerDamageEvent(EntityDamageEvent evt) {
        if (evt.getEntity() instanceof Player) {
            runRPGItemTick((Player)evt.getEntity(), RPGItemPower.Action.TAKE_DAMAGE);
        }
    }
}
