package co.amscraft.quests.rewards;

import co.amscraft.quests.Reward;
import co.amscraft.ultralib.editor.FieldDescription;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandReward extends Reward {
    @FieldDescription(help = "Placeholders: <player>, <displayname>")
    public String command = "bcast &7<player> has completed his quest!";

    @Override
    public void give(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.command
                .replace("<player>", player.getName())
                .replace("<displayname>", player.getDisplayName())
        );
    }
}
