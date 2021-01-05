package co.amscraft.magicranks;

import co.amscraft.ultralib.editor.EditorCheck;
import co.amscraft.ultralib.editor.EditorData;
import co.amscraft.ultralib.modules.Module;
import org.bukkit.command.CommandSender;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class MagicRanks extends Module {
    @Override
    public String[] getModuleDependancies() {
        return new String[]{"UltraMagic"};
    }

    @Override
    public void onEnable() {
        try {
            EditorData.registerParse(MagicRank.class, MagicRank.class.getMethod("getRank", String.class));
            EditorCheck.register(MagicRank.class.getField("ranks"), new EditorCheck("You cannot make two rank setups for the same group and the group must exist!") {
                @Override
                public boolean check(Object object, CommandSender sender) {
                    return MagicRank.getRank(object + "") == null && PermissionsEx.getPermissionManager().getGroupNames().contains(object + "");
                }
            });
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

    }
}
