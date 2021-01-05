package co.amscraft.ultralib.network;

import co.amscraft.ultralib.network.events.PacketCommandEvent;
import co.amscraft.ultralib.utils.ObjectUtils;

import java.util.Arrays;
import java.util.HashMap;

public class PacketCommand {
    private static HashMap<String, Command> commands = new HashMap<>();
    private String command;
    private Object[] args;

    public PacketCommand(String command, Object... args) {
        this.setCommand(command);
        this.setArgs(args);
    }

    public PacketCommand() {
        this(null);
    }

    public static HashMap<String, Command> getCommands() {
        return commands;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    @Override
    public String toString() {
        return ObjectUtils.toString(this).replace(this.getArgs() + "", Arrays.asList(this.getArgs()) + "");
    }

    public static abstract class Command {
        public abstract void run(PacketCommandEvent evt);
    }
}
