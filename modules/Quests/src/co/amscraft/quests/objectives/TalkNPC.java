package co.amscraft.quests.objectives;

import co.amscraft.quests.Objective;
import co.amscraft.quests.player.ObjectiveInstance;
import co.amscraft.quests.player.QuestInstance;
import co.amscraft.ultralib.player.UltraPlayer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;

public class TalkNPC extends Objective {
    public int NPC = -1;

    @EventHandler
    public static void onNPCClick(NPCRightClickEvent evt) {
        for (ObjectiveInstance instance : QuestInstance.getObjectiveInstances(UltraPlayer.getPlayer(evt.getClicker()), TalkNPC.class)) {
            if (((TalkNPC) instance.getObjective()).NPC == evt.getNPC().getId()) {
                instance.finish();
            }
        }
    }

    @Override
    public String getDisplay() {
        return "Talk to " + CitizensAPI.getNPCRegistry().getById(NPC).getName();
    }

    @Override
    public String formatDisplay(String display, ObjectiveInstance instance) {
        return display;
    }
}
