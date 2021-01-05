package co.amscraft.ultralib.modules;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorData;
import co.amscraft.ultralib.tic.GameTic;
import co.amscraft.ultralib.utils.ObjectUtils;
import co.amscraft.ultralib.utils.savevar.SaveVariables;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Level;

/**
 * Created by Izzy on 2017-10-09.
 * This class represents a Module that uses the UltraLib plugin
 */
public abstract class Module {
    //A static list of all the currently loaded Module's in the order they were loaded
    private static Set<Module> modules = new HashSet<>();
    /**
     * The File of the Jar that this Module was loaded  from
     */
    private File moduleJar;
    /**
     * A set of all the loaded classes that this Module has
     */
    private Set<Class<?>> classes = new HashSet<>();

    /**
     * Weather or not this module is enabled
     */
    private boolean enabled = false;

    /**
     * A list of all the Module's loaded by UltraLib
     *
     * @return All the currently loaded modules
     */
    public static Set<Module> getModules() {
        return Collections.unmodifiableSet(modules);
    }

    /**
     * A static function to get all the loaded module names
     *
     * @return The names  of the modules
     */
    public static String[] getModuleNames() {
        Set<Module> list = getModules();
        String[] modules = new String[list.size()];
        int i = 0;
        for (Module module : getModules()) {
            modules[i] = module.getName();
            i++;
        }
        return modules;
    }

    /**
     * A method to get the module that a given jar file represents
     *
     * @param file The jar file of the module
     * @return The object that represents the Module
     */
    public static Module getModule(File file) {
        for (Module module : modules) {
            if (module.moduleJar.equals(file)) {
                return module;
            }
        }
        return loadModule(file);
    }

