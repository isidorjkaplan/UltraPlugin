package co.amscraft.traits.requirements;

import org.bukkit.entity.Player;

public class Blacklist extends QuestRequirement {
    @Override
    public boolean canSee(Player player) {
        return !Whitelist.hasQuest(player, quest, stage);
    }
}
