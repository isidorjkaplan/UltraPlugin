package co.amscraft.rpgitems;


import co.amscraft.ultralib.editor.EditorCheck;
import co.amscraft.ultralib.modules.Module;
import org.bukkit.command.CommandSender;

public class RPGItems extends Module {
    @Override
    public String[] getModuleDependancies() {
        return new String[]{"UltraMagic"};
    }

    @Override
    public void onEnable() {
        try {
            EditorCheck.register(RPGItem.class.getField("name"), new EditorCheck("You must enter a one word identifier") {
                @Override
                public boolean check(Object object, CommandSender sender) {
                    return !object.toString().contains(" ");
                }
            });
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

    }
}
