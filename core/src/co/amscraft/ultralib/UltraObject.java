package co.amscraft.ultralib;


import co.amscraft.ultralib.editor.EditorData;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.events.UltraObjectCreationEvent;
import co.amscraft.ultralib.modules.Module;
import co.amscraft.ultralib.utils.ObjectUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by Izzy on 2017-08-23.
 */
public abstract class UltraObject {
    @FieldDescription(show = false)
    /**
     * A field that stores objects recieved over a network before they have been accepted or rejected
     */
    public static List<UltraObject> networkReceivedObjects = new ArrayList<>();
    /**
     * The map that stores all of the currently loaded UltraObject's by type
     */
    private static HashMap<Class<? extends UltraObject>, Set<? extends UltraObject>> map = new HashMap<>();
    /**
     * A map of the file's for each UltraObject Type
     */
    private static HashMap<Class<? extends UltraObject>, FileConfiguration> configs = new HashMap<>();
    @FieldDescription(show = false)
    /**
     * The unique ID of the UltraObject
     */
    public int ID;

    @FieldDescription(show = false, save = false)

    /**
     * The constructor for UltraObject, the variable states weather or not it should be registered to the list of active UltraObjects
     */
    public UltraObject(boolean register) {

        if (register) {
            if (register(this)) {
                this.ID = nextId(this.getClass());
            }
        }

    }


    /**
     * The no-args constructor to create an UltraObject
     */
    public UltraObject() {
        this(true);
    }

    /**
     * A method for getting an UltraObject class by it's simple name
     *
     * @param name The simple name
     * @param <T>  The object type
     * @return The object type class
     */
    public static <T extends UltraObject> Class<T> getClass(String name) {
        for (Class<?> type : EditorData.getRegisteredDatatypes()) {
            if (type.getSimpleName().equalsIgnoreCase(name)) {
                return (Class<T>) type;
            }
        }
        return null;
    }

    /**
     * Get the next available ID for a given type
     *
     * @param type The class of the type you  want to get an ID for
     * @param <T>  The Type you want to get an ID for
     * @return The next available ID number
     */
    public static <T extends UltraObject> int nextId(Class<T> type) {
        int highest = 0;
        for (UltraObject obj: getList(type)) {
            if (obj.ID > highest) {
                highest = obj.ID;
            }
        }
        return highest + 1;
    }

