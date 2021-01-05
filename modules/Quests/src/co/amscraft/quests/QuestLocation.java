package co.amscraft.quests;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class QuestLocation {
    public double x;
    public double y;
    public double z;
    public String world;

    public QuestLocation() {
        this(Bukkit.getWorlds().get(0), 0, 0, 0);
    }

    public QuestLocation(World world, double x, double y, double z) {
        this.world = world.getName();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z);
    }

    @Override
    public String toString() {
        return world + " " + Math.round(x) + ", " + Math.round(y) + ", " + Math.round(z);
    }
}
