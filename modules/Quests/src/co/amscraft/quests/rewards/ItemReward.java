package co.amscraft.quests.rewards;

import co.amscraft.quests.Reward;
import co.amscraft.ultralib.editor.EditorCheck;
import co.amscraft.ultralib.editor.FieldDescription;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemReward extends Reward {
    public Item item = new Item();

    @Override
    public void give(Player player) {
        player.getInventory().addItem(item.getItem());
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
