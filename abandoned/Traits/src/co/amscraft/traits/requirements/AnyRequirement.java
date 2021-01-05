package co.amscraft.traits.requirements;

import co.amscraft.traits.Requirement;
import co.amscraft.ultralib.editor.FieldDescription;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AnyRequirement extends Requirement {
    @FieldDescription(help = "A list of requirements (If any are true you can see NPC)")
    public List<Requirement> requirements = new ArrayList<>();

    @Override
    public boolean canSee(Player player) {
        for (Requirement r : requirements) {
            if (r.canSee(player)) {
                return true;
            }
        }
        return false;
    }
}
