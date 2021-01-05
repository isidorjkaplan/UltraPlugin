package co.amscraft.quests;

import co.amscraft.quests.requirements.ParentRequirement;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public abstract class Requirement {
    @FieldDescription(save = false, show = false)
    private Quest quest = null;

    public abstract boolean meetsRequirements(Player player);

    public Quest getQuest() {
        if (quest == null) {
            for (Quest quest : Quest.getList(Quest.class)) {
                if (ParentRequirement.searchFor(this, quest.getRequirement())) {
                    this.quest = quest;
                    return quest;
                }
            }
        }
        return this.quest;
    }

    public abstract String getFailMessage(UltraPlayer player);

    @Override
    public String toString() {
        String s = this.getClass().getSimpleName() + "[";
        for (Field field : this.getClass().getDeclaredFields()) {
            try {
                s += field.get(this) + " ,";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (s.endsWith(" ,")) {
            s = s.substring(0, s.length() - 3);
        }
        s += "]";
        return s;
    }
}
