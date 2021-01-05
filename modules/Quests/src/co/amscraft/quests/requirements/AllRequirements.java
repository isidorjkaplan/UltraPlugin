package co.amscraft.quests.requirements;

import co.amscraft.quests.Requirement;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.entity.Player;

public class AllRequirements extends ParentRequirement {

    @Override
    public boolean meetsRequirements(Player player) {
        for (Requirement r : this.requirements) {
            if (!r.meetsRequirements(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getFailMessage(UltraPlayer player) {
        for (Requirement r : this.requirements) {
            if (!r.meetsRequirements(player.getBukkit())) {
                return r.getFailMessage(player);
            }
        }
        return "You did not fail the requirement -- report glitch to IzzyK";
    }
}
