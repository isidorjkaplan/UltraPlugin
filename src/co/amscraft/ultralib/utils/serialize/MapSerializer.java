package co.amscraft.ultralib.utils.serialize;

import co.amscraft.ultralib.utils.ObjectSerializer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapSerializer extends AbstractSerializer<Map> {
    @Override
    public boolean use(Class<?> type) {
        //System.out.println(type);
        return Map.class.isAssignableFrom(type);
    }

    @Override
    public void write(ConfigurationSection config, Map object, Set<Object> added) {
        //System.out.println(object);
        Object[] keys = new Object[object.size()];
        Object[] values = new Object[object.size()];
        int i = 0;
        for (Object key : object.keySet()) {
            keys[i] = key;
            values[i] = object.get(key);
            i++;
        }
        ObjectSerializer.write(config.createSection("keys"), keys, added);
        ObjectSerializer.write(config.createSection("values"), values, added);
    }

    @Override
    public Map read(ConfigurationSection config) {
        try {
            Object[] keys = (Object[]) (ObjectSerializer.read(config.getConfigurationSection("keys")));
            Object[] values = (Object[]) (ObjectSerializer.read(config.getConfigurationSection("values")));
            Map map = (Map) getClass(config).newInstance();
            for (int i = 0; i < keys.length; i++) {
                map.put(keys[i], values[i]);
            }
            return map;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }


}
