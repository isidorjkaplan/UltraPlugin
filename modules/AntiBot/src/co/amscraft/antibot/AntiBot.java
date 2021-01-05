package co.amscraft.antibot;

import co.amscraft.ultralib.modules.Module;
import co.amscraft.ultralib.tic.ServerTic;

import java.io.IOException;

public class AntiBot extends Module {
    @ServerTic(delay = 36400, isAsync = true)
    public static void saveIPGraph() throws IOException {
        IPGraph.getGraph().save();
    }

    @Override
    public String[] getModuleDependancies() {
        return new String[]{"OnTime"};
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {
        try {
            IPGraph.getGraph().save();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