    /**
     * A method to register an UltraObject with the server so that it gets auto-saved and loaded
     *
     * @param object The object to register
     * @return weather or not it registered without any issues
     */
    public static boolean register(UltraObject object) {
        if (!getList(object.getClass()).contains(object)) {
            try {
                ((Collection<UltraObject>) getList(object.getClass())).add(object);
                new UltraObjectCreationEvent(object).dispatch();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * The method to load all of the objects of a given class from their file
     *
     * @param type The UltraObject class
     * @param <T>  The UltraObject type
     */
    public static <T extends UltraObject> void load(Class<T> type) {

        long start = System.currentTimeMillis();
        map.put(type, new HashSet<>());
        FileConfiguration config = getConfig(type);
        if (config.getConfigurationSection(type.getSimpleName()) != null) {
            for (String strId : config.getConfigurationSection(type.getSimpleName()).getKeys(false)) {
                try {
                    T object = (T) ObjectUtils.read(config.getConfigurationSection(type.getSimpleName() + "." + strId));
                    if (object != null) {
                        object.ID = Integer.parseInt(strId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        }
        ObjectUtils.debug(Level.INFO, "Loaded " + type.getSimpleName() + "'s in " + ((System.currentTimeMillis() - start) / 1000.0));
        try {
            purgeOldBackups(type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * A method to save all the objects of a given class from RAM to the server
     *
     * @param type The type you want to save
     */
    public static void save(Class<? extends UltraObject> type) {
        FileConfiguration config = getConfig(type);
        //ConfigurationSection section = config.getConfigurationSection(type.getSimpleName());
        for (UltraObject object : getList(type)) {
            object.save(config);
        }
        UltraLib.saveConfig(config, getFile(type));
    }

    /**
     * A method to get the config file for a given class
     *
     * @param type The type you want to get the config for
     * @return The ConfigFile for the class
     */
    public static FileConfiguration getConfig(Class<? extends UltraObject> type) {
        if (!configs.containsKey(type)) {
            configs.put(type, YamlConfiguration.loadConfiguration(getFile(type)));
        }
        return configs.get(type);
    }


    /**
     * A method to get the java file reference for the object's tyupe
     *
     * @param type The type of the object
     * @return The java file reference
     */
    public static File getFile(Class<? extends UltraObject> type) {
        try {
            return new File(Module.get(type).getDataFolder() + "/" + type.getSimpleName().toLowerCase() + ".yml");
        } catch (Exception e) {
            try {
                return new File(UltraLib.getInstance().getDataFolder() + "/" + type.getSimpleName() + ".yml");
            } catch (Exception e1) {
                e1.printStackTrace();
                return new File("");
            }
        }
    }

    /**
     * A method to get all the object's of any given type
     *
     * @param type The type to get the objects for
     * @param <T>  The type
     * @return The list of all the objects of that type
     */
    public static <T extends UltraObject> Set<T> getList(Class<T> type) {
        if (!map.containsKey(type)) {
            load(type);
        }
        return (Set<T>) map.get(type);
    }


    /**
     * A method to get the object based on the known content's of one of it's fields
     *
     * @param type       The type of the object
     * @param field      The name of the field you know
     * @param fieldValue The value you are checking against
     * @param <T>        The type of the UltraObject
     * @return The Object if it exists, may return null if no objects matched
     */
    public static <T extends UltraObject> T getObject(Class<T> type, String field, Object fieldValue) {
        try {
            Field f = ObjectUtils.getField(type, field);
            boolean accessible = f.isAccessible();
            f.setAccessible(true);
            for (T object : Collections.unmodifiableSet(getList(type))) {
                if (f.get(object).equals(fieldValue)) {
                    return object;
                }
            }
            f.setAccessible(accessible);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    /**
     * A command to purge all the backups from before 2 day's prior
     *
     * @param type
     */
    public static void purgeOldBackups(Class<? extends UltraObject> type) {
        purgeOldBackups(type, 86400 * 12);
    }

    /**
     * A method to purge all the backups beyond a given amount of seconds
     *
     * @param type    The object type to clear
     * @param seconds The amount of seconds back to save
     */
    public static void purgeOldBackups(Class<? extends UltraObject> type, long seconds) {
        long time = System.currentTimeMillis() - (1000 * seconds);
        try {
            File folder = new File(Module.get(type).getDataFolder() + "/" + type.getSimpleName().toLowerCase() + "_backups");
            if (folder.exists()) {
                try {
                    for (File backup : Arrays.asList(folder.listFiles())) {
                        if (folder.listFiles().length < 4) {
                            break;
                        }
                        try {
                            long fileTime = Long.parseLong(backup.getName().replace(".yml", ""));
                            if (time > fileTime) {
                                System.out.println("Deleted old backup: " + backup.getPath());
                                backup.delete();
                            }
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * An accessor method to get the ID of the object
     *
     * @return The ID of the object
     */
    public int getId() {
        return this.ID;
    }

    /**
     * A method to save the object to given FileConfig
     *
     * @param config The config to save to
     */
    public void save(FileConfiguration config) {
        try {
            if (!getList(this.getClass()).contains(this)) {
                this.ID = nextId(this.getClass());
                ((Set) getList(this.getClass())).add(this);
            }
            File file = new File(Module.get(this.getClass()).getDataFolder() + "/" + this.getClass().getSimpleName().toLowerCase() + "_backups/" + System.currentTimeMillis() + ".yml");
            config.save(file);
            String path = this.getClass().getSimpleName() + "." + this.getId();
            config.set(path, null);
            ObjectUtils.write(config.createSection(path), this);
            purgeOldBackups(this.getClass());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //config.set(this.getClass().getSimpleName() + "." + this.getId(), list);
        /*
        for (Field field : this.getClass().getFields()) {
            try {
                config.set(this.getClass().getSimpleName() + "." + this.getId() + "." + field.getName(), field.get(this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

    /**
     * A method to save the object to it's normal file
     */
    public void save() {
        FileConfiguration config = UltraObject.getConfig(this.getClass());
        this.save(config);
        UltraLib.saveConfig(config, getFile(this.getClass()));
    }

    /**
     * A method to entirely delete an object from the server
     */
    public void delete() {
        FileConfiguration config = getConfig(this.getClass());
        config.set(this.getClass().getSimpleName() + "." + this.getId(), null);
        try {
            config.save(getFile(this.getClass()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        getList(this.getClass()).remove(this);
    }

    /**
     * The ToString method for how to represent this object
     *
     * @return The object as a string
     */
    @Override
    public String toString() {
        return ObjectUtils.toString(this);
    }


}
