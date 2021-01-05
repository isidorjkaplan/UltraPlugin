package co.amscraft.ultralib.editor.editors;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.editor.DisplayValue;
import co.amscraft.ultralib.editor.Editor;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.utils.ObjectUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CollectionEditor extends AbstractEditor {

    @Override
    public boolean useEditor(Object type) {
        return type instanceof Collection;
    }

    @Override
    public void run(Editor editor, String command, String value) {
        Object parent = editor.getEditing().get(editor.getEditing().size() - 2);
        //Field CollectionField = null;
        Class<?> type = null;
        if (parent instanceof Class) {
            type = (Class) parent;
        } else {
            for (Field field : ObjectUtils.getFields(parent.getClass())) {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                try {
                    if (field.get(parent) == (editor.getObject())) {
                        type = ObjectUtils.getListType(field);
                        break;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                field.setAccessible(accessible);
            }
        }
        switch (command.toLowerCase()) {
            case "new": {
                editor.getEditing().add(new NewCollectionItem((Collection) editor.getObject(), type));
            }
            break;
            case "clear": {
                ((Collection) editor.getObject()).clear();
            }
            break;
            case "options": {
                if (type.isEnum()) {
                    EditorSettings s = editor.getSettings();
                    try {
                        editor.sendMessage(s.getVariable() + "Values" + s.getColon() + ": " + s.getValue() + Arrays.asList((Object[]) type.getMethod("values").invoke(null)));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                } else {
                    editor.sendMessage("Your not editing an enum Collection");
                }
            }
            break;
            default: {
                Object object = null;
                for (String s : new String[]{command, value}) {
                    if (s != null) {
                        object = ObjectUtils.parse(type, s);
                        if (object == null) {
                            try {
                                int index = Integer.parseInt(s);
                                if (((Collection) editor.getObject()).size() > index) {
                                    object = ((Collection) editor.getObject()).toArray()[index];
                                }
                            } catch (NumberFormatException e) {

                            }
                        }
                        if (object == null) {
                            //System.out.println(command + ": " + value);
                            for (Object o : (Collection) editor.getObject()) {
                                if (o.toString().equalsIgnoreCase(s)) {
                                    object = o;
                                    break;
                                }
                            }
                            for (Object o : (Collection) editor.getObject()) {
                                if (o.toString().toLowerCase().contains(s.toLowerCase())) {
                                    object = o;
                                    break;
                                }
                            }
                        }

                    }
                }
                if (object != null) {
                    switch (command.toLowerCase()) {
                        case "remove": {
                            Object obj = ((Collection) editor.getObject()).remove(object);
                            try {
                                if (obj instanceof UltraObject && UltraObject.getList((Class<? extends UltraObject>)obj.getClass()).equals(editor.getObject()))
                                {
                                    ((UltraObject)obj).delete();
                                }
                            } catch (Exception e) {

                            }
                        }
                        break;
                        case "add": {
                            if (!((Collection) editor.getObject()).contains(object)) {
                                ((Collection) editor.getObject()).add(object);
                            } else {
                                editor.sendMessage("The Collection already has that object!");
                            }
                        }
                        break;
                        default: {
                            editor.getEditing().add(object);

                        }
                        break;
                    }
                } else {
                    editor.sendMessage("The object you entered does not exist!");
                }
            }
        }
    }

    @Override
    public List<DisplayValue> getDisplayValues(Object object) {
        List<DisplayValue> value = new ArrayList<>();
        Collection Collection = (Collection) object;
        value.add(new DisplayValue("Items", Collection.toString(), null, null, "Collection"));
        return value;
    }



    @Override
    public List<String> getAutoComplete(Editor editor) {
        List<String> Collection = new ArrayList<>();
        for (Object object : (Collection) editor.getObject()) {
            Collection.add(object + "");
        }
        return Collection;
    }

    @Override
    public String[] getCommands() {
        return new String[]{"Say 'new' to create a new item for the Collection",
                "Say 'clear' to empty the Collection", "Say 'remove: <item>' to remove an item from the Collection", "Say 'add: <item>' to add an item to the Collection", "Say 'options' to Collection options if static"};
    }

    public static class NewCollectionItem extends NewObjectEditor.NewObject {

        public NewCollectionItem(Collection Collection, Class<?> type) {
            super(type, Collection);
            //System.out.println(type);
        }

        public Collection getCollection() {
            return (Collection) this.getParent();
        }

        @Override
        public void implement(Object object) {
            if (!this.getCollection().contains(object)) {
                this.getCollection().add(object);
            }
        }
    }
}
