package co.amscraft.ultralib.utils.serialize;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractSerializer<T> {

    /**
     * A static constant of the wrapper types
     */
    public static final Set<Class<?>> WRAPPER_TYPES;

    static {
        Set<Class<?>> ret = new HashSet<>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        ret.add(ItemStack.class);
        ret.add(PotionEffectType.class);
        WRAPPER_TYPES = Collections.unmodifiableSet(ret);
    }

    /**
     * A method to get the class of the object stored in the root of a file
     *
     * @param section The config section
     * @return The type of the object stored here
     */
    public static Class<?> getClass(ConfigurationSection section) {
        try {
            return Class.forName(section.getString("clazz"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * A method that checks if the object is "primitive" for the sake of the ParsableSerialzer
     *
     * @param type The object type
     * @return If it is primitive
     */
    public static boolean isPrimitive(Class<?> type) {
        return (type.isPrimitive() || String.class.isAssignableFrom(type) || WRAPPER_TYPES.contains(type));
    }

    /**
     * Asks the serialzer if it should use this protocal for a given type
     *
     * @param type The type it is asking about
     * @return Weather or not to use this serialzer for that type
     */
    public abstract boolean use(Class<?> type);

    /**
     * The method to use this serialzer to serialize a given object
     *
     * @param config The file you are writing it to
     * @param object The object you are writing
     * @param added  A list of object's already added so that you do not recursivly write an already written object
     */
    public abstract void write(ConfigurationSection config, T object, Set<Object> added);

    /**
     * A method to read the object stored at the root of a given file section
     *
     * @param config The file to read from
     * @return The object that has been read
     */
    public abstract T read(ConfigurationSection config);

}
