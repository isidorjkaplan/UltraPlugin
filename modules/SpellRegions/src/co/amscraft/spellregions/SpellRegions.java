package co.amscraft.spellregions;

import co.amscraft.ultralib.editor.EditorCheck;
import co.amscraft.ultralib.modules.Module;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class SpellRegions extends Module {
    @Override
    public String[] getModuleDependancies() {
        return new String[]{"UltraMagic"};
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
