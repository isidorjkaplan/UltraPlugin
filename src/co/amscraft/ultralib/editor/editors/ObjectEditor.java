package co.amscraft.ultralib.editor.editors;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.editor.*;
import co.amscraft.ultralib.utils.ObjectUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjectEditor extends AbstractEditor {
    public static void setField(Field field, Object editing, Object value) {
        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        try {
            field.set(editing, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        field.setAccessible(accessible);
    }

    @Override
    public boolean useEditor(Object type) {
        return true;
    }

    public void sendInfo(Editor editor) {
        for (Field field : ObjectUtils.getFields(editor.getObject().getClass())) {
            if (field != null && field.getAnnotation(FieldDescription.class) == null || field.getAnnotation(FieldDescription.class).show()) {
                EditorSettings s = editor.getSettings();
                editor.sendMessage(s.getColon() + "(" + s.getVariable() + field.getType().getSimpleName() + s.getColon() + ") " + s.getValue() + field.getName() + s.getColon() + ": " + s.getHelp() + (field.getAnnotation(FieldDescription.class) != null ? field.getAnnotation(FieldDescription.class).help() : "No description available"));
            }
        }
    }

    @Override
    public void run(Editor editor, String command, String value) {
        if (command.equalsIgnoreCase("info")) {
            sendInfo(editor);
        } else {
            Object editing = editor.getObject();
            Field field = null;
            try {
                field = ObjectUtils.getField(editing.getClass(), command);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            if (field != null && (field.getAnnotation(FieldDescription.class) == null || field.getAnnotation(FieldDescription.class).show())) {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                try {
                    Object object = null;
                    if (value == null) {
                        if (field.getType().isEnum()) {
                            EditorSettings s = editor.getSettings();
                            editor.sendMessage(s.getVariable() + "Values" + s.getColon() + ": " + s.getValue() + Arrays.asList((Object[]) field.getType().getMethod("values").invoke(null)));
                        } else {
                            object = field.get(editing);
                            if (object == null) {
                                object = field.getType();
                            }
                            editor.getEditing().add(object);
                        }
                    } else {
                        if (value.equalsIgnoreCase("new")) {
                            editor.getEditing().add(new NewObject(field, editing));
                        } else if (value.equalsIgnoreCase("reset") && !(editor.getObject() instanceof UltraObject)) {
                            field.set(editor.getObject(), field.get(editor.getObject().getClass().newInstance()));
                        } else {
                            object = ObjectUtils.parse(field.getType(), value);
                            //System.out.println(command + ": " + value + " = " + object);
                            EditorCheck check = EditorCheck.getCheck(field);
                            if (check != null && !check.check(object, editor.getPlayer().getBukkit())) {
                                editor.sendMessage(check.getFailMessage());
                                object = null;
                            }
                            if (object != null) {
                                setField(field, editing, object);
                            } else if (field.getType().isEnum()) {
                                EditorSettings s = editor.getSettings();
                                editor.sendMessage(s.getVariable() + "Values" + s.getColon() + ": " + s.getValue() + Arrays.asList((Object[]) field.getType().getMethod("values").invoke(null)));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                field.setAccessible(accessible);
            } else {
                editor.sendMessage("You must enter a valid field!");
            }

        }
    }

    @Override
    public List<DisplayValue> getDisplayValues(Object object) {
        List<DisplayValue> list = new ArrayList<>();
        for (Field field : ObjectUtils.getFields(object.getClass())) {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            if (field.getAnnotation(FieldDescription.class) == null || field.getAnnotation(FieldDescription.class).show()) {
                try {
                    Object o = field.get(object);
                    String display = o + "";
                    if (o != null && display.equals(o.getClass().getName() + "@" + Integer.toHexString(o.hashCode()))) {
                        display = ObjectUtils.toString(o);
                    }
                    String help = null;
                    if (field.getAnnotation(FieldDescription.class) != null) {
                        display += " " + field.getAnnotation(FieldDescription.class).unit();
                        help = field.getAnnotation(FieldDescription.class).help();
                    }
                    String command = null;
                    if (!field.getType().isPrimitive() && !field.getType().equals(String.class)) {
                        command = field.getName();
                    }
                    list.add(new DisplayValue(field.getName(), display, command, help, field.getType().getSimpleName()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            field.setAccessible(accessible);
        }
        return list;
    }

    /*@Override
    public Map<String, Object> getDisplayValues(Object object) {
        Map<String, Object> map = new HashMap<>();
        for (Field field: ObjectUtils.getFields(object.getClass())) {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            if (field.getAnnotation(FieldDescription.class) == null || field.getAnnotation(FieldDescription.class).show()) {
                try {
                    Object o =  field.get(object);
                    String display = o + "";
                    if (o != null && display.equals(o.getClass().getName() + "@" + Integer.toHexString(o.hashCode()))) {
                        display = ObjectUtils.toString(field.get(object));
                    }
                    if (field.getAnnotation(FieldDescription.class) != null) {
                        display += " " + field.getAnnotation(FieldDescription.class).unit();
                    }
                    map.put(field.getName(), display);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            field.setAccessible(accessible);
        }
        return map;
    }*/

    @Override
    public List<String> getAutoComplete(Editor editor) {
        List<String> list = new ArrayList<>();
        for (DisplayValue value : getDisplayValues(editor.getObject())) {
            list.add(value.getKey());
        }
        return list;
    }

    @Override
    public String[] getCommands() {
        return new String[]{"Enter '<field>: new' to create a new value for that field", "Enter 'info' for more info on variables!", "Enter '<field>: reset' to reset a field to it's default value"};
    }

    public static class NewObject extends NewObjectEditor.NewObject {
        private Field field;

        public NewObject(Field field, Object editing) {
            super(field.getType(), editing);
            this.field = field;
        }

        public Field getField() {
            return field;
        }

        @Override
        public void implement(Object object) {
            setField(this.getField(), this.getParent(), object);
        }
    }
}
