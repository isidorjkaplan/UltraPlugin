package co.amscraft.traits.requirements;

import co.amscraft.quests.Quest;
import co.amscraft.quests.player.QuestsData;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.entity.Player;

public class After extends QuestRequirement {

    public static boolean beatQuest(Player player, String quest, int stage) {
        QuestsData data = UltraPlayer.getPlayer(player).getData(QuestsData.class);
        Quest q = Quest.getQuest(quest);
        return data.completed.containsKey(q.getId()) || (stage != -1 && data.getQuestInstance(q) != null && data.getQuestInstance(q).stage > stage);
    }

    @Override
    public boolean canSee(Player player) {
        return beatQuest(player, quest, stage);
    }
}
