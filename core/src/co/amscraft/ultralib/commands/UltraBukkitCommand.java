package co.amscraft.ultralib.commands;

import co.amscraft.ultralib.utils.NMSUtils;
import co.amscraft.ultralib.utils.ObjectUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by Izzy on 2017-11-19.
 */
public class UltraBukkitCommand extends BukkitCommand {
    private UltraCommand command = null;


    protected UltraBukkitCommand(UltraCommand command) {
        super(command.getAlias());
        this.command = command;
        this.setAliases(Arrays.asList(command.getAliases()));
        this.setName(this.command.getAlias());
        //this.setDescription(command.getHelp());
        ObjectUtils.debug(Level.INFO, "Successfully registered command: /" + this.getName());
        NMSUtils.registerCommand(this);
        //UltraLib.getInstance().getCommand(this.getName()).setTabCompleter(UltraCommand.getTabCompleter());
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] strings) {
        String[] args = new String[strings.length + 1];
        args[0] = alias;
        for (int i = 0; i < strings.length; i++) {
            args[i + 1] = strings[i];
        }
        //ObjectUtils.debug(Level.WARNING, Arrays.asList(args) + ", " + Arrays.asList(strings));
        this.command.execute(sender, args, 0, "ultralib.commands");
        return true;
    }
}
