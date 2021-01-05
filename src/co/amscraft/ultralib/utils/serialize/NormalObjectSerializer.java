package co.amscraft.ultralib.utils.serialize;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.utils.ObjectSerializer;
import co.amscraft.ultralib.utils.ObjectUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Set;

public class NormalObjectSerializer extends AbstractSerializer<Object> {

    /**
     * Checks if a field is seralizable
     *
     * @param field The field to check
     * @return If this field is meant to be written to the file
     */
    private static boolean canSerialize(Field field) {
        return field != null && !Modifier.isStatic(field.getModifiers()) && (field.getAnnotation(FieldDescription.class) == null || field.getAnnotation(FieldDescription.class).save());
    }

    @Override
    public boolean use(Class type) {
        return true;
    }

    @Override
    public void write(ConfigurationSection config, Object object, Set<Object> added) {
        for (Field field : ObjectUtils.getFields(object.getClass())) {
            if (canSerialize(field)) {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                try {
                    //System.out.println(field.getName() + ": " + field.get(object));
                    Object o = field.get(object);
                    if (o != null) {
                        ConfigurationSection section = config.createSection(field.getName());
                        ObjectSerializer.write(section, o, added);

                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                field.setAccessible(accessible);
            }
        }
    }

    @Override
    public Object read(ConfigurationSection config) {
        try {
            Class<?> clazz = getClass(config);
            Object object = clazz.newInstance();
            for (Field field : ObjectUtils.getFields(clazz)) {
                if (canSerialize(field)) {
                    boolean a = field.isAccessible();
                    field.setAccessible(true);
                    ConfigurationSection read = config.getConfigurationSection(field.getName());
                    if (read != null) {
                        Object value = ObjectSerializer.read(read);
                        if (/*!field.getType().isPrimitive() ||*/ value != null) {
                            field.set(object, value);
                        }
                    }
                    field.setAccessible(a);
                }
            }
            return object;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
