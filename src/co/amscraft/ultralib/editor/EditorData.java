package co.amscraft.ultralib.editor;

import co.amscraft.ultralib.utils.ObjectUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by Izzy on 2017-10-09.
 */
public class EditorData {
    public static HashMap<Class<?>, EditorData> data = new HashMap<>();
    private List<Method> parse = new ArrayList<>();
    private Collection<?> root = new ArrayList();
    //  private List<Constructor> constructors = new ArrayList<>();


    public EditorData(Class<?> type) {
        data.put(type, this);
    }

    public static Set<Class<?>> getRegisteredDatatypes() {
        return data.keySet();
    }

    public static boolean hasRoot(Class<?> type) {
        return !getData(type, false).root.isEmpty();
    }

    public static EditorData getData(Class<?> type, boolean register) {
        if (register && !data.containsKey(type)) {
            data.put(type, new EditorData(type));
        }
        return data.get(type);
    }

    public static void registerParse(Class<?> reference, List<Method> pointers) {
        for (Method method : pointers) {
            registerParse(reference, method);
        }
    }

    public static void registerParse(Class<?> reference, Method pointer) {
        List<Method> list = getData(reference, true).parse;
        if (!list.contains(pointer)) {
            list.add(pointer);
        }
    }

    public static void registerRoot(Class<?> pointer, Collection root) {
        getData(pointer, true).root = root;
    }

    public static <T> Collection<T> getRoot(Class<T> type) {
        return (Collection<T>) getData(type, false).root;
    }

    /*public static List<Constructor> getConstructors(Class<?> type) {
        List<Constructor> list = null;
        if (getData(type, false) == null) {
            list = new ArrayList();
            for (Constructor c : type.getConstructors()) {
                list.add(c);
            }
        } else {
            list = getData(type, true).constructors;
        }
        if (list.isEmpty()) {
            try {
                list.add(type.getConstructor());
            } catch (Exception e) {

            }
        }
        return list;
    }*/
    public static List<Constructor> getConstructors(Class<?> type) {

        List<Constructor> list = new ArrayList<>();
        if (type != null) {
            if (!Modifier.isAbstract(type.getModifiers())) {
                list.addAll(Arrays.asList(type.getConstructors()));
            }
            for (Class<?> clazz : ObjectUtils.getReflections().getSubTypesOf(type)) {
                if (!Modifier.isAbstract(clazz.getModifiers())) {
                    list.addAll(Arrays.asList(clazz.getConstructors()));
                }
            }
        }
        return list;
    }


    /*public static void registerConstructor(Class<?> type, Constructor constructor) {
        if (!getData(type, true).constructors.contains(constructor)) {
            getData(type, true).constructors.add(constructor);
        }
    }

    public static void registerConstructors(Class<?> type, Constructor<?>[] constructors) {
        for (Constructor c : constructors) {
            registerConstructor(type, c);
        }
    }*/

    public static List<Method> getParse(Class<?> type) {
        if (getData(type, false) == null) {
            return new ArrayList<>();
        }
        return getData(type, false).parse;
    }

}
