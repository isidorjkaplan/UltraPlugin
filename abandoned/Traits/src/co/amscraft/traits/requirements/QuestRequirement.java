package co.amscraft.traits.requirements;

import co.amscraft.traits.Requirement;
import co.amscraft.ultralib.editor.FieldDescription;

public abstract class QuestRequirement extends Requirement {
    @FieldDescription(help = "The name of the quest you want to use")
    public String quest;
    @FieldDescription(help = "Leave as -1 for entire quest")
    public int stage = -1;
}
