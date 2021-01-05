package co.amscraft.quests.requirements;

import co.amscraft.quests.Requirement;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.entity.Player;

public class PermissionRequirement extends Requirement {
    public String permission = "ultralib.quests";

    @Override
    public boolean meetsRequirements(Player player) {
        return player.hasPermission(permission);
    }

    @Override
    public String getFailMessage(UltraPlayer player) {
        return "You do not have permission to take this quest!";
    }
}
