package co.amscraft.ultralib.utils;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.editor.EditorData;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.utils.serialize.AbstractSerializer;
import co.amscraft.ultralib.utils.serialize.NormalObjectSerializer;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.reflections.Reflections;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by Izzy on 2017-11-23.
 */
public class ObjectUtils {


    public static final int DEFAULT_SERIALIZATION_DEPTH = 50;
    private static Reflections reflections = null;

    /**
     * This method stops anybody from creating a new ObjectUtils object, it is a class with static methods only
     */
    private ObjectUtils() {

    }

    /**
     * A method used to read a seralized configuration section and parse it into an Object
     *
     * @param section The configuration section of the seralized object
     * @return The desearlized Object value
     */
    public static Object read(ConfigurationSection section) {
        if (section.get("clazz") != null) {
            try {
                return ObjectSerializer.read(section);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (section.get("class") != null) {
            // ObjectUtils.debug(Level.WARNING, "Using deprecated read method: " + section.get("class"));
            return readDeprecated(section);
        }
        return null;
    }

    @Deprecated
    public static Object readDeprecated(ConfigurationSection section) {
        try {//Note: Configuration sections are alot like a Map<String, Object> that is stored to a file, with key's and values
            Class<?> type = Class.forName(section.getString("class"));//Check for the key "class" to get the type of Class that the object is
            if (UltraObject.class.isAssignableFrom(type) && section.getKeys(false).size() == 2) {//If it is an UltraObject and only stores an ID, then it is a pointer and not an actual seralized object
                return UltraObject.getObject((Class<? extends UltraObject>) type, "ID", section.getInt("ID"));//In this case, retrive the object it is pointing to and return it
            }
            return readObject(section, type.newInstance());//Otherwise, read the configuration section to a new instance of the type and return it.
        } catch (Exception e) {
            ObjectUtils.debug(Level.WARNING, "Error while reading in object: " + section.getName());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * A method used to read a seralized configuration section and parse it into an Object
     *
     * @param section The configuration section of the seralized object
     * @return The desearlized Object value
     */
    @Deprecated
    public static Object readObject(ConfigurationSection section, Object object) {
        if (section.get("clazz") != null) {
            try {
                Object read = ObjectSerializer.read(section);
                for (Field field : getFields(object.getClass())) {
                    boolean a = field.isAccessible();
                    field.setAccessible(true);
                    try {
                        field.set(object, field.get(read));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    field.setAccessible(a);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if (section.get("class") != null) {
            //ObjectUtils.debug(Level.WARNING, "Using deprecated read method: " + section.get("class"));
            try {
                return readDepracted(section, object);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param config The config of the seralized object
     * @param object The object you are reading to
     * @return The object you read to with all its values changed
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     */
    @Deprecated
    public static Object readDepracted(ConfigurationSection config, Object object) throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<?> type = Class.forName(config.getString("class"));//Gets the class if the seralized object
        if (!type.isAssignableFrom(object.getClass())) {//If the object you passed is not castable from the object in the file then it is not a valid object and throw an exception
            throw new ClassCastException();
        }
        if (object instanceof List) {//If the seralized object is a list it has a unique protocal for reading in the object
            Class<?> listType = Object.class;//By default any list is a list of Objects
            try {
                listType = Class.forName(config.getString("type"));//If the list has a stored type, retrive that type for reference
            } catch (NullPointerException e) {
                if (config.get("type") != null) {//If the type is specified and invalid, print the exception.
                    e.printStackTrace();
                }
            }
            for (String key : config.getKeys(false)) {//For each of the saved objeccts
                if (!key.equals("class") && !key.equals("type") && !key.equals("size")) {//If it is not the class reference, the size reference, or the type reference, it is a seralized object
                    int i = Integer.parseInt(key);//The integer value of the key is its Index in the ArrayList
                    while (((List) object).size() <= i) {//If the index is greater then the array size, grow the array with null objects and place the object at the desired index
                        ((List) object).add(null);
                    }
                    if (config.get(key) instanceof ConfigurationSection) {//If the object stored in this section is another seralized object
                        ((List) object).set(i, read(config.getConfigurationSection(key)));//Retrive the seralized object annd put it in the created list
                    } else {//Otherwise it is a primitive datatype
                        //System.out.println(config.get(key));
                        if (config.get(key) == null || (config.get(key) instanceof String && config.getString(key).equals("null"))) {
                            ((List) object).set(i, null);//If it is null then add a null object to the list
                        } else {
                            ((List) object).set(i, parse(listType, config.get(key)));//If not, parse the object from its String value to its desired type
                        }
                    }
                }
            }
            if (config.get("size") != null) {//If the list is smaller then its saved size, grow the list with null objects to match its initial size
                while (((List) object).size() < config.getInt("size")) {
                    ((List) object).add(null);
                }
            }
        } else if (Map.class.isAssignableFrom(type)) {//If it is a seralized map
            List keys = (List) read(config.getConfigurationSection("keys"));//Then deseralize the list of keys and values
            List values = (List) read(config.getConfigurationSection("values"));
            for (Object key : keys) {//construct the map matching the key indecies and the value indecies
                ((Map) object).put(key, values.get(keys.indexOf(key)));
            }
        } else {//If it is a normal object, the default protocal
            for (Field field : getFields(type)) {//For each field that the type (including superclasses) has
                if (canSerialize(field)) {//If the type is seralizable
                    boolean accessible = field.isAccessible();//Store if the field is private or not
                    field.setAccessible(true);//If it is private, temporarilly make it public so that you can interface directly with it
                    if (config.contains(field.getName())) {//If the config map has the key the has the field name
                        Object o = config.get(field.getName());//Get the  object stored in the console
                        if (o != null) {//If its not null
                            if (o instanceof ConfigurationSection) {//If the objecct is a seralized object
                                o = read((ConfigurationSection) o);//Deseralize the object
                                if (Object[].class.isAssignableFrom(field.getType()) && o instanceof List) {//If the object is supposed to be a fixed size array and it is currently a list
                                    field.set(object, Array.newInstance(field.getType().getComponentType(), ((List) o).size()));//Convert it from a list to an array
                                    for (int i = 0; i < ((List) o).size(); i++) {
                                        Array.set(field.get(object), i, ((List) o).get(i));
                                    }
                                } else {
                                    field.set(object, o);//set the field to the now deserarlized object
                                }
                            } else if (field.getType().isAssignableFrom(o.getClass())) {
                                field.set(object, o);
                            } else {
                                o = parse(field.getType(), o + "");
                                if (o != null) {
                                    field.set(object, o);
                                }
                            }
                        }
                    }
                    field.setAccessible(accessible);//Set the field's public/private value to what it is supposed to be
                }
            }
        }
        return object;//return the deseralized object
    }

    /**
     * A method for converting a string to a color code
     *
     * @param string The string
     * @return The color code
     */
    public static ChatColor getColor(String string) {
        return ChatColor.getByChar(string.replace("§", ""));
    }

    /**
     * A utility method for creating an item quickly
     *
     * @param item   The item type
     * @param amount The amount of items
     * @param name   The name of the item
     * @param lore   The item lore as a list, each item is its own line
     * @param data   The item byte data
     * @return The constructed item
     */
    public static ItemStack getItemStack(Material item, int amount, String name, List<String> lore, byte data) {
        ItemStack stack = new ItemStack(item, amount);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    /**
     * A utility method for creating an item quickly
     *
     * @param item   The item type
     * @param amount The amount of items
     * @param name   The name of the item
     * @param lore   The item lore as a single string
     * @param data   The item byte data
     * @return The constructed item
     */
    public static ItemStack getItemStack(Material item, int amount, String name, String lore, byte data) {
        List<String> list = new ArrayList<>();
        String[] words = lore.split(" ");
        String line = "";
        int length = 0;
        String color = "§7";
        for (String word : words) {
            line += word + " ";
            length += word.length();
            if (length >= 20) {
                list.add(color + line);
                length = 0;
                try {
                    if (line.contains("§")) {
                        color = "§" + Character.toString(line.charAt(line.lastIndexOf("§") + 1));
                    }
                } catch (Exception e) {

                }
                line = "";
            }
        }
        if (!line.equals("")) {
            list.add("§7" + line);
        }
        return getItemStack(item, amount, name, list, data);
    }

    /**
     * A utility method for creating an item quickly, assuming the item has no metadata
     *
     * @param item   The item type
     * @param amount The amount of items
     * @param name   The name of the item
     * @param lore   The item lore
     * @return The constructed item
     */
    public static ItemStack getItemStack(Material item, int amount, String name, String lore) {
        return getItemStack(item, amount, name, lore, (byte) 0);
    }


    /**
     * A method that is used to write ArrayLists to a seralized configuration section
     *
     * @param section The section you are writing to
     * @param object  The list you are seralizing
     * @param depth   The depth you are allowed to seralize (after a certain depth it will throw an exception)
     * @param added   The list of objects you have already seralized, this is to prevent internal references to the object which might result in seralizing an object inside itself leading to infinite recursion with no exit conditon
     * @throws IllegalAccessException
     */
    @Deprecated
    private static void writeList(ConfigurationSection section, Object object, int depth, HashSet<Object> added) throws IllegalAccessException {
        //System.out.println(section.getName() + ": " + object);
        if (object.getClass().isArray()) {//If it is an array
            section.set("class", ArrayList.class.getName());//Specify that the object should be stored as an ArrayList instead
        }
        List list;//Creates the list object to seralize
        if (object instanceof List) {
            list = (List) object;//If it is already a list just pass the pointer
        } else if (object instanceof Set) {
            list = new ArrayList((Set) object);
        } else {
            list = new ArrayList();//If it is an Array create an array list
            for (int i = 0; i < Array.getLength(object); i++) {
                list.add(Array.get(object, i));
            }
        }
        // System.out.println(list); //Debugging info
        section.set("size", list.size());//Write the size of the list to the file
        for (int i = 0; i < (list.size()); i++) {//For each index of the array list
            Object o = (list).get(i);//Grab the object from the list
            if (o != null && (o.getClass().isPrimitive() || o instanceof String || getWrapperTypes().contains(o.getClass()) || o instanceof ItemStack)) {
                section.set(i + "", o);//If it is a primitive object write it directly to the file
            } else if (o == null) {
                section.set(i + "", null);//If the object is null write that it is null to the file, this will leave the index empty and save storage space
            } else if (o.getClass().isEnum()) {
                section.set(i + "", o + "");//If it is an enum write it as its toString() method
            } else if (o instanceof PotionEffectType) {
                section.set(i + "", ((PotionEffectType) o).getName());//If it is a potion effect, write its name
            } else if (o instanceof UltraObject) {
                section.set(i + ".class", o.getClass().getName());//If it is an UltraObject inside of a list, dont write the object, just write a pointer
                section.set(i + ".ID", ((UltraObject) o).getId());//Write the ID your pointing to
            } else {
                section.set(i + "", null);//Clear the section from any preexisting stuff
                write(section.createSection(i + ""), o, depth - 1, added);//Create a new section and seralize the object at depth-1 passing all relevent information
            }
        }
    }

    /**
     * The method used to write a Map to a file
     *
     * @param section The section to write it to
     * @param object  The map object
     * @param depth   The depth left to seralize
     * @param added   The alreaady seralized objects
     * @throws IllegalAccessException
     */
    @Deprecated
    private static void writeMap(ConfigurationSection section, Object object, int depth, HashSet<Object> added) throws IllegalAccessException {
        List<Object> keys = new ArrayList<>();//Create a list for the keys
        for (Object key : ((Map) object).keySet()) {
            keys.add(key);//add all the keys to that list
        }
        List<Object> values = new ArrayList<>();//create an object for the values
        for (Object value : ((Map) object).values()) {
            values.add(value);//add all the values to it
        }
        section.set("keys", null);//clear the current keys
        section.set("values", null);//clear the current vallues
        write(section.createSection("keys"), keys, depth - 1, added);//write the keys to the list
        write(section.createSection("values"), values, depth - 1, added);//write the values to the list
    }

    /**
     * Checks if a field is seralizable
     *
     * @param field The field to check
     * @return If this field is meant to be written to the file
     */
    @Deprecated
    private static boolean canSerialize(Field field) {
        return field != null && !Modifier.isStatic(field.getModifiers()) && (field.getAnnotation(FieldDescription.class) == null || field.getAnnotation(FieldDescription.class).save());
    }

    /**
     * A parse method used for quick parsing a string to an object with no errors
     *
     * @param type   The type you are parsing to
     * @param string The string you are parsing
     * @return The object that this string is refering to
     */
    public static <P> P parse(Class<P> type, String string) {
        try {
            return parse(type, (Object) string);//try catch run the parse method
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * A method used for parsing an object into another object based on references
     *
     * @param type  The object type you desire
     * @param value The object you desire
     * @return The object being referenced
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public static <P> P parse(Class<P> type, Object value) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //System.out.println( "Class=" + type.getName() + ", Value="+value);
        if (value == null || (value instanceof String && value.toString().equals("null"))) {
            return null;//If the value is a string pointing to null return null
        }
        if (type.getClass().equals(value.getClass()) || type.isAssignableFrom(value.getClass())) {
            return type.cast(value);//If the value is already the desired type just return the value
        }
        if (type.isPrimitive() || (AbstractSerializer.WRAPPER_TYPES.contains(type) && type.getName().contains("java.lang"))) {//If it is a primitive class, grab its non-primitive wrapper class and run its parse<Type> method
            Class<?> nonPrimitive = type.isPrimitive() ? Class.forName(("java.lang." + (Character.toString(Character.toUpperCase(type.getSimpleName().charAt(0))) + type.getSimpleName().substring(1))).replace("Int", "Integer")) : type;
            return (P) (nonPrimitive.getMethod("parse" + nonPrimitive.getSimpleName().replace("Integer", "Int"), String.class).invoke(null, value));
        }
        if (type.isEnum()) {//If it is an enum then try it against all the enum's and see if it matches
            //System.out.println("IS ENUM");
            for (P p : (P[]) type.getMethod("values").invoke(null)) {
                if (p.toString().equalsIgnoreCase(value.toString())) {
                    return p;
                }
            }
        }
        Exception exception = null;
        List<Method> methods = EditorData.getParse(type);
        for (Class<?> child : type.getClasses()) {
            methods.addAll(EditorData.getParse(child));//get a list of all the
        }
        for (Method m : methods) {
            //System.out.println(m);
            try {
                Class<?> param = m.getParameterTypes()[0];
                value = parse(param, value);
                Object object = m.invoke(null, value);
                return (P) object;
            } catch (Exception e) {
                exception = e;
            }
        }
        if (exception != null) {
            exception.printStackTrace();
        }
        return null;
    }

    public static Set<Class<?>> getWrapperTypes() {
        return NormalObjectSerializer.WRAPPER_TYPES;
    }


    @Deprecated
    private static void writeObject(ConfigurationSection section, Object object, int depth, HashSet<Object> added) {
        for (Field field : getFields(object.getClass())) {
            if (canSerialize(field)) {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                //int modifier = field.getModifiers();
                //Field modifiersField = Field.class.getDeclaredField("modifiers");
                //modifiersField.setAccessible(true);
                //modifiersField.setInt(field, field.getModifiers() & ~Modifier.PUBLIC);
                try {
                    Object o = field.get(object);
                    section.set(field.getName(), null);
                    if (o == null) {
                        //section.set(field.getName(), null);
                    } else if (o.getClass().isPrimitive() || o instanceof String || getWrapperTypes().contains(o.getClass()) || o.getClass().isAssignableFrom(ItemStack.class) || o.getClass().isAssignableFrom(Location.class)) {
                        section.set(field.getName(), o);
                    } else if (o.getClass().isEnum()) {
                        section.set(field.getName(), o + "");
                    } else if (o instanceof PotionEffectType) {
                        section.set(field.getName(), ((PotionEffectType) o).getName());
                    } else if (false && UltraObject.class.isAssignableFrom(o.getClass())) {
                        section.set(field.getName() + ".class", o.getClass().getName());
                        section.set(field.getName() + ".ID", ((UltraObject) o).getId());
                    } else {
                        if (List.class.isAssignableFrom(o.getClass())) {
                            section.set(field.getName() + ".type", getListType(field).getName());
                        } else if (Object[].class.isAssignableFrom(o.getClass())) {
                            section.set(field.getName() + ".type", o.getClass().getComponentType().getName());
                        }
                        write(section.getConfigurationSection(field.getName()) == null ? section.createSection(field.getName()) : section.getConfigurationSection(field.getName()), o, depth - 1, added);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                field.setAccessible(accessible);
            }
            // modifiersField.setInt(field,  modifier);

        }
    }

    public static Class<?> getNonAbstractSuperclass(Class<?> type) {
        if (!Modifier.isAbstract(type.getModifiers())) {
            return type;
        }
        return getNonAbstractSuperclass(type.getSuperclass());
    }

    public static List<Class<?>> getClasses(Class<?> type) {
        List<Class<?>> list = new ArrayList<>();
        list.add(type);
        if (type.getSuperclass() != null) {
            list.addAll(getClasses(type.getSuperclass()));
        }
        return list;
    }

    /**
     * From the internet
     * @param theUrl The URL to read
     * @return The contents of that URL
     */
    public static String getUrlContents(String theUrl)
    {
        StringBuilder content = new StringBuilder();

        // many of these calls can throw exceptions, so i've just
        // wrapped them all in one try/catch statement.
        try
        {
            // create a url object
            URL url = new URL(theUrl);

            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();

            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;

            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null)
            {
                content.append(line + "\n");
            }
            bufferedReader.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        return content.toString();
    }


    public static void write(ConfigurationSection section, Object object) throws IllegalAccessException {
        //write(section, object, DEFAULT_SERIALIZATION_DEPTH, new HashSet<>());
        ObjectSerializer.write(section, object);
    }

    public static String toString(Object object) {
        String string = object.getClass().getSimpleName() + "[";
        for (Field field : getFields(object.getClass())) {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            try {
                string += field.getName() + "=" + field.get(object) + ", ";
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            field.setAccessible(accessible);
        }
        if (string.endsWith(", ")) {
            string = string.substring(0, string.length() - 2);
        }
        string += "]";
        return string;
    }

    /*
    public static long calculateSize(Object object) {
        return calculateSize(object, DEFAULT_SERIALIZATION_DEPTH, new ArrayList<>());
    }
    public static long calculateSize(Object object, int depth, List<Object> checked) {
        depth--;
        long size = 8;
        if (object == null || checked.contains(object)) {
            return size;
        }
        else if (depth <= 0) {
            ObjectUtils.debug(Level.WARNING, "Depth exception! You tried to find to deep an object depth!");
            return size;
        }
        if (object instanceof Boolean) {
            return 1;
        } else if (object instanceof Double) {
            return Double.SIZE/8;
        } else if (object instanceof Integer) {
            return Integer.SIZE/8;
        } else if (object instanceof Long) {
            return Long.SIZE/8;
        } else if (object instanceof Byte) {
            return Byte.SIZE/8;
        } else if (object instanceof Short) {
            return Short.SIZE/8;
        } else if (object instanceof Character) {
            return Character.SIZE/8;
        } else if (object instanceof String) {
            return ((String) object).getBytes().length + 8;
        } else if (object.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(object); i++) {
               // System.out.println(Array.get(object, i));
                size += calculateSize(Array.get(object, i), depth, checked);
            }
        } else if (object instanceof Collection) {
            for (Object o : (Collection) object) {
                size += calculateSize(o, depth, checked);
            }
        } else {
            for (Field field : getFields(object.getClass())) {
                boolean accessible = field.isAccessible();
                try {
                    field.setAccessible(true);
                    size += calculateSize(field.get(object), depth, checked);
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                if (field.isAccessible() != accessible) {
                    field.setAccessible(accessible);
                }
            }
        }
        return size;
    }*/

    public static Reflections getReflections() {
        if (reflections == null) {
            reflections = new Reflections();
        }
        return reflections;
    }

    public static boolean containsExact(HashSet list, Object object) {
        for (Object o : list) {
            if (o == object) {
                return true;
            }
        }
        return false;
    }

    public static void write(ConfigurationSection section, Object object, int depth, HashSet<Object> added) throws IllegalAccessException {
        if (object != null) {
            if (!containsExact(added, object)) {
                added.add(object);
                if (depth > 0) {
                    try {
                        if (object.getClass().isArray() || Modifier.isPublic(object.getClass().getConstructor().getModifiers())) {
                            section.set("class", object.getClass().getName());
                            if (object instanceof List || object.getClass().isArray() || object instanceof HashSet) {
                                writeList(section, object, depth - 1, added);
                            } else if (object instanceof Map) {
                                writeMap(section, object, depth - 1, added);
                            } else {
                                writeObject(section, object, depth - 1, added);
                            }
                        } else {
                            throw new NoSuchMethodException();
                        }
                    } catch (NoSuchMethodException e) {
                        ObjectUtils.debug(Level.WARNING, "Tried to serialize object with no empty constructor: " + object);
                    }
                } else {
                    ObjectUtils.debug(Level.WARNING, "Depth overflow on object: " + object);
                }
            } else {
                ObjectUtils.debug(Level.WARNING, "Attempted to recursively serialized already serialised object: " + object);
            }
        } else {
            ObjectUtils.debug(Level.WARNING, "Attempted to write null object!");
        }
    }

    public static String write(Object object) throws IllegalAccessException {
        YamlConfiguration config = new YamlConfiguration();
        write(config, object);
        return config.saveToString();
    }

    public static Object read(String serialized) {
        try {
            YamlConfiguration config = new YamlConfiguration();
            config.loadFromString(serialized);
            return read(config);
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static <T> T read(String serialized, Class<T> type) {
        return (T) read(serialized);
    }


    public static byte[] cipher(byte[] input, byte[] key, int mode) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            SecretKeySpec aesKey = new SecretKeySpec(key, "AES");
            cipher.init(mode, aesKey);
            return cipher.doFinal(input);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] generateKey() {
        KeyGenerator gen = null;
        try {
            gen = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        gen.init(128);
        SecretKey aesKey = gen.generateKey();
        return aesKey.getEncoded();
    }

    public static List<Field> getFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Field field : type.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                fields.add(field);
            }
        }
        if (type.getSuperclass() != null) {
            fields.addAll(getFields(type.getSuperclass()));
        }
        return fields;
    }

    public static Class<?> getListType(Field stringListField) {
        try {
            ParameterizedType stringListType = (ParameterizedType) stringListField.getGenericType();
            return (Class<?>) stringListType.getActualTypeArguments()[0];
        } catch (Exception e) {
            return Object.class;
        }
    }


    public static Field getField(Class<?> type, String name) throws NoSuchFieldException {
        //type.getField("");
        for (Field field : getFields(type)) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        throw new NoSuchFieldException();
    }

    public static TextComponent toTextComponent(String s) {
        s = s.replace("§l", "&l");
        s = ChatColor.RESET + s;
        String[] strings = s.split("§");
        TextComponent component = new TextComponent();
        for (int i = 0; i < strings.length; i++) {
            if (strings[i].length() > 1) {
                TextComponent add = new TextComponent(strings[i].substring(1).replace("&l", "§l"));
                add.setColor(net.md_5.bungee.api.ChatColor.getByChar(strings[i].charAt(0)));
                component.addExtra(add);
            } else if (i == 0) {
                component.addExtra(strings[i]);
            }
        }
        return component;
    }

    public static int countInStacktrace(String element) {
        int count = 0;
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (StackTraceElement elm : elements) {
            if (element.equals(elm.getClassName() + "." + elm.getMethodName())) {
                count++;
            }
        }
        return count;
    }

    public static void debug(Level level, String message) {
        try {
            UltraLib.getInstance().getLogger().log(level, message);
        } catch (Exception e) {
            System.out.println("[UltraLib] " + message);
        }
    }


    public static Object clone(Object object) {
        try {
            return read(write(object));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
