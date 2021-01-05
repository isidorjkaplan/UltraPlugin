package co.amscraft.quests.requirements;

import co.amscraft.quests.Quest;
import co.amscraft.quests.Requirement;
import co.amscraft.quests.player.QuestsData;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.entity.Player;

public class Cooldown extends Requirement {
    @FieldDescription(unit = "seconds", help = "The amount of time before you can take this quest again, set to -1")
    public long cooldown = -1;

    @Override
    public boolean meetsRequirements(Player bukkit) {
        if (bukkit != null) {
            UltraPlayer player = UltraPlayer.getPlayer(bukkit);
            if (player != null) {
                // PlayerUtility utils = player.getData(PlayerUtility.class);
                Quest quest = getQuest();
                if (quest != null) {
                    if (!player.getData(QuestsData.class).getCompleted().containsKey(getQuest().getId())) {
                        return true;
                    }
                    if (cooldown < 0) {
                        return false;
                    }
                    return timeLeft(player) <= 0;
                }
            }
        }
        return false;
    }

    private double timeLeft(UltraPlayer player) {
        return cooldown - (double) (System.currentTimeMillis() - player.getData(QuestsData.class).completed.get(this.getQuest().getId())) / 1000;
    }


    @Override
    public String getFailMessage(UltraPlayer player) {
        return cooldown < 0 ? "You have already completed this quest" : "You must wait " + ((int) timeLeft(player) + " more seconds!");
    }
}
