package co.amscraft.spawn;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SerializableLocation {
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;
    private String world;

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    public SerializableLocation() {
        this.world = Bukkit.getWorlds().get(0).getName();
    }

    public SerializableLocation(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.pitch = location.getPitch();
        this.yaw = location.getYaw();
        this.world = location.getWorld().getName();
    }
}
