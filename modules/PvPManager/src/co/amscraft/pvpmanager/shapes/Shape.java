package co.amscraft.pvpmanager.shapes;

import org.bukkit.Location;

import java.lang.reflect.Field;

public abstract class Shape {
    public abstract boolean isInside(Location location);

    public abstract Cube toCube();

    public Shape clone() {
        Shape shape = null;
        try {
            shape = this.getClass().newInstance();
            for (Field field : this.getClass().getFields()) {
                field.set(shape, field.get(this));
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return shape;

    }
}
