package co.amscraft.quests.player;

import co.amscraft.quests.Objective;
import co.amscraft.quests.Quest;
import co.amscraft.quests.Reward;
import co.amscraft.quests.Stage;
import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.PlayerReference;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class QuestInstance extends PlayerReference {

    public int quest;
    public int stage;
    public List<ObjectiveInstance> objectives = new ArrayList<>();

    public QuestInstance() {

    }

    public QuestInstance(Quest quest, UltraPlayer player) {
        this.quest = quest.getId();
        this.stage = -1;
        this.uuid = player.getId().toString();
        this.nextStage();
    }

    public static boolean playerHasObjective(UltraPlayer player, Class<?> objective) {
        for (QuestInstance quest : player.getData(QuestsData.class).quests) {
            for (ObjectiveInstance o : quest.objectives) {
                if (objective.isAssignableFrom(o.getObjective().getClass())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<ObjectiveInstance> getObjectiveInstances(UltraPlayer player, Class<?> objective) {
        List<ObjectiveInstance> objectives = new ArrayList<>();
        for (QuestInstance quest : player.getData(QuestsData.class).getQuests()) {
            if (quest.objectives != null) {
                for (ObjectiveInstance o : quest.objectives) {
                    if (o != null && objective.isAssignableFrom(o.getObjective().getClass())) {
                        objectives.add(o);
                    }
                }
            }
        }
        return objectives;
    }

    public Quest getQuest() {
        return Quest.getQuest(this.quest);
    }

    public void completeObjective(Objective objective) {
        for (ObjectiveInstance instance : this.objectives) {
            if (instance.getObjective().equals(objective)) {
                this.objectives.remove(instance);
                break;
            }
        }
        for (Reward reward : objective.rewards) {
            reward.give(this.getPlayer().getBukkit());
        }
        this.getPlayer().getBukkit().sendMessage(ChatColor.translateAlternateColorCodes('&', objective.completeMessage));
        if (this.isFinishedStage()) {
            nextStage();
        }
    }

    public void sendDisplay() {
        this.sendDisplay(this.getPlayer());
    }

    public void sendDisplay(UltraPlayer target) {
        EditorSettings s = target.getData(EditorSettings.class);
        target.getBukkit().sendMessage(s.getVariable() + "Quest" + s.getColon() + ": " + s.getValue() + this.getQuest().getName());
        for (ObjectiveInstance i : this.objectives) {
            target.getBukkit().sendMessage(s.getHelp() + i.getDisplay());
        }
    }

    public boolean isFinishedStage() {
        for (ObjectiveInstance objective : this.objectives) {
            if (objective.getObjective().required) {
                return false;
            }
        }
        return true;
    }

    public ObjectiveInstance getObjective(Objective objective) {
        for (ObjectiveInstance instance : this.objectives) {
            if (instance.getObjective() == objective) {
                return instance;
            }
        }
        return null;
    }

    public void nextStage() {
        this.setStage(this.stage + 1);
    }

    public Stage getStage() {
        return this.getQuest().getStages().get(this.stage);
    }

    public void setStage(int stage) {
        if (this.stage >= 0 && this.getStage() != null) {
            this.getPlayer().getBukkit().sendMessage(this.getStage().getFinishMessage());
            for (Reward reward : this.getStage().rewards) {
                reward.give(this.getPlayer().getBukkit());
            }
        }
        this.objectives.clear();
        this.stage = stage;
        if (this.stage >= this.getQuest().getStages().size()) {
            this.endAttempt(true);
        } else {
            this.getPlayer().getBukkit().sendMessage(this.getStage().getStartMessage());
            for (Objective objective : this.getStage().objectives) {
                this.objectives.add(new ObjectiveInstance(objective));
            }
            this.sendDisplay();
        }
    }

    public void endAttempt(boolean successful) {
        this.getPlayer().getData(QuestsData.class).quests.remove(this);
        if (successful) {
            this.getPlayer().getData(QuestsData.class).completed.put(this.getQuest().getId(), System.currentTimeMillis());
            for (Reward reward : this.getQuest().getRewards()) {
                reward.give(this.getPlayer().getBukkit());
            }
            Player player = this.getPlayer().getBukkit();
            String finish = this.getQuest().getFinishMessage();
            new BukkitRunnable() {
                public void run() {
                    try {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', finish));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskLater(UltraLib.getInstance(), 40);
        } else if (this.getQuest().getStages().size() > this.stage && this.getStage() != null) {
            this.getPlayer().getBukkit().sendMessage(ChatColor.translateAlternateColorCodes('&', this.getStage().failMessage));
        }
    }

}
