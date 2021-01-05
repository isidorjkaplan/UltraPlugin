package co.amscraft.ultralib.utils;

import co.amscraft.ultralib.utils.serialize.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class ObjectSerializer {
    /**
     * The maximum depth of the recursive write function
     */
    public static final int MAXIMUM_WRITE_DEPTH = 100;
    /**
     * A list of all the serializers in order from which should be checked firsto to which should be checked last
     */
    private static final AbstractSerializer[] SERIALIZERS = {new MinecraftItemSerializer(), new ParsableSerializer(), new UltraObjectSerializer(), new MapSerializer(), new CollectionsSeralizer(), new ArraySeralizer(), new NormalObjectSerializer()};

    /**
     * A method to get the seralizer associated with a given type
     *
     * @param type The type to get for
     * @param <T>  The type to get for
     * @return The seralizer to use for objects of that type
     */
    public static <T extends AbstractSerializer> T getSerializerByType(Class<T> type) {
        for (AbstractSerializer s : SERIALIZERS) {
            if (s.getClass().equals(type)) {
                return (T) s;
            }
        }
        return null;
    }

    /**
     * A method to write an object to a FileInterface
     *
     * @param object The object to write
     * @return The YamlConfiguration that represents that object
     */
    public static YamlConfiguration write(Object object) {
        return write(object, new HashSet<>());
    }

    /**
     * A method to write an object to a YamlConnfiguration while bearing in mind already seralized objects
     *
     * @param object The object to write
     * @param added  A list of already added objects
     * @return The YamlConfiguration of that object
     */
    protected static YamlConfiguration write(Object object, Set<Object> added) {
        YamlConfiguration config = new YamlConfiguration();
        write(config, object, added);
        return config;
    }

    /**
     * A method used to write an object to a string
     *
     * @param object The object to write
     * @return A string representation of that object
     */
    public static String writeToString(Object object) {
        return write(object).saveToString();
    }

    /**
     * A method to get the seralizer of any given object
     *
     * @param object The object you want to serailize
     * @return The apropriete seralizer to use
     */
    private static AbstractSerializer getSerializer(Object object) {
        return getSerializer(object.getClass());
    }

    /**
     * A method to get the serialzer of a given type
     *
     * @param type The type you want to get the seralizer for
     * @return The seralizer for that type
     */
    private static AbstractSerializer getSerializer(Class<?> type) {
        for (AbstractSerializer serializer : SERIALIZERS) {
            if (serializer.use(type)) {
                //System.out.println(serializer.getClass().getSimpleName() + " for type: " + type.getSimpleName());
                return serializer;
            }
            //System.out.println("Rejected " + serializer.getClass().getSimpleName() + " for type: " + type.getSimpleName());
        }
        System.out.println(type);
        return null;
    }

    /**
     * A method to write an object to a configuration section
     *
     * @param config The configuration section to write to
     * @param object The object you are writing
     */
    public static void write(ConfigurationSection config, Object object) {
        write(config, object, new HashSet<>());
    }

    /**
     * A method for writing an object to a config, keeping in mind already added objects
     *
     * @param config The config to write to
     * @param object The object you are writing
     * @param added  A list of the objects you have already added
     */
    public static void write(ConfigurationSection config, Object object, Set<Object> added) {
        //System.out.println(object + ": " + config + ": " + (added.toString().length()>50?added.toString().substring(0, 50):added.toString()));
        int count = ObjectUtils.countInStacktrace(ObjectSerializer.class.getName() + ".write");
        if (count <= MAXIMUM_WRITE_DEPTH) {
            if (!added.contains(object)) {
                config.set("clazz", object.getClass().getName());
                added = new HashSet<Object>(added);
                added.add(object);
                getSerializer(object).write(config, object, added);
            } else {
                ObjectUtils.debug(Level.WARNING, "Tried to double serialize object: " + object + " in " + added);
            }
        } else {
            ObjectUtils.debug(Level.WARNING, "Depth exception on object: " + object + " in " + added);
        }
    }


    /**
     * A method to read an object given its seralized string
     *
     * @param string The string form of the object
     * @return The deseralized object
     */
    public static Object read(String string) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(string);
            return read(config);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * /**
     * A method to read an object from a configuration section
     *
     * @param config The config to read from
     * @return The object you have read from it
     * @throws ClassNotFoundException
     */
    public static Object read(ConfigurationSection config) throws ClassNotFoundException {
        if (config.getString("clazz") == null) {
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.set("config", config);
            System.out.println("Null config! " + yaml.saveToString());
            return null;
        } else {
            return getSerializer(Class.forName(config.getString("clazz"))).read(config);
        }
    }
}
