package co.amscraft.mcmmospy;

import co.amscraft.ultralib.commands.UltraCommand;

public class McSpyCommand extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"McMMOSpy"};
    }

    @Override
    public String getHelp() {
        return "The command to enable McPartySpying";
    }
}
