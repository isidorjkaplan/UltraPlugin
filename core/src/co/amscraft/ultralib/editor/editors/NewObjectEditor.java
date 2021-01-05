package co.amscraft.ultralib.editor.editors;

import co.amscraft.ultralib.editor.DisplayValue;
import co.amscraft.ultralib.editor.Editor;
import co.amscraft.ultralib.editor.EditorData;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultralib.utils.ObjectUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class NewObjectEditor extends AbstractEditor {
    @Override
    public boolean useEditor(Object type) {
        return type instanceof NewObject;
    }

    @Override
    public void run(Editor editor, String command, String value) {
        NewObject newObject = (NewObject) editor.getObject();
        //System.out.println(EditorData.getConstructors(newObject.getType()));
        for (Constructor c : EditorData.getConstructors(newObject.getType())) {
            try {
                Class<?> clazz = Class.forName(c.getAnnotatedReturnType().getType().getTypeName());
                if (clazz.getSimpleName().equalsIgnoreCase(command)) {
                    if (value != null) {
                        boolean skip = false;
                        Object[] args = new Object[value.split(" ").length];
                        if (value != null) {
                            for (int i = 0; i < args.length; i++) {
                                try {
                                    args[i] = ObjectUtils.parse(c.getParameterTypes()[i], value.split(" ")[i]);
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    skip = true;
                                    break;
                                }
                            }
                        }
                        if (!skip) {
                            Object object = c.newInstance(args);
                            newObject.implement(object);
                            editor.getEditing().remove(newObject);
                            editor.getEditing().add(object);
                            return;
                        }
                    } else if (value == null && c.getParameterTypes().length == 0) {
                        Object object = c.newInstance();
                        newObject.implement(object);
                        editor.getEditing().remove(newObject);
                        editor.getEditing().add(object);
                        return;
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        editor.sendMessage("The constructor you mentioned does not exist!");
    }

    /* @Override
     public Map<String, Object> getDisplayValues(Object object) {
         Map<String, Object> map = new HashMap<>();
         for (Constructor c : EditorData.getConstructors(((NewObject)object).getType())) {
             String message = "";
             try {
                 message += Class.forName(c.getAnnotatedReturnType().getType().getTypeName()).getSimpleName();
             } catch (Exception e) {
                 e.printStackTrace();
             }
             while (map.containsKey(message)) {
                 message += ChatColor.WHITE;
             }
             String params = "";
             for (Parameter p : c.getParameters()) {
                 params += "{" + p.getType() + "}, ";
             }
             map.put(message, params);
         }
         return map;
     }*/
    @Override
    public List<DisplayValue> getDisplayValues(Object object) {
        List<DisplayValue> values = new ArrayList<>();
        for (Constructor c : EditorData.getConstructors(((NewObject) object).getType())) {
            String message = "";
            try {
                message += Class.forName(c.getAnnotatedReturnType().getType().getTypeName()).getSimpleName();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String params = "";
            for (Parameter p : c.getParameters()) {
                params += "{" + p.getType() + "}, ";
            }
            values.add(new DisplayValue(message, params, message, "Click to create this type of object", message));
        }
        return values;
    }

    @Override
    public List<String> getAutoComplete(Editor editor) {
        List<String> list = new ArrayList<>();
        for (Constructor c : EditorData.getConstructors(((NewObject) editor.getObject()).getType())) {
            String s = null;
            try {
                s = Class.forName(c.getAnnotatedReturnType().getType().getTypeName()).getSimpleName();
                if (!list.contains(s)) {
                    list.add(s);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        return list;
    }

    @Override
    public String[] getCommands() {
        return new String[0];
    }

    /**
     * An object that stores the parent object and the type of the new object that is being made
     * while the user selects the object constructor they want
     */
    public static abstract class NewObject {
        private Class<?> type;
        private Object parent;

        /**
         * The new object constructor
         *
         * @param type   The object type that is being creaated
         * @param parent The parent object that the new object is inside
         */
        public NewObject(Class<?> type, Object parent) {
            this.type = type;
            this.parent = parent;
        }

        /**
         * The abstract method used to actually place the new object in the correct place
         *
         * @param object The object that has been created
         */
        public abstract void implement(Object object);

        public Object getParent() {
            return parent;
        }

        public Class<?> getType() {
            return type;
        }

        public Editor getEditor() {
            for (UltraPlayer player : UltraPlayer.getPlayers()) {
                if (player.hasData(Editor.class) && player.getData(Editor.class).getObject() == this) {
                    return player.getData(Editor.class);
                }
            }
            return null;
        }
    }
}
