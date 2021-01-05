package co.amscraft.traits;

import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public abstract class Requirement {
    public abstract boolean canSee(Player player);

    @Override
    public String toString() {
        String string = this.getClass().getSimpleName() + "[";
        for (Field field : this.getClass().getFields()) {
            try {
                string += field.getName() + "=" + field.get(this) + ",";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        string = string + "]";
        return string;
    }
}
