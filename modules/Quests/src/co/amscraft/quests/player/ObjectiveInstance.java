package co.amscraft.quests.player;

import co.amscraft.quests.Objective;
import co.amscraft.quests.Quest;
import co.amscraft.ultralib.player.UltraPlayer;

import java.util.HashMap;
import java.util.UUID;

public class ObjectiveInstance {
    public int quest = 0;
    public int stage = 0;
    public int objective = 0;
    public UUID player = null;
    public HashMap<String, Object> data = new HashMap<>();

    public ObjectiveInstance() {

    }

    public ObjectiveInstance(Objective objective) {
        this.quest = objective.getStage().getQuest().getId();
        this.stage = this.getQuest().getStages().indexOf(objective.getStage());
        this.objective = objective.getStage().objectives.indexOf(objective);
        this.getObjective().start(this);
    }

    public Quest getQuest() {
        return Quest.getQuest(this.quest);
    }

    public UltraPlayer getPlayer() {
        if (this.player == null) {
            for (UltraPlayer player : UltraPlayer.getPlayers()) {
                QuestInstance quest = player.getData(QuestsData.class).getQuestInstance(this.getQuest());
                if (quest != null) {
                    if (quest.objectives.contains(this)) {
                        this.player = player.getId();
                        return player;
                    }
                }
            }
        }
        return UltraPlayer.getPlayer(player);
    }

    public QuestInstance getQuestInstance() {
        return this.getPlayer().getData(QuestsData.class).getQuestInstance(this.getQuest());
    }

    public void finish() {
        this.getQuestInstance().completeObjective(this.getObjective());
    }

    public String getDisplay() {
        return this.getObjective().formatDisplay(this.getObjective().displayOverride.equalsIgnoreCase("none") ? this.getObjective().getDisplay() : this.getObjective().displayOverride, this);
    }


    public Objective getObjective() {
        try {
            return this.getQuest().getStages().get(this.stage).objectives.get(this.objective);
        } catch (Exception e) {
            this.getQuestInstance().objectives.remove(this);
            e.printStackTrace();
        }
        return null;
    }

}
