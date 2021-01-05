package co.amscraft.ultralib.editor;


import co.amscraft.ultralib.utils.ObjectUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * A class that couples the data needed for a line to show the player
 */
public class DisplayValue {
    private String key;
    private String command;
    private String help;
    private String type;
    private String value;


    /**
     * @param key     The key
     * @param value   The value
     * @param command The command to make the player run if they click
     * @param help    The help message to display the player if they hover over the message
     * @param type    The object datatype
     */
    public DisplayValue(String key, String value, String command, String help, String type) {
        this.key = key;
        this.type = type;
        this.command = command;
        this.value = value;
        this.help = help;
    }

    /**
     * Accessor method for command
     *
     * @return Command
     */
    public String getCommand() {
        return command;
    }

    /**
     * Accessor method for Key
     *
     * @return The key
     */
    public String getKey() {
        return key;
    }

    /**
     * Accessor method for Value
     *
     * @return The value
     */
    public String getValue() {
        return value;
    }

    /**
     * Accessor method for the object type
     *
     * @return The object type
     */
    public String getType() {
        return type;
    }

    /**
     * A method solidify the information to display into an actual message that can be sent to the player
     *
     * @param s The settings to use for colors and stuff
     * @return The Player-Senable TextComponent object
     */
    public TextComponent toTextComponent(EditorSettings s) {
        //bukkit.sendMessage(s.getVariable() + field + s.getColon() + ": " + s.getValue() + map.get(field));
        TextComponent c = ObjectUtils.toTextComponent(s.getVariable() + this.key + s.getColon() + ": " + s.getValue() + this.value);
        //s.getColon() + "(" + s.getVariable() + field.getType().getSimpleName() + s.getColon() + ") " + s.getValue() + field.getName() + s.getColon() + ": " + s.getHelp() + (field.getAnnotation(FieldDescription.class) != null?field.getAnnotation(FieldDescription.class).help():"No description available"
        String hover = s.getColon() + "(" + s.getVariable() + type + s.getColon() + ")\n";
        hover += s.getValue() + (help != null ? help : "No description available");
        c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{ObjectUtils.toTextComponent(hover)}));
        if (command != null) {
            c.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        }
        c.setColor(ChatColor.getByChar(s.getValue().charAt(1)));
        return c;
    }
}
