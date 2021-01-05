package co.amscraft.ultralib.commands;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.editor.EditorData;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by Izzy on 2017-11-05.
 * A UltraCommand which IS-A Command Component
 */
public abstract class UltraCommand extends Component {
    /**
     * The list of all the loaded UltraCommands
     */
    private static Set<UltraCommand> commands = new HashSet<>();
    /**
     * The BukkitCommand that represents this UltraCommand, used for interfacing with Minecraft
     */
    public UltraBukkitCommand command;

    /**
     * A method to register an UltraCommand from it's class
     * @param type The UltraCommand to registerr
     */
    public static void register(Class<? extends UltraCommand> type) {
        if (!Modifier.isAbstract(type.getModifiers())) {
            try {
                UltraCommand command = type.newInstance();
                register(command);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A function to register an UltraCommand given the CommandObject
     * @param command The UltraCommand Object
     */
    public static void register(UltraCommand command) {
        if (!UltraCommand.commands.contains(command)) {
            for (UltraCommand cmd : UltraCommand.commands) {
                if (cmd.getAlias().equalsIgnoreCase(command.getAlias())) {
                    return;
                }
            }
            command.command = new UltraBukkitCommand(command);
            UltraCommand.commands.add(command);
        }
    }

    /**
     * A function to get all the loaded UltraCommands
     * @return The loaded UltraCommands
     */
    public static Set<UltraCommand> getCommands() {
        return UltraCommand.commands;
    }

    /**
     * A function to get a UltraCommand by name
     * @param name The name of the command
     * @return The UltraCommand
     */
    public static UltraCommand getCommand(String name) {
        for (UltraCommand cmd : getCommands()) {
            if (cmd.isValid(name)) {
                return cmd;
            }
        }
        return null;
    }

    /**
     * A function to get the possible tab-completes of a given String of arguments
     * @param command The string of argruments
     * @return The AutoTabComplete list
     */
    public List<String> completeTab(String command) {
        String[] split = command.split(" ");
        if (command.endsWith(" ")) {
            split = Arrays.copyOf(split, split.length + 1);
            split[split.length - 1] = "";
        }
        String editing = split[split.length - 1].toLowerCase();
        Component component = this;
        int i = 1;
        while (i < split.length) {
            Component c = component.getComponent(split[i]);
            i++;
            if (c != null) {
                component = c;
                //System.out.println(i + ", " + component.getAlias());
            } else {
                break;
            }
        }
        //System.out.println(component.getAlias() + ", " + i);
        List<String> list = new ArrayList<>();
        if (component.getComponents().length > 0) {
            for (Component c : component.getComponents()) {
                if (c.getAlias().toLowerCase().startsWith(split[split.length - 1].toLowerCase())) {
                    list.add(c.getAlias());
                }
            }
        } else if (component.getHelp() != null && component.getHelp().length() > 3) {
            String[] helps = component.getHelp().split(" ");
            if (helps.length > split.length - i) {
                String help = helps[split.length - i];
                help = help.substring(1, help.length() - 1);
                //System.out.println(help);
                if (help.toLowerCase().contains("player")) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.getName().toLowerCase().startsWith(editing)) {
                            list.add(player.getName());
                        }
                    }
                }
                boolean foundType = false;
                for (Class<?> type : EditorData.getRegisteredDatatypes()) {
                    if (UltraObject.class.isAssignableFrom(type) && type.getSimpleName().equalsIgnoreCase(help)) {
                        for (UltraObject object : UltraObject.getList((Class<? extends UltraObject>) type)) {
                            if (object.toString().toLowerCase().startsWith(editing)) {
                                list.add(object.toString());
                                foundType = true;
                            }
                        }
                        break;
                    }
                }
                if (!foundType) {
                    list.addAll(Arrays.asList(help.split("[/|,]")));
                }

            }
        }
        return list;
    }

    @Override
    public UltraCommand getCommand() {
        return this;
    }

    public UltraBukkitCommand getBukkitCommand() {
        return this.command;
    }

    @Override
    public abstract String getHelp();


}
