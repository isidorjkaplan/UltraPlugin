package co.amscraft.ultralib.commands;

import org.bukkit.command.CommandSender;

import java.util.Arrays;

/**
 * Created by Izzy on 2017-11-23.
 */
public class BaseUltraCommand extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"Ultra", "uhelp"};
    }

    @Override
    public String getHelp() {
        return "The UltraCommand help menu";
    }

    @Override
    public Component[] getComponents() {
        return UltraCommand.getCommands().toArray(new Component[UltraCommand.getCommands().size()]);
    }

    @Override
    public void execute(CommandSender sender, String[] args, int i, String permission) {
        i = i + 1;
        if (args.length < i + 1) {
            args = Arrays.copyOf(args, i + 1);
        }
        for (Component component : getComponents()) {
            if (component.isValid(args[i])) {
                component.execute(sender, args, i, permission);
                return;
            }
        }
        this.run(sender, args, i);

    }
}
