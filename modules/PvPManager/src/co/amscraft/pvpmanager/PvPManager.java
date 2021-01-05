package co.amscraft.pvpmanager;

import co.amscraft.pvpmanager.shapes.Location;
import co.amscraft.ultralib.editor.EditorCheck;
import co.amscraft.ultralib.editor.EditorData;
import co.amscraft.ultralib.modules.Module;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class PvPManager extends Module {
    @Override
    public String[] getModuleDependancies() {
        return new String[]{"UltraMagic"};
    }

    @Override
    public void onEnable() {
        try {
            EditorData.registerParse(Arena.class, Arena.class.getMethod("getArena", String.class));
            EditorCheck.register(Location.class.getField("world"), new EditorCheck("The world you entered is not a valid world") {
                @Override
                public boolean check(Object object, CommandSender sender) {
                    return Bukkit.getWorld(object + "") != null;
                }
            });
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
       /* for (Class<?> type: this.getClasses()) {
            if (!Modifier.isAbstract(type.getModifiers()) && Shape.class.isAssignableFrom(type)) {
                EditorData.registerConstructors(Shape.class, type.getConstructors());
            }
        }*/
    }

    /*TODO glitches
     * 1) People spawn in the floor when the match starts
     * 2) Disable spells and pvp before match
     */

    @Override
    public void onDisable() {

    }
}
