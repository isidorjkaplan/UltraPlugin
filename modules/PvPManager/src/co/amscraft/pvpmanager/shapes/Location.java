package co.amscraft.pvpmanager.shapes;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class Location {

    public double x;
    public double y;
    public double z;
    public float pitch;
    public float yaw;
    public String world;

    public Location() {
        this(Bukkit.getWorlds().get(0), 0, 0, 0, 0, 0);
    }

    public Location(World world, double x, double y, double z) {
        this(world, x, y, z, 0, 0);
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

    @Override
    public String toString() {
        return world + "[" + x + ", " + y + ", " + z + "]";
    }

    public double distance(org.bukkit.Location location) {
        if (location.getWorld() == this.getWorld()) {
            return location.distance(this.getLocation());
        }
        return -1;
    }

    public World getWorld() {
        return Bukkit.getWorld(this.world);
    }

    public org.bukkit.Location getLocation() {
        return new org.bukkit.Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }
}
