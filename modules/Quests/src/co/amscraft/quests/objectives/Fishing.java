package co.amscraft.quests.objectives;

import co.amscraft.quests.Objective;
import co.amscraft.quests.player.ObjectiveInstance;
import co.amscraft.quests.player.QuestInstance;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;

public class Fishing extends Objective {
    public int amount = 1;

    @EventHandler
    public static void onFishCatch(PlayerFishEvent evt) {
        if (evt.getCaught() != null) {
            UltraPlayer player = UltraPlayer.getPlayer(evt.getPlayer());
            for (ObjectiveInstance i : QuestInstance.getObjectiveInstances(player, Fishing.class)) {
                int count = (int) i.data.getOrDefault("count", 0);
                count += 1;
                i.data.put("count", count);
                if (count >= ((Fishing) i.getObjective()).amount) {
                    i.finish();
                }
            }
        }
    }


    @Override
    public String getDisplay() {
        return "Captured {COUNT}/{MAX_COUNT} fish!";
    }

    @Override
    public String formatDisplay(String display, ObjectiveInstance instance) {
        return display
                .replace("{COUNT}", instance.data.getOrDefault("count", 0) + "")
                .replace("{MAX_COUNT}", this.amount + "");
    }

}
