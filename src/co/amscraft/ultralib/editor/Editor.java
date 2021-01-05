package co.amscraft.ultralib.editor;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.editor.editors.*;
import co.amscraft.ultralib.player.PlayerData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectTypeWrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Izzy on 2017-10-01.
 */
public class Editor extends PlayerData {
    /**
     * The priorities with which the editor interfaces are asked to edit an object
     */
    private static final Class<? extends AbstractEditor>[] PRIORITY = new Class[]{NewObjectEditor.class, CollectionEditor.class, MapEditor.class, ObjectEditor.class};
    private static final DisplayValue HELP = new DisplayValue("Help", "Enter 'help' for more commands", "help", null, "Command");
    private static final DisplayValue BACK = new DisplayValue("Back", "Enter 'back' to finish editing this object", "back", null, "Command");
    private static Map<Class<? extends AbstractEditor>, AbstractEditor> map = new HashMap<>();
    /**
     * A field that stores the objects currently being edited. When you edit a new object it is added to the end of the list
     * Think of this as the StackTrace in programaming. When you edit the field of an object you can go back later
     * beacuse the edited field is higher on the stacktrace then the parent object
     */
    private List<Object> editing = new ArrayList<>();
    private boolean resend;

    /**
     * @param type the Editor Interface you want to get
     * @param <T>  The type of the editor
     * @return The editor you requested
     */
    private static <T extends AbstractEditor> T getSubEditor(Class<T> type) {
        if (!map.containsKey(type)) {
            try {
                map.put(type, type.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return (T) map.get(type);
    }

    public boolean isResending() {
        return resend;
    }

    public void setResend(boolean resend) {
        this.resend = resend;
    }

    /**
     * A method that lets you get the editor that is to be used for any given object
     *
     * @param object The object you are requesting for
     * @return The editor for that object
     */
    private AbstractEditor getSubEditor(Object object) {
        for (Class<? extends AbstractEditor> editor : PRIORITY) {
            if (getSubEditor(editor).useEditor(object)) {
                return getSubEditor(editor);
            }
        }
        return null;
    }

    /**
     * A method for getting which editor protocal to use based on the current object it is editing
     *
     * @return
     */
    public AbstractEditor getSubEditor() {
        return this.getSubEditor(this.getObject());
    }

    /**
     * The method run when the player uses the back command
     * It will remove and save if neccicary the current object  and go back a level
     * If there is no higher level to go back to it will exit the editor
     */
    public void back() {
        if (this.getObject() instanceof UltraObject) {
            ((UltraObject) this.getObject()).save();
        }
        this.editing.remove(this.editing.size() - 1);
        while (!this.editing.isEmpty() && this.getObject() instanceof Class<?>) {
            this.editing.remove(this.editing.size() - 1);
        }
        if (this.editing.isEmpty() || (this.getObject() instanceof Class<?> && this.editing.size() == 1)) {
            this.getPlayer().getBukkit().sendMessage(this.getSettings().getSuccess() + "You have exited the editor!");
            this.getPlayer().removeData(Editor.class);
        }
    }

    /**
     * The run method that is called with a command and an argument
     * in order to process the command and effect the necciccary objects
     *
     * @param command The command
     * @param value   The argument
     */
    public void run(String command, String value) {
        if (isLegalObject(this.getObject())) {
            this.setResend(true);//resets the resend so that unless canclelled the player will have their screen messages refreshed
            switch (command.toLowerCase()) {
                case "delete"://If the command is delete
                    Object object = this.getObject();
                    back();
                    try {
                        object.getClass().getMethod("delete").invoke(object);
                    } catch (Exception e) {

                    }
                    break;
                case "back":
                    back();
                    break;
                case "help":
                    sendHelp();
                    this.setResend(false);
                    break;
                default:
                    this.getSubEditor().run(this, command, value);
                    break;
            }
            if (this.isResending() && this.getPlayer().hasData(Editor.class)) {
                this.resend();
            } else {
                this.setResend(true);
            }
        } else {
            this.sendMessage("You are not allowed to edit " + this.getObject().getClass().getSimpleName() + ": " + this.getObject());
            this.setResend(true);
            back();
        }
    }

    /**
     * The method to send the help screen to the player
     */
    public void sendHelp() {
        Player bukkit = this.getPlayer().getBukkit();
        EditorSettings s = this.getSettings();
        bukkit.sendMessage(s.getTitle().replace("{CLASS}", this.getObject().getClass().getSimpleName()));
        for (String command : getSubEditor().getCommands()) {
            bukkit.sendMessage(s.getHelp() + command);
        }
        try {
            this.getObject().getClass().getMethod("delete");
            bukkit.sendMessage(s.getHelp() + "Enter 'delete' to delete this object!");
        } catch (NoSuchMethodException e) {

        }
        bukkit.sendMessage(s.getHelp() + "Enter 'help' for more commands");
        bukkit.sendMessage(s.getHelp() + "Enter 'back' to finish editing this object");
    }

    /**
     * The method that sends the player their current options
     */
    public void resend() {
        Player bukkit = this.getPlayer().getBukkit();
        EditorSettings s = this.getSettings();
        bukkit.sendMessage(s.getTitle().replace("{CLASS}", this.getObject().getClass().getSimpleName()));
        /*try {
            bukkit.sendMessage(s.getVariable() + "Size" + s.getColon() + ": " + s.getValue() + SizeOf.deepSizeOf(this.getObject()));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        List<DisplayValue> values = this.getSubEditor().getDisplayValues(this.getObject());
        for (DisplayValue value : values) {
            bukkit.spigot().sendMessage(value.toTextComponent(s));
        }
        bukkit.spigot().sendMessage(HELP.toTextComponent(s));
        bukkit.spigot().sendMessage(BACK.toTextComponent(s));
    }

    /**
     * A method to check if you are allowed to edit an object, some objects such as primitive objects and java objects are not supposed to be edited by players for security reasons
     *
     * @param object The object checking for
     * @return If it is legal
     */
    public boolean isLegalObject(Object object) {
        return getSubEditor(object) != getSubEditor(Object.class) || (!object.getClass().getPackage().getName().startsWith("java") && !object.getClass().isPrimitive() && !object.getClass().getPackage().getName().startsWith("org.bukkit") && !object.getClass().getPackage().getName().startsWith("net.minecraft"));
    }

    /**
     * A method used to send the player an error message
     * This also stops the player from having their current options resent after the message
     *
     * @param message The error message to send
     */
    public void sendMessage(String message) {
        this.setResend(false);
        this.getPlayer().getBukkit().sendMessage(this.getSettings().getError() + message);
    }

    /**
     * A method to access the player's preferences for colors
     *
     * @return The player's preferences for colors
     */
    public EditorSettings getSettings() {
        return this.getPlayer().getData(EditorSettings.class);
    }

    public List<Object> getEditing() {
        return editing;
    }

    /**
     * Get the object that is at the top of the editor StackTrace
     *
     * @return The object at the top of the editor StackTrace
     */
    public Object getObject() {
        if (this.getEditing().isEmpty()) {
            return null;
        }
        return this.getEditing().get(this.getEditing().size() - 1);
    }
/*
    public static void main(String[] args) {
        String command = "this is a 'command' command!";
        System.out.println(command.substring(command.indexOf("'") + 1, command.lastIndexOf("'")));
    }*/


    /**
     * A method for getting the options to suggust to a player based on what they have already entered
     *
     * @param key What they have already entered
     * @return A list of strings to suggust to the user
     */
    public List<String> getAutoComplete(String key) {
        List<String> list = this.getSubEditor().getAutoComplete(this);
        try {
            for (String command : getSubEditor().getCommands()) {
                if (command.indexOf("'") != command.lastIndexOf("'")) {
                    list.add(command.substring(command.indexOf("'") + 1, command.lastIndexOf("'")).replace(" ", ""));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        list.add("back");
        try {
            this.getObject().getClass().getMethod("delete");
            list.add("delete");
        } catch (NoSuchMethodException e) {

        }
        list.add("help");
        if (!key.equals("")) {
            for (String string : list.toArray(new String[list.size()])) {
                if (!string.toLowerCase().startsWith(key.toLowerCase())) {
                    list.remove(string);
                }
            }
        }
        return list;
    }

    /**
     * Overrides the save method from the PlayerData so that it does not save to a file
     */
    @Override
    public void save(FileConfiguration config) {
        // config.set();
    }
}