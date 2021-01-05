package co.amscraft.ultrastats;

import co.amscraft.ultralib.editor.EditorData;
import co.amscraft.ultralib.modules.Module;

/**
 * Created by Izzy on 2017-11-12.
 */
public class UltraStats extends Module {
    @Override
    public String[] getModuleDependancies() {
        return new String[]{"UltraMagic"};
    }

    @Override
    public void onEnable() {
        try {
            EditorData.registerParse(Stat.class, Stat.class.getMethod("getStat", String.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

    }
}
