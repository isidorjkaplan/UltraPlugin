package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.editor.EditorCheck;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.utils.NMSUtils;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.SpellThread;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GiveItem extends Action implements Listener {
    @FieldDescription(help = "How long they keep the item for, put to -1 for infinite", unit = "seconds")
    public double duration = 10;
    public Item item = new Item();

    public static void checkInventory(Inventory inventory) {
        long current = System.currentTimeMillis();
        for (int i = 0; i < inventory.getSize(); i++) {
            try {
                Object o = NMSUtils.read(inventory.getItem(i), "expires", String.class);
                if (o != null && !o.equals("")) {
                    //System.out.println(current + ", " + o );
                    long time = Long.parseLong(o + "");
                    if (current > time) {
                        inventory.setItem(i, null);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        if (target.getObject() instanceof Player) {
            ItemStack stack = item.getItem();
            if (stack != null) {
                if (duration >= 0) {
                    try {
                        stack = NMSUtils.write(stack, "expires", (System.currentTimeMillis() + Math.round((duration * 1000))) + "");
                        //System.out.println(NMSUtils.read(stack, "expires", String.class) + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Player player = ((Player) target.getObject());
                player.getInventory().addItem(stack);
                SpellThread thread = new SpellThread() {
                    public void cast() {
                        checkInventory(player.getInventory());
                    }
                };
                thread.runTaskLater(UltraLib.getInstance(), Math.round((duration + 1) * 20));
                spell.getThreads().add(thread);
            }
        }
    }

    @EventHandler
    public void inventoryCheck(InventoryOpenEvent evt) {
        checkInventory(evt.getInventory());
    }

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent evt) {
        checkInventory(evt.getPlayer().getInventory());
    }

    public static class Item {
        @FieldDescription(help = "Cords of the chest that the item will be copied from")
        public int x = 0, y = 0, z = 0;
        public String world = Bukkit.getWorlds().get(0).getName();
        @FieldDescription(help = "The slot of the chest that will be copied. -1 < slot < chest_size")
        public int slot = 0;

        public Item() {
            try {
                EditorCheck.register(Item.class.getField("world"), new EditorCheck("You must enter a valid world: " + Bukkit.getWorlds()) {
                    @Override
                    public boolean check(Object object, CommandSender sender) {
                        return Bukkit.getWorld(object + "") != null;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {

            return this.getItem() != null ? this.getItem() + "" : world + "[" + x + ", " + y + ", " + z + "]" + "[slot=" + slot + "]";
        }

        public ItemStack getItem() {
            try {
                Location location = new Location(Bukkit.getWorld(world), x, y, z);
                if (location.getBlock().getType() == Material.CHEST) {
                    Chest chest = (Chest) location.getBlock().getState();
                    return chest.getBlockInventory().getItem(slot).clone();
                }
            } catch (Exception e) {

            }
            return null;
        }
    }


}
