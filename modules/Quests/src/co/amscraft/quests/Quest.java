package co.amscraft.quests;

import co.amscraft.quests.requirements.AllRequirements;
import co.amscraft.ultralib.UltraObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Quest extends UltraObject {
    private String name = "Quest";
    private Requirement requirement = new AllRequirements();
    private String question = "Would you like to take the quest?";
    private String decline = "When your ready to take the quest come back!";
    private String finishMessage = "You have completed the quest!";
    private int NPC = -1;
    private List<Stage> stages = new ArrayList<>();
    private List<Reward> rewards = new ArrayList<>();

    public static Quest getQuest(String name) {
        if (name == null) {
            return null;
        }
        name = name.replace("_", " ");
        for (Quest quest : Quest.getList(Quest.class)) {
            if (quest.getName().equalsIgnoreCase(name)) {
                return quest;
            }
        }
        for (Quest quest : Quest.getList(Quest.class)) {
            if (quest.getName().toLowerCase().contains(name.toLowerCase())) {
                return quest;
            }
        }
        return null;
    }

    public static Set<Quest> getQuests() {
        return getList(Quest.class);
    }

    public static Quest getQuest(int id) {
        return UltraObject.getObject(Quest.class, "ID", id);
    }

    public List<Stage> getStages() {
        return this.stages;
    }

    /*
    public Objective getObjective(int[] id) {
        Objective objective = null;
        if (id.length > 1) {
            Stage stage = this.getStages().get(id[0]);
            List<Objective> objectives = stage.objectives;
            for (int i = 1; i < id.length; i++) {
                objective = objectives.get(i);
                if (!(objective instanceof ParentObjective)) {
                    return objective;
                }
                objectives = ((ParentObjective) objective).objectives;
            }
        }
        return objective;
    }*/

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Requirement getRequirement() {
        return requirement;
    }

    public void setRequirement(Requirement requirement) {
        this.requirement = requirement;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDecline() {
        return decline;
    }

    public void setDecline(String decline) {
        this.decline = decline;
    }

    public String getFinishMessage() {
        return finishMessage;
    }

    public void setFinishMessage(String finishMessage) {
        this.finishMessage = finishMessage;
    }

    public int getNPC() {
        return NPC;
    }

    public void setNPC(int NPC) {
        this.NPC = NPC;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(List<Reward> rewards) {
        this.rewards = rewards;
    }
}
