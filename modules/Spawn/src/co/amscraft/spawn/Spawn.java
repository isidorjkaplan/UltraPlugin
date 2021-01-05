package co.amscraft.spawn;

import co.amscraft.ultralib.UltraObject;
import org.bukkit.Location;

public class Spawn extends UltraObject {
    private SerializableLocation location = new SerializableLocation();
    private String permission = "UltraLib.Spawns.<spawn>";
    private int priority = 0;

    @Override
    public String toString() {
        String[] args = permission.split("[.]");
        return args[args.length-1];
    }

    public SerializableLocation getLocation() {
        return location;
    }

    public static Spawn getSpawn(String name) {
        for (Spawn spawn: getList(Spawn.class)) {
            if (spawn.toString().equalsIgnoreCase(name)) {
                return spawn;
            }
        }
        return null;
    }

    public void setLocation(Location location) {
        this.location = new SerializableLocation(location);
    }

    public String getPermission() {
        return permission;
    }

    public int getPriority() {
        return priority;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
