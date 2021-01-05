package co.amscraft.quests.player;

import co.amscraft.quests.Quest;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.player.PlayerData;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestsData extends PlayerData {
    public List<QuestInstance> quests = new ArrayList<>();
    public HashMap<Integer, Long> completed = new HashMap<>();
    @FieldDescription(save = false)
    private List<Quest> pending = new ArrayList<>();

    public List<QuestInstance> getQuests() {
        if (quests == null) {
            quests = new ArrayList<>();
        }
        return quests;
    }

    public List<Quest> getPending() {
        return this.pending;
    }

    public HashMap<Integer, Long> getCompleted() {
        if (completed == null) {
            completed = new HashMap<>();
        }
        return completed;
    }


    public void addPending(Quest quest) {
        if (quest != null) {
            this.getPlayer().getBukkit().sendMessage(ChatColor.translateAlternateColorCodes('&', quest.getQuestion()));
            EditorSettings s = this.getPlayer().getData(EditorSettings.class);
            this.getPlayer().getBukkit().sendMessage(s.getHelp() + "Use " + s.getSuccess() + "/quest accept" + s.getHelp() + " or " + s.getError() + "/quest decline");
        }
        if (!this.pending.contains(quest)) {
            this.pending.add(quest);
        }
    }

    public QuestInstance getQuestInstance(Quest quest) {
        for (QuestInstance i : quests) {
            try {
                if (i.getQuest().equals(quest)) {
                    return i;
                }
            } catch (Exception e) {
                quests.remove(i);
                this.getPlayer().getBukkit().sendMessage(this.getPlayer().getData(EditorSettings.class).getError() + "An error occurred with quest instance: " + i.getQuest());
            }
        }
        return null;
    }

}
