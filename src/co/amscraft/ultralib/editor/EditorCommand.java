package co.amscraft.ultralib.editor;

import co.amscraft.ultralib.commands.Component;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * Created by Izzy on 2017-11-08.
 */
public class EditorCommand extends UltraCommand {
    @Override
    public Component[] getComponents() {
        return new Component[]{UltraCommand.getCommand("settings"), new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"send", "s"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                if (UltraPlayer.getPlayer(sender).hasData(Editor.class)) {
                    if (args.length == i + 1) {
                        args = Arrays.copyOf(args, i + 2);
                    }
                    //UltraPlayer.getPlayer(sender).getData(Editor.class).run(args[i].replace(":", ""), args[i + 1]);
                }
            }

            @Override
            public String getHelp() {
                return "Send a message to the editor via command!";
            }
        }};
    }

    @Override
    public String[] getAliases() {
        return new String[]{"editor", "e"};
    }

    @Override
    public void run(CommandSender sender, String[] args, int i) {
        EditorSettings data = UltraPlayer.getPlayer(sender.getName()).getData(EditorSettings.class);
        if (args[i] == null) {
            sender.sendMessage(data.getVariable() + "Please enter one of the following editor types");
            String string = data.getColon() + "/" + data.getValue();
            for (int o = 0; o < i; o++) {
                string += args[o] + " ";
            }
            for (Class<?> type : EditorData.getRegisteredDatatypes()) {
                if (EditorData.hasRoot(type)) {
                    sender.sendMessage(string + type.getSimpleName());
                }
            }
            sender.sendMessage(data.getHelp() + "Or use one of the following commands: ");
            super.run(sender, args, i);
        } else {
            for (Class<?> type : EditorData.getRegisteredDatatypes()) {
                if (type.getSimpleName().equalsIgnoreCase(args[i])) {
                    if (UltraPlayer.getPlayer(sender).hasPermission("ultralib.commands.editor." + type.getSimpleName())) {
                        Editor editor = UltraPlayer.getPlayer(sender.getName()).getData(Editor.class);
                        editor.getEditing().add(type);
                        editor.getEditing().add(EditorData.getRoot(type));
                        editor.resend();
                    } else {
                        sender.sendMessage(data.getError() + "You do not have permission to edit this data type!");
                    }
                    return;
                }
            }
            sender.sendMessage(data.getError() + "You entered an invalid datatype: " + args[i]);
        }
    }

    @Override
    public String getHelp() {
        return "The command to access the in game object editor!";
    }

}