    /**
     * Get a module by it's name
     *
     * @param name The name of the module
     * @return The Module Object
     */
    public static Module getModule(String name) {
        for (Module module : Module.getModules()) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    /**
     * A static method to get a Module by type
     *
     * @param type The Module Type
     * @param <T>  The Module Type
     * @return The Module Object
     */
    public static <T extends Module> T getModule(Class<T> type) {
        return (T) getModule(type.getSimpleName());
    }


    /**
     * A method to add a jar file to the module, loading all the classes it contains
     *
     * @param loader The Class Loader
     * @param url    The URL to load
     * @param module The Module to load it to
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    protected static void addURL(URLClassLoader loader, URL url, Module module) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        addURL(loader, url, module, true);
    }

    /**
     * A method to add a jar file to a Module
     *
     * @param loader      The class loader
     * @param url         The URL
     * @param module      The Module
     * @param loadClasses If it should enable the classes after adding them
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void addURL(URLClassLoader loader, URL url, Module module, boolean loadClasses) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        java.lang.reflect.Method method = java.net.URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{java.net.URL.class});
        method.setAccessible(true); /*promote the method to public access*/
        method.invoke(loader, new Object[]{url});
        if (loadClasses) {
            for (String string : Module.getClasseNames(url.getPath())) {
                try {
                    if (Class.forName(string, false, loader) == null) {
                        break;
                    }
                    loader.loadClass(string);
                    if (module != null) {
                        module.classes.add(Class.forName(string));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * A method to load a module from a Jar file
     *
     * @param file The jar file to read
     * @return The Module that it loads
     */
    public static Module loadModule(File file) {
        UltraLib.getInstance().getLogger().log(Level.INFO, "Attempting to load jar " + file.getName());
        try {
            Class<? extends Module> main = null;
            ClassLoader loader = UltraLib.getInstance().getLoader();
            addURL((URLClassLoader) loader, file.toURI().toURL(), null);
            Set<Class<?>> classes = new HashSet<>();
            for (String type : getClasseNames(file.getPath())) {
                Class loadedClass = Class.forName(type);//classLoader.loadClass(type);
                classes.add(loadedClass);
                if (Module.class.isAssignableFrom(loadedClass)) {
                    main = (Class<? extends Module>) loadedClass;
                }
            }
            //t classLoader.close();
            try {
                if (main != null) {
                    Module module = main.newInstance();
                    module.moduleJar = file;
                    modules.add(module);
                    module.classes = classes;
                    if (module.isDependanciesLoaded()) {
                        module.enable();
                    }
                    UltraLib.getInstance().getLogger().log(Level.INFO, "Successfully loaded module: " + main.getSimpleName() + " from jar " + file.getName());
                } else {
                    UltraLib.getInstance().getLogger().log(Level.WARNING, "Module jar " + file.getName() + " is missing main class!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                UltraLib.getInstance().getLogger().log(Level.WARNING, "Failed to load module jar " + file.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * A method to get a list of all the classes in the Module in the order they were loaded
     *
     * @param jarName The jar file path
     * @return The list of all the classes
     */
    public static List<String> getClasseNames(String jarName) {
        ArrayList<String> classes = new ArrayList<String>();
        try {
            JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
            JarEntry jarEntry;

            while (true) {
                jarEntry = jarFile.getNextJarEntry();
                if (jarEntry == null) {
                    break;
                }
                if (jarEntry.getName().endsWith(".class")) {
                    String string = jarEntry.getName().replaceAll("/", "\\.");
                    classes.add(string.substring(0, string.length() - 6));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return classes;
    }

    /**
     * A method to add all the Jar's inside a folder to a Class Loader
     *
     * @param loader The ClassLoader
     * @param folder The folder
     * @param load   If it should enable the classes when they are added
     */
    public static void addURLFolder(URLClassLoader loader, File folder, boolean load) {
        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(".jar")) {
                try {
                    addURL(loader, file.toURI().toURL(), null, load);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (file.isDirectory()) {
                addURLFolder(loader, file, load);
            }
        }

    }

    /**
     * A static method to load all the Module's in the UltraLib/modules folder
     */
    public static void loadModules() {
        File folder = new File(UltraLib.getInstance().getDataFolder() + "/modules");
        try {
            addURLFolder((URLClassLoader) UltraLib.getInstance().getLoader(), folder, false);
            //addURLFolder((URLClassLoader) UltraLib.getInstance().getLoader(), folder, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!folder.isFile()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".jar")) {
                        UltraLib.getInstance().getLogger().log(Level.INFO, "Detected module jar " + file.getName());
                        try {
                            loadModule(file);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        for (Module m : getModules()) {
            m.enableRecursively();
        }
    }

    /**
     * A function to get a list of all the enabled modules
     *
     * @return
     */
    public static ArrayList<Module> getEnabledModules() {
        ArrayList<Module> list = new ArrayList<>();
        for (Module m : Module.getModules()) {
            if (m.isEnabled()) {
                list.add(m);
            }
        }
        return list;
    }

    /**
     * A function to get the set of all the disabled modules
     *
     * @return The disabled modules
     */
    public static Set<Module> getDisabledModules() {
        Set<Module> list = Module.getModules();
        list.removeAll(Module.getEnabledModules());
        return list;
    }

    /**
     * A function to get a Module that loaded a given class
     *
     * @param type The class you loadded
     * @return The module that loaded that class
     */
    public static Module get(Class<?> type) {
        for (Module m : modules) {
            if (m.classes.contains(type)) {
                return m;
            }
        }
        return null;
    }

    /**
     * A function to get all the modules given a list of module names
     *
     * @param modules The names of the modules
     * @return The Module's that those names represent
     */
    public Set<Module> getModules(String... modules) {
        Set<Module> set = new HashSet<>();
        for (String s : modules) {
            Module m = Module.getModule(s);
            if (m != null) {
                set.add(m);
            }
        }
        return set;
    }

    /**
     * A function to recursivly load modules and their dependancies
     */
    public void enableRecursively() {
        this.enableRecursively(new HashSet<>());
    }

    /**
     * A function to r
     *
     * @param attempted
     */
    private void enableRecursively(Set<Module> attempted) {
        if (!this.isEnabled() && !attempted.contains(this)) {
            attempted.add(this);
            if (!this.isDependanciesLoaded()) {
                for (Module depend : getModules(this.getModuleDependancies())) {
                    depend.enableRecursively(attempted);
                }
            }
            if (this.isDependanciesLoaded()) {
                this.enable();
            } else {
                ObjectUtils.debug(Level.WARNING, "Module " + this.getName() + " is missing dependencies!");
            }
        }
    }

    /**
     * A method to enable this module
     */
    public void enable() {
        for (String type : getClasseNames(this.getJar().getPath())) {
            try {
                Class loadedClass = Class.forName(type);//classLoader.loadClass(type);
                GameTic.register(loadedClass);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        try {
            this.onEnable();
            for (Class<?> type : this.getClasses()) {
                SaveVariables.addClass(type);
                if (!Modifier.isAbstract(type.getModifiers())) {
                    if (UltraObject.class.isAssignableFrom(type)) {
                        UltraObject.getList((Class<? extends UltraObject>) type);
                        EditorData.registerRoot(type, UltraObject.getList((Class<? extends UltraObject>) type));
                    } else if (UltraCommand.class.isAssignableFrom(type)) {
                        UltraCommand.register((Class<? extends UltraCommand>) type);
                    }
                    if (Listener.class.isAssignableFrom(type) && !Modifier.isAbstract(type.getModifiers())) {
                        try {
                            UltraLib.getInstance().getServer().getPluginManager().registerEvents((Listener) type.newInstance(), UltraLib.getInstance());
                        } catch (Exception e) {
                            ObjectUtils.debug(Level.WARNING, "Error while enabling listener class " + type.getSimpleName() + ": " + e.getClass().getSimpleName());
                        }
                    }
                }
            }
            this.enabled = true;
            ObjectUtils.debug(Level.INFO, "Successfully enabled module: " + this.getName());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * A method that checks if a module is enabled
     *
     * @return If that module is enabled
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * A method to check if all the dependancies are loaaded
     *
     * @return If all the dependancies are loaded
     */
    private boolean isDependanciesLoaded() {
        for (String string : getModuleDependancies()) {
            Module m = Module.getModule(string);
            if (m == null || !m.isEnabled()) {
                return false;
            }
        }
        return true;
    }

    /**
     * A function to get all the commands that is loaded by a perticular module
     *
     * @return All the functions loaded by a Module
     */
    public List<UltraCommand> getCommands() {
        List<UltraCommand> commands = new ArrayList<>();
        for (UltraCommand command : UltraCommand.getCommands()) {
            if (this.getClasses().contains(command.getClass())) {
                commands.add(command);
            }
        }
        return commands;
    }

    /**
     * A function to get all the classes loaded by a Module
     *
     * @return All the classes loaded by a module
     */
    public List<Class> getClasses() {
        List<Class> list = new ArrayList<>();
        for (String name : getClasseNames(this.getJar().getPath())) {
            try {
                list.add(Class.forName(name));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }


    /**
     * A function to get the Jar file that a Module represents
     *
     * @return The jar file of this module
     */
    public File getJar() {
        return this.moduleJar;
    }

    /**
     * A function to get the name of the Module
     *
     * @return The name of the Module
     */
    public String getName() {
        return this.getClass().getSimpleName();
    }

    /**
     * A function to get the data folder of the Module
     *
     * @return The data folder of the Module
     */
    public String getDataFolder() {
        return UltraLib.getInstance().getDataFolder() + "/modules/" + this.getName();
    }

    /**
     * An abstract function to get the dependancies of a given module
     *
     * @return The dependancies of a given module
     */
    public abstract String[] getModuleDependancies();

    /**
     * A function that is called when the Module is enabled
     */
    public abstract void onEnable();

    /**
     * A function that is called for each module when the server is turning off
     */
    public abstract void onDisable();
}
