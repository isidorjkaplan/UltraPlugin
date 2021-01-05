package co.amscraft.spellregions;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.actions.Action;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class SpellRegion extends UltraObject {
    @FieldDescription(help = "The name of the WorldGuard region it uses!")
    public String region = "<region_name>";
    @FieldDescription(help = "The world of the region")
    public String world = Bukkit.getWorlds().get(0).getName();
    public String errorMessage = "Features of the spell you cast were disabled in this region!";

    @FieldDescription(help = "The name of the action")
    private List<String> actions = new ArrayList<>();

    @FieldDescription(help = "Whitelist = Allow only listed actions\nBlacklist = Allow all except listed actions")
    private Mode mode = Mode.BLACKLIST;

    public enum Mode {
        WHITELIST, BLACKLIST
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return region + "[" + world + "]";
    }

    public boolean canCast(Action action) {
        String type = action.getClass().getName().toLowerCase();
        for (String s: actions) {
            if (type.contains(s.toLowerCase())) {
                return mode == Mode.WHITELIST;
            }
        }
        return mode == Mode.BLACKLIST;
    }

    public static SpellRegion getRegion(World world, String region) {
        return getRegion(world.getName(), region);
    }
    public static SpellRegion getRegion(String world, String region) {
        for (SpellRegion sr: getList(SpellRegion.class)) {
            if (sr.world.equalsIgnoreCase(world) && sr.region.equalsIgnoreCase(region)) {
                return sr;
            }
        }
        return null;
    }
}
