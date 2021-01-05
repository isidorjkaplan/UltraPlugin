package co.amscraft.ultralib.utils.serialize;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;

public class MinecraftItemSerializer extends AbstractSerializer<Object> {
    private static final Class<?>[] TYPES = new Class<?>[]{ItemStack.class, PotionEffectType.class};

    @Override
    public boolean use(Class<?> type) {
        for (Class c : TYPES) {
            if (c.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void write(ConfigurationSection config, Object object, Set<Object> added) {
        if (object != null) {
            if (object instanceof PotionEffectType) {
                config.set("clazz", PotionEffectType.class.getName());
                config.set("value", ((PotionEffectType) object).getName());
            } else {
                config.set("value", object);
            }
        }
    }

    @Override
    public Object read(ConfigurationSection config) {
        Class clazz = getClass(config);
        if ((ItemStack.class.isAssignableFrom(clazz))) {
            return config.getItemStack("value");
        } else if (PotionEffectType.class.isAssignableFrom(clazz)) {
            return PotionEffectType.getByName(config.getString("value"));
        }
        return null;
    }

}
