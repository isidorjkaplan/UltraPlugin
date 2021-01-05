package co.amscraft.quests;

import co.amscraft.ultralib.editor.FieldDescription;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class Stage {
    public String startMessage = "none";
    public String finishMessage = "none";
    public String failMessage = "&cYou have failed the quest!";
    public List<Reward> rewards = new ArrayList<>();
    public List<Objective> objectives = new ArrayList<>();
    @FieldDescription(save = false, show = false)
    private Quest quest = null;

    public String getStartMessage() {
        if (startMessage.equals("none")) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', this.startMessage);
    }

    public String getFinishMessage() {
        if (finishMessage.equals("none")) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', this.finishMessage);
    }

    public Quest getQuest() {
        if (this.quest == null) {
            for (Quest quest : Quest.getList(Quest.class)) {
                if (quest.getStages().contains(this)) {
                    this.quest = quest;
                    break;
                }
            }
        }
        return this.quest;
    }

    public boolean hasObjective(Objective objective) {
        return this.objectives.contains(objective);
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        for (Objective objective : this.objectives) {
            list.add(objective.getClass().getSimpleName());
        }
        return "Stage" + list;
    }
}
