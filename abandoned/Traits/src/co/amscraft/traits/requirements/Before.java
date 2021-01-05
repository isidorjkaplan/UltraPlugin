package co.amscraft.traits.requirements;

import org.bukkit.entity.Player;

public class Before extends QuestRequirement {

    @Override
    public boolean canSee(Player player) {
        return !After.beatQuest(player, quest, stage);
    }
}
