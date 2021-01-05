package co.amscraft.ultralib.editor;

import co.amscraft.ultralib.player.PlayerData;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Created by Izzy on 2017-10-03.
 */
public class EditorSettings extends PlayerData {
    @FieldDescription(help = "The title for any editor menu")
    public String title = "&6&lEditing {CLASS}";
    @FieldDescription(help = "A color setting for the editor and commands", unit = "color")
    private String variable = "&9";
    @FieldDescription(help = "A color setting for the editor and commands", unit = "color")
    private String colon = "&3";
    @FieldDescription(help = "A color setting for the editor and commands", unit = "color")
    private String value = "&7";
    @FieldDescription(help = "A color setting for the editor and commands", unit = "color")
    private String helpColor = "&7";
    @FieldDescription(help = "A color setting for the editor and commands", unit = "color")
    private String error = "&c";
    @FieldDescription(help = "A color setting for the editor and commands", unit = "color")
    private String success = "&a";
    //private Mode mode = Mode.CHAT;

    public static EditorSettings getSettings(CommandSender sender) {
        return UltraPlayer.getPlayer(sender).getData(EditorSettings.class);
    }

    @FieldDescription(show = false)


    public String getTitle() {
        return ChatColor.translateAlternateColorCodes('&', this.title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVariable() {
        return ChatColor.translateAlternateColorCodes('&', this.variable);
    }

    public void setVariable(String variable) {
        this.variable = variable;
    }

    public String getColon() {
        return ChatColor.translateAlternateColorCodes('&', this.colon);
    }

    public void setColon(String colon) {
        this.colon = colon;
    }

    public String getValue() {
        return ChatColor.translateAlternateColorCodes('&', this.value);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getHelp() {
        return ChatColor.translateAlternateColorCodes('&', this.helpColor);
    }

    public String getError() {
        return ChatColor.translateAlternateColorCodes('&', this.error);
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSuccess() {
        return ChatColor.translateAlternateColorCodes('&', this.success);
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public void setHelpColor(String helpColor) {
        this.helpColor = helpColor;
    }


}
