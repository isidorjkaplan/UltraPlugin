package co.amscraft.ultralib.utils.serialize;

import co.amscraft.ultralib.utils.ObjectSerializer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.Set;

public class CollectionsSeralizer extends AbstractSerializer<Collection> {
    @Override
    public boolean use(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }

    @Override
    public void write(ConfigurationSection config, Collection object, Set<Object> added) {
        Object[] array = object.toArray();
        ObjectSerializer.write(config.createSection("array"), array, added);
    }

    @Override
    public Collection read(ConfigurationSection config) {
        //if (config.conta)
        try {
            Collection list = (Collection) getClass(config).newInstance();
            Object[] array = (Object[]) ObjectSerializer.read(config.getConfigurationSection("array"));
            for (int i = 0; i < array.length; i++) {
                list.add(array[i]);
            }
            return list;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }


}
