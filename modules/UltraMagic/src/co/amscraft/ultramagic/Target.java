package co.amscraft.ultramagic;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultramagic.exceptions.InvalidTargetException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Izzy on 2017-10-15.
 */
public class Target<T> {
    private T object;

    public Target(T object) throws InvalidTargetException {
        if (object instanceof Location || object instanceof Entity || object instanceof Block) {
            this.object = object;
        } else {
            throw new InvalidTargetException();
        }
    }


    public static Target getTarget(List<Class<?>> types, Location location, double radius, Target... ignore) throws InvalidTargetException {
        Callable function = new Callable() {
            @Override
            public Object call() throws Exception {
                Location location1 = location.clone();
                for (Class<?> type : types) {
                    try {
                        return getTarget(type, location1, radius, ignore);
                    } catch (Exception e) {
                        if (!(e instanceof InvalidTargetException)) {
                            e.printStackTrace();
                        }
                    }
                }
                throw new InvalidTargetException();
            }
        };
        try {
            if (Bukkit.isPrimaryThread()) {
                return (Target)function.call();
            } else {
                return (Target)Bukkit.getScheduler().callSyncMethod(UltraLib.getInstance(), function).get();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Target[] getTargets(List<Class<?>> types, Location location, double radius, Target... ignore) throws InvalidTargetException {
        location = location.clone();
        List<Target> list = new ArrayList<>();
        for (Class<?> type : types) {
            try {
                list.addAll(Arrays.asList(getTargets(type, location, radius, ignore)));
            } catch (InvalidTargetException e) {

            }
        }
        if (!list.isEmpty()) {
            Target[] targets = new Target[list.size()];
            for (int i = 0; i < targets.length; i++) {
                targets[i] = list.get(i);
            }
            return targets;
        }
        throw new InvalidTargetException();
    }

    public static Target[] getTargets(Class<?> type, Location location, double radius, Target... ignore) throws InvalidTargetException {
        ArrayList<Target> list = new ArrayList<>();
        for (Target target : getTargetsInBox(type, location, radius, radius, radius, ignore)) {
            if (target.getLocation().distance(location) <= radius) {
                list.add(target);
            }
        }
        if (!list.isEmpty()) {
            Target[] targets = new Target[list.size()];
            for (int i = 0; i < targets.length; i++) {
                targets[i] = list.get(i);
            }
            return targets;
        }
        throw new InvalidTargetException();
    }

    public static Target[] getTargetsInBox(List<Class<?>> types, Location location, double xLength, double yLength, double zLength, Target... ignore) throws InvalidTargetException {
        List<Target> list = new ArrayList<>();
        for (Class<?> type : types) {
            try {
                list.addAll(Arrays.asList(getTargetsInBox(type, location, xLength, yLength, zLength, ignore)));
            } catch (Exception e) {

            }
        }
        if (!list.isEmpty()) {
            Target[] targets = new Target[list.size()];
            for (int i = 0; i < targets.length; i++) {
                targets[i] = list.get(i);
            }
            return targets;
        }
        throw new InvalidTargetException();

    }

    public static Target[] getTargetsInBox(Class<?> type, Location location, double xLength, double yLength, double zLength, Target... ignore) throws InvalidTargetException {
        location = location.clone();
        xLength = Math.abs(xLength);
        yLength = Math.abs(yLength);
        zLength = Math.abs(zLength);
        ArrayList<Target> list = new ArrayList<>();
        if (Location.class.isAssignableFrom(type)) {
            for (double x = -xLength; x <= xLength; x += 1) {
                for (double y = -yLength; y <= yLength; y += 1) {
                    for (double z = -zLength; z <= zLength; z += 1) {
                        list.add(new Target(location.add(x, y, z)));
                        location.subtract(x, y, z);
                    }
                }
            }
            if (list.isEmpty()) {
                list.add(new Target(location.clone()));
            }
        } else if (Block.class.isAssignableFrom(type)) {
            //boolean asyncEnabled = AsyncCatcher.enabled;
            //AsyncCatcher.enabled = false;
            //System.out.println(xLength + ", " + yLength + ", " + zLength + ", " + (location == null) + ", " + list + ", " + ignore);
            for (double x = -xLength; x <= xLength; x++) {
                for (double y = -yLength; y <= yLength; y++) {
                    for (double z = -zLength; z <= zLength; z++) {
                        try {
                            Block block = location.add(x, y, z).getBlock();
                            if (block.getType().isSolid()) {
                                list.add(new Target(block));
                            }
                            location.subtract(x, y, z);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (list.isEmpty() && location.getBlock().getType().isSolid()) {
                list.add(new Target(location.getBlock()));
            }
            //AsyncCatcher.enabled = asyncEnabled;
        } else if (Entity.class.isAssignableFrom(type)) {
            for (Entity e : location.getWorld().getNearbyEntities(location, xLength, yLength, zLength)) {
                if (type.isAssignableFrom(e.getClass())) {
                    list.add(new Target(e));
                }
            }
        }
        List<Object> objects = new ArrayList<>();
        for (Target t : ignore) {
            objects.add(t.getObject());
        }
        for (Target target : (List<Target>) list.clone()) {
            if (objects.contains(target.getObject())) {
                list.remove(target);
            }
        }
        if (!list.isEmpty()) {
            Target[] targets = new Target[list.size()];
            for (int i = 0; i < targets.length; i++) {
                targets[i] = list.get(i);
            }
            return targets;
        }
        throw new InvalidTargetException();
    }


    private static boolean contains(Target[] targets, Object object) {
        for (Target t : targets) {
            if (t.getObject().equals(object)) {
                return true;
            }
        }
        return false;
    }

    public static Target getTarget(Class<?> type, Location location, double radius, Target... ignore) throws InvalidTargetException {
        if (Block.class.isAssignableFrom(type) && location.getBlock().getType().isSolid()) {
            return new Target(location.getBlock());
        } else if (Location.class.isAssignableFrom(type)) {
            return new Target(location);
        }
        Target[] targets = getTargets(type, location, radius, ignore);
        if (targets.length == 0) {
            throw new InvalidTargetException();
        }
        Target t = targets[0];
        for (Target target : targets) {
            if (location.distance(target.getEyeLocation()) < location.distance(t.getEyeLocation())) {
                t = target;
            }
        }
        return t;
    }

    public Class<T> getType() {
        return (Class<T>) this.object.getClass();
    }

    public Location getLocation() {
        if (this.getObject() instanceof Location) {
            return (Location) this.getObject();
        } else if (this.getObject() instanceof Entity) {
            return ((Entity) this.getObject()).getLocation();
        } else {
            return ((Block) this.getObject()).getLocation();
        }
    }

    public T getObject() {
        return this.object;
    }

    public String getName() {
        if (this.getObject() instanceof Entity) {
            return ((Entity) this.getObject()).getName();
        }
        return this.getObject().getClass().getSimpleName();
    }

    public Location getEyeLocation() {
        return this.getObject() instanceof LivingEntity ? ((LivingEntity) this.getObject()).getEyeLocation() : this.getLocation();
    }
}
