package co.amscraft.traits.requirements;

import co.amscraft.traits.Requirement;
import co.amscraft.ultralib.editor.FieldDescription;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AllRequirements extends Requirement {
    @FieldDescription(help = "A list of requirements (All requirements MUST BE TRUE to see NPC)")
    public List<Requirement> requirements = new ArrayList<>();

    @Override
    public boolean canSee(Player player) {
        for (Requirement r : requirements) {
            if (!r.canSee(player)) {
                return false;
            }
        }
        return true;
    }
}
