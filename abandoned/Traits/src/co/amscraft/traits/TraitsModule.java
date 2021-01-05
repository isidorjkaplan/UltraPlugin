package co.amscraft.traits;

import co.amscraft.ultralib.editor.EditorData;
import co.amscraft.ultralib.modules.Module;
import net.citizensnpcs.api.npc.NPC;

public class TraitsModule extends Module {

    @Override
    public String[] getModuleDependancies() {
        return new String[]{"QuestsModule"};
    }

    @Override
    public void onEnable() {
        try {
            /*EditorData.registerConstructors(Requirement.class, After.class.getConstructors());
            EditorData.registerConstructors(Requirement.class, Before.class.getConstructors());
            EditorData.registerConstructors(Requirement.class, Whitelist.class.getConstructors());
            EditorData.registerConstructors(Requirement.class, Blacklist.class.getConstructors());
            EditorData.registerConstructors(Requirement.class, AllRequirements.class.getConstructors());
            EditorData.registerConstructors(Requirement.class, AnyRequirement.class.getConstructors());
            EditorData.registerConstructors(Requirement.class, HasPermission.class.getConstructors());*/
            EditorData.registerParse(Vanish.class, Vanish.class.getMethod("getVanish", NPC.class));
            EditorData.registerParse(NPC.class, Vanish.class.getMethod("getNPC", int.class));
            //EditorData.registerConstructors(Requirement.class, .class.getConstructors());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

    }
}
