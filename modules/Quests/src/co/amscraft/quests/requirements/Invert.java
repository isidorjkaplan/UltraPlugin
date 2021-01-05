package co.amscraft.quests.requirements;

import co.amscraft.quests.Requirement;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.entity.Player;

public class Invert extends Requirement {
    public Requirement requirement = null;

    @Override
    public boolean meetsRequirements(Player player) {
        return !requirement.meetsRequirements(player);
    }

    @Override
    public String getFailMessage(UltraPlayer player) {
        return requirement.getFailMessage(player);
    }
}
