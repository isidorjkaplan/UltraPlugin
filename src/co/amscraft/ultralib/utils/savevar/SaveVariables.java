package co.amscraft.ultralib.utils.savevar;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.modules.Module;
import co.amscraft.ultralib.utils.ObjectUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SaveVariables {
    private static Set<Field> fields = new HashSet<>();
    public static void addClass(Class<?> clazz) {
            for (Field f: clazz.getDeclaredFields()) {
                //System.out.println(f + ": " + ann);
                if (f.isAnnotationPresent(SaveVar.class)) {

                    fields.add(f);
                    //load(f);
                }
            }
    }

    public static void load() {
        //System.out.println("Fields: " + fields);
        for (Field f: fields) {
            load(f);
        }
    }

    public static String getPath(Field f) {
        return f.getDeclaringClass().getName() + "." + f.getName();
    }
    public static void load(Field f) {
        Module m = Module.get(f.getDeclaringClass());
        SaveVar ann =  f.getAnnotation(SaveVar.class);

        if (m != null && ann != null) {
            File file = new File(m.getDataFolder() + "/" + ann.file());
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            String path = getPath(f);
            if (config.contains(path)) {
                try {
                    boolean a = f.isAccessible();
                    int modifiers = f.getModifiers();
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.setInt(f, modifiers & ~Modifier.FINAL);
                    f.setAccessible(true);
                    try {
                        f.set(null, ObjectUtils.read(config.getConfigurationSection(path)));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    f.setAccessible(a);
                    modifiersField.setInt(f, modifiers);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void save() {
        for (Field f: fields) {
            save(f);
        }
    }
    public static void save(Field f) {
        Module m = Module.get(f.getDeclaringClass());
        SaveVar ann =  f.getAnnotation(SaveVar.class);
        if (m != null && ann != null) {
            File file = new File(m.getDataFolder() + "/" + ann.file());
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            String path = getPath(f);
                boolean a = f.isAccessible();
                f.setAccessible(true);
                try {
                    config.set(path, null);
                    ObjectUtils.write(config.createSection(path), f.get(null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                f.setAccessible(a);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
