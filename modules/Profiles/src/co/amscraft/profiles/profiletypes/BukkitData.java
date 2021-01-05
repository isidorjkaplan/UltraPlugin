package co.amscraft.profiles.profiletypes;

import co.amscraft.profiles.ProfileType;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class BukkitData extends ProfileType {
    public boolean op = false;
    public boolean flying = false;
    public GameMode gameMode = GameMode.SURVIVAL;
    public ItemStack[] inventory = new ItemStack[]{};
    public ItemStack[] enderChest = new ItemStack[]{};
    public Location location = new Location(Bukkit.getWorlds().get(0).getSpawnLocation());
    public int level = 0;
    public float exp = 0;
    public double health = 20;
    public double maxHealth = 20;
    public float exhaustion = 20;
    public int foodLevel = 20;

    @Override
    public void save() {
        UltraPlayer player = this.getPlayer();
        op = player.getBukkit().isOp();
        inventory = player.getBukkit().getInventory().getContents();
        inventory = Arrays.copyOf(inventory, inventory.length);
        enderChest = player.getBukkit().getEnderChest().getContents();
        enderChest = Arrays.copyOf(enderChest, enderChest.length);
        flying = player.getBukkit().isFlying();
        gameMode = player.getBukkit().getGameMode();
        location = new Location(player.getBukkit().getLocation());
        level = player.getBukkit().getLevel();
        exp = player.getBukkit().getExp();
        //maxHealth = player.getBukkit().getMaxHealth();
        health = player.getBukkit().getHealth();
        exhaustion = player.getBukkit().getExhaustion();
        foodLevel = player.getBukkit().getFoodLevel();


    }

    @Override
    public String getFailMessage() {
        if (Bukkit.getPluginManager().isPluginEnabled("Magic")) {
            /*com.elmakers.mine.bukkit.api.magic.MagicAPI api = com.elmakers.mine.bukkit.magic.MagicPlugin.getAPI();
            ItemStack stack = this.getPlayer().getBukkit().getInventory().getItemInMainHand();
            if (api.isWand(stack)) {
                return "You cannot hold a wand while switching characters";
                // System.out.println("Wand closed: " + api.getWand(stack).isInventoryOpen());
            }*/
        }
        return null;
    }

    @Override
    public void enable() {
        UltraPlayer player = this.getPlayer();
        if (inventory.length == 0) {
            inventory = new ItemStack[this.getPlayer().getBukkit().getInventory().getSize()];
        }
        if (enderChest.length == 0) {
            enderChest = new ItemStack[player.getBukkit().getEnderChest().getContents().length];
        }
        //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "magic:wandp " + player.getBukkit().getName() + " configure");
        /*if (Bukkit.getPluginManager().isPluginEnabled("Magic")) {
            com.elmakers.mine.bukkit.api.magic.MagicAPI api = com.elmakers.mine.bukkit.magic.MagicPlugin.getAPI();
            ItemStack stack = player.getBukkit().getInventory().getItemInMainHand();
            if (api.isWand(stack)) {
                api.getWand(stack).closeInventory();
                // System.out.println("Wand closed: " + api.getWand(stack).isInventoryOpen());
            }
        }*/
        player.getBukkit().closeInventory();
        for (int i = 0; i < inventory.length; i++) {
            player.getBukkit().getInventory().setItem(i, inventory[i]);
        }
        for (int i = 0; i < enderChest.length; i++) {
            player.getBukkit().getEnderChest().setItem(i, enderChest[i]);
        }
        player.getBukkit().setGameMode(this.gameMode);
        player.getBukkit().setFlying(this.flying);
        player.getBukkit().teleport(this.location.getLocation());
        player.getBukkit().setLevel(level);
        player.getBukkit().setExp(exp);
        //player.getBukkit().setMaxHealth(maxHealth);
        player.getBukkit().setHealth(health);
        player.getBukkit().setExhaustion(exhaustion);
        player.getBukkit().setFoodLevel(foodLevel);
        player.getBukkit().setOp(op);
    }

    public static class Location {
        public double x;
        public double y;
        public double z;
        public float pitch;
        public float yaw;
        public String world;

        public Location() {
            this(Bukkit.getWorlds().get(0), 0, 0, 0, 0, 0);
        }

        public Location(World world, double x, double y, double z, float yaw, float pitch) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.pitch = pitch;
            this.yaw = yaw;
            this.world = world.getName();
        }

        public Location(org.bukkit.Location location) {
            this(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        }

        public org.bukkit.Location getLocation() {
            return new org.bukkit.Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
        }
    }
}
