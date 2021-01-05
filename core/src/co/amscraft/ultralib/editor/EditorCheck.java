package co.amscraft.ultralib.editor;

import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by Izzy on 2017-10-15.
 */
public abstract class EditorCheck<T> {
    private static final HashMap<Field, EditorCheck> map = new HashMap<>();
    private final String FAIL_MESSAGE;

    public EditorCheck(String failMessage) {
        this.FAIL_MESSAGE = failMessage;
    }


    public static boolean check(Field field, Object object, CommandSender sender) {
        return !map.containsKey(field) || (map.get(field).check(object, sender));
    }

    public static void register(Field field, EditorCheck check) {
        map.put(field, check);
    }

    public static EditorCheck getCheck(Field field) {
        return map.get(field);
    }

    public String getFailMessage() {
        return this.FAIL_MESSAGE;
    }


    public abstract boolean check(T object, CommandSender sender);


}
