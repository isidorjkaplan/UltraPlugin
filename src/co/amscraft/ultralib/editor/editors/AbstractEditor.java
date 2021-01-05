package co.amscraft.ultralib.editor.editors;

import co.amscraft.ultralib.editor.DisplayValue;
import co.amscraft.ultralib.editor.Editor;

import java.util.List;

public abstract class AbstractEditor {
    /**
     * This method asks the editor if it is supposed to handle that object based on its type
     *
     * @param type The object its asking if it should handle
     * @return Weather or not to use this editor for that object
     */
    public abstract boolean useEditor(Object type);

    /**
     * The method to actually proccess a command with this editor interface
     *
     * @param editor  The userdata editor for the object
     * @param command The command to use
     * @param value   The argument of that command
     */
    public abstract void run(Editor editor, String command, String value);

    /**
     * A method to get a list of strings that represent the valid options for the user
     *
     * @param editor The editor data
     * @return A list of auto-tab-complete strings
     */
    public abstract List<String> getAutoComplete(Editor editor);

    /**
     * A method that gets the different options to display to the user
     *
     * @param object The object it is retriving info on
     * @return The list of display values
     */
    public abstract List<DisplayValue> getDisplayValues(Object object);

    /**
     * A method that retrives the list of commands that the editor has
     *
     * @return The array of commands that the editor possesses
     */
    public abstract String[] getCommands();

}
