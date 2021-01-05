package co.amscraft.quests.requirements;

import co.amscraft.quests.Requirement;
import co.amscraft.ultralib.editor.FieldDescription;

import java.util.ArrayList;
import java.util.List;

public abstract class ParentRequirement extends Requirement {
    @FieldDescription(help = "A list of requirements!")
    public List<Requirement> requirements = new ArrayList<>();

    public static boolean searchFor(Requirement requirement, Requirement root) {
        if (root == requirement) {
            return true;
        }
        if (root instanceof ParentRequirement) {
            for (Requirement r : ((ParentRequirement) root).requirements) {
                if (searchFor(requirement, r)) {
                    return true;
                }
            }
        }
        return false;
    }
}
