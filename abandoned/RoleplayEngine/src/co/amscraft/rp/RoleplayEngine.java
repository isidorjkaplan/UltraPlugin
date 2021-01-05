package co.amscraft.rp;

import co.amscraft.ultralib.editor.EditorData;
import co.amscraft.ultralib.modules.Module;

public class RoleplayEngine extends Module {

    @Override
    public String[] getModuleDependancies() {
        return new String[0];
    }

    /*
     * TODO: SUggustions
     *   Injuries
     *   Health
     *   Stamina
     *   Inventory
     *   
     */
    @Override
    public void onEnable() {
        try {
            EditorData.registerParse(RoleplayRank.class, RoleplayRank.class.getMethod("getRank", String.class));
            EditorData.registerParse(RoleplayPower.class, RoleplayPower.class.getMethod("getPower", String.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

    }
}
