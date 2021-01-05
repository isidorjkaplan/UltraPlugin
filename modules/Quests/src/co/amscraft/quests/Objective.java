package co.amscraft.quests;

import co.amscraft.quests.player.ObjectiveInstance;
import co.amscraft.ultralib.editor.FieldDescription;
import org.bukkit.event.Listener;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class Objective implements Listener {

    public boolean required = true;
    public String completeMessage = "&7You have completed objective " + this.getClass().getSimpleName();
    @FieldDescription(help = "Placeholders: {PLAYER}, {COUNT}, {MAX_COUNT}")
    public String displayOverride = "none";
    public List<Reward> rewards = new ArrayList<>();
    @FieldDescription(save = false, show = false)
    private Stage stage = null;

    public abstract String getDisplay();

    public abstract String formatDisplay(String display, ObjectiveInstance instance);

    public Stage getStage() {
        for (Quest quest : Quest.getList(Quest.class)) {
            for (Stage stage : quest.getStages()) {
                if (stage.hasObjective(this)) {
                    this.stage = stage;
                    return this.stage;
                }
            }
        }
        return this.stage;
    }

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


    /**
     * A method to be called whenever the objective starts, optional overloading / abstract method
     *
     * @param instance The QuestInstance that is calling the objective
     */
    public void start(ObjectiveInstance instance) {
        /**Empty method, sometimes overloaded by subclasses**/
    }

}
