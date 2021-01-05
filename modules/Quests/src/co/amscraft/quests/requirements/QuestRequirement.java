package co.amscraft.quests.requirements;

import co.amscraft.quests.Quest;
import co.amscraft.quests.Requirement;
import co.amscraft.quests.player.QuestsData;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.entity.Player;

public class QuestRequirement extends Requirement {
    public String quest = "";

    @Override
    public boolean meetsRequirements(Player player) {
        Quest quest = Quest.getQuest(this.quest);
        if (quest == null) {
            return false;
        }
        return UltraPlayer.getPlayer(player).getData(QuestsData.class).completed.containsKey(quest.getId());
    }

    @Override
    public String getFailMessage(UltraPlayer player) {
        return "You must complete quest: " + quest;
    }
}
