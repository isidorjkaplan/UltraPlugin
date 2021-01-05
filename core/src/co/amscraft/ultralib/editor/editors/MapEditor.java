package co.amscraft.ultralib.editor.editors;

import co.amscraft.ultralib.editor.DisplayValue;
import co.amscraft.ultralib.editor.Editor;
import co.amscraft.ultralib.utils.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapEditor extends AbstractEditor {
    public static Field getParentField(Editor editor) {
        Object parent = editor.getEditing().get(editor.getEditing().size() - 2);
        for (Field field : ObjectUtils.getFields(parent.getClass())) {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            try {
                if (field.get(parent) == (editor.getObject())) {
                    return field;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            field.setAccessible(accessible);
        }
        return null;

    }

    @Override
    public boolean useEditor(Object type) {
        return type instanceof Map;
    }

    private Class<?> getMapKey(Editor editor) {
        ParameterizedType pt = (ParameterizedType) getParentField(editor).getGenericType();
        try {
            return Class.forName(pt.getActualTypeArguments()[0].getTypeName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Class<?> getMapValue(Editor editor) {
        ParameterizedType pt = (ParameterizedType) getParentField(editor).getGenericType();
        try {
            return Class.forName(pt.getActualTypeArguments()[1].getTypeName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void run(Editor editor, String command, String value) {
        String keyString = value.split(" ")[0];
        String objectString = value.replaceFirst(keyString + " ", "");
        if (objectString.equals("")) {
            objectString = null;
        }
        Class<?> keyType = getMapKey(editor);
        Class<?> valueType = getMapValue(editor);
        Object key = ObjectUtils.parse(keyType, keyString);
        Object object = ObjectUtils.parse(valueType, objectString);
        Map map = (Map) editor.getObject();
        switch (command.toLowerCase()) {
            case "add":
                if (key != null && object != null) {
                    map.put(key, object);
                } else {
                    editor.sendMessage("You must enter a valid key and a valid value");
                }
                break;
            case "remove":
                if (key != null) {
                    map.remove(key);
                } else {
                    editor.sendMessage("You enter the key object to remove");
                }
                break;
            case "clear":
                map.clear();
                break;
        }
    }

    /*
    @Override
    public Map<String, Object> getDisplayValues(Object object) {
        Map<String, Object> map = new HashMap<>();
        for (Object key: ((Map<Object, Object>)object).keySet()) {
            map.put(ObjectUtils.toString(key),ObjectUtils.toString(((Map)object).get(key) ));
        }
        return map;
    }*/

    @Override
    public List<DisplayValue> getDisplayValues(Object object) {
        ArrayList<DisplayValue> list = new ArrayList<>();
        for (Object key : ((Map<Object, Object>) object).keySet()) {
            //list.pu(ObjectUtils.toString(key),ObjectUtils.toString(((Map)object).get(key) ));
            list.add(new DisplayValue(ObjectUtils.toString(key), ObjectUtils.toString(((Map) object).get(key)), null, null, "Map Item"));
        }
        return list;
    }

    @Override
    public List<String> getAutoComplete(Editor editor) {
        List<String> list = new ArrayList<>();
        for (Object key : ((Map) editor.getObject()).keySet()) {
            list.add(key + "");
        }
        return list;
    }

    @Override
    public String[] getCommands() {
        return new String[]{"add: <key> <value>", "remove: <key>", "clear"};
    }
}
