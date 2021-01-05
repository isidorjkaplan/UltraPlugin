package co.amscraft.traits.requirements;

import co.amscraft.traits.Requirement;
import org.bukkit.entity.Player;

public class HasPermission extends Requirement {
    public String permission = "*";

    @Override
    public boolean canSee(Player player) {
        return player.hasPermission(permission);
    }
}
