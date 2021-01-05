package co.amscraft.ultralib.utils.serialize;

import co.amscraft.ultralib.utils.ObjectSerializer;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Array;
import java.util.Set;

public class ArraySeralizer extends AbstractSerializer<Object> {
    @Override
    public boolean use(Class<?> type) {
        return type.isArray();
    }

    @Override
    public void write(ConfigurationSection config, Object object, Set<Object> added) {
        config.set("size", Array.getLength(object));
        for (int i = 0; i < Array.getLength(object); i++) {
            Object o = Array.get(object, i);
            if (o != null) {
                ObjectSerializer.write(config.createSection(i + ""), o, added);
            }
        }
    }

    @Override
    public Object read(ConfigurationSection config) {
        Object array = Array.newInstance(getClass(config).getComponentType(), config.getInt("size"));
        // boolean primitive = isPrimitive(array.getClass().getComponentType());
        for (int i = 0; i < Array.getLength(array); i++) {
            Object o = config.get(i + "");
            if (o != null) {
                try {
                    o = ObjectSerializer.read((ConfigurationSection) o);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            Array.set(array, i, o);
        }
        return array;
    }


}
