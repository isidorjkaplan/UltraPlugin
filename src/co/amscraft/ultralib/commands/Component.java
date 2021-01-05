package co.amscraft.ultralib.commands;

import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * Created by Izzy on 2017-11-06.
 * This class represents a command component which can store it's own sub commands
 */
public abstract class Component {

    /**
     * A function that checks if a given alias is valid for calling this command component
     * @param alias The alias in question
     * @return If it is a valid alias to call a command component
     */
    public boolean isValid(String alias) {
        if (getAlias().equalsIgnoreCase(alias)) {
            return true;
        }
        for (String string : getAliases()) {
            if (string.equalsIgnoreCase(alias)) {
                return true;
            }
        }
        return false;
    }

    /**
     * A function to get all the components, this is meant to be overrode by actual components
     * but it was not declared abstract beacuse it does not HAVE to be overriden
     * @return The list of all sub components
     */
    public Component[] getComponents() {
        return new Component[0];
    }

    /**
     * A function to get the main alias of a component
     * Precondition: There aare aliases for the component
     * @return The main alias of a component
     */
    public String getAlias() {
        return getAliases()[0];//Returns the first alias of a component
    }

    /**
     * An abstract method to get all the aliases of this component
     * @return The aliases of this component
     */
    public abstract String[] getAliases();

    /**
     * The default run function. If a Component it called and it did not override the run function it will present the user with a list of sub components
     * @param sender The sender
     * @param args The arguments
     * @param i The current argument number
     */
    public void run(CommandSender sender, String[] args, int i) {
        //System.out.println("Sender: " + sender.getName());
        EditorSettings data = UltraPlayer.getPlayer(sender.getName()).getData(EditorSettings.class);
        sender.sendMessage(data.getVariable() + "Help menu");
        String command = data.getColon() + "/" + data.getVariable();
        for (int o = 0; o < i; o++) {
            command += args[o] + " ";
        }
        for (Component component : getComponents()) {
            sender.sendMessage(command + component.getAlias() + data.getValue() + " " + component.getHelp());
        }
    }


    /**
     * Meant to be replaced in subclasses, the help option to display to the user
     * @return The help option to display to the user
     */
    public String getHelp() {
        return "Access the " + this.getAlias() + " command!";
    }

    /**
     * A function that should not be replaced, it executes the Component
     * @param sender The sender
     * @param args The arguments
     * @param i The current argument
     * @param permission The permission node currently used
     */
    public void execute(CommandSender sender, String[] args, int i, String permission) {
        permission = permission + "." + this.getAlias();
        i = i + 1;
        if (args.length < i + 1) {
            args = Arrays.copyOf(args, i + 1);
        }
        if (UltraPlayer.getPlayer(sender).hasPermission(permission)) {
            Component component = getComponent(args[i]);
            if (component != null) {
                component.execute(sender, args, i, permission);
                return;
            }
            this.run(sender, args, i);
        } else {
            sender.sendMessage("You do not have permission to run this command: " + permission);
        }
    }

    /**
     * A function to get the Command that contains this function
     * @return The command that has this Component
     */
    public UltraCommand getCommand() {
        for (UltraCommand command : UltraCommand.getCommands()) {
            if (containsComponent(command, this)) {
                return command;
            }
        }
        return null;
    }

    /**
     * A method to check if a component contains a different component recursivly
     * @param root The root component
     * @param component The component you are checking for
     * @return If the root contains component
     */
    private boolean containsComponent(Component root, Component component) {
        if (root.getComponents().length == 0) {
            return false;
        }
        boolean contains = false;
        for (Component c : root.getComponents()) {
            if (c.getAlias().equals(component.getAlias())) {
                contains = true;
                break;
            }
        }
        return contains || (component.getComponents().length > 0 && containsComponent(root, component));
    }

    /**
     * A function to get the subcomponent by name
     * @param alias The name of the subcomponent
     * @return The subcomponent
     */
    public Component getComponent(String alias) {
        for (Component component : getComponents()) {
            if (component.isValid(alias)) {
                return component;
            }
        }
        return null;
    }

}
