package co.amscraft.quests.objectives;

import co.amscraft.quests.Objective;
import co.amscraft.quests.player.ObjectiveInstance;
import co.amscraft.quests.player.QuestInstance;
import co.amscraft.quests.rewards.ItemReward;
import co.amscraft.ultralib.player.UltraPlayer;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class DeliverItem extends Objective {
    public ItemReward.Item item = new ItemReward.Item();
    public int NPC = 0;

    @EventHandler
    public static void onNPCClick(NPCRightClickEvent evt) {
        for (ObjectiveInstance instance : QuestInstance.getObjectiveInstances(UltraPlayer.getPlayer(evt.getClicker()), DeliverItem.class)) {
            if (((DeliverItem) instance.getObjective()).NPC == evt.getNPC().getId()) {
                ItemStack stack = evt.getClicker().getInventory().getItemInMainHand();
                if (((DeliverItem) instance.getObjective()).item.getItem().isSimilar(stack)) {
                    int amountRequired = ((DeliverItem) instance.getObjective()).item.getItem().getAmount();
                    int amountLeft = amountRequired - (int) instance.data.getOrDefault("count", 0);
                    if (amountLeft > stack.getAmount()) {
                        stack.setAmount(stack.getAmount() - amountLeft);
                        amountLeft = 0;
                    } else if (amountLeft <= stack.getAmount()) {
                        amountLeft -= stack.getAmount();
                        stack = null;
                    }
                    evt.getClicker().getInventory().setItemInMainHand(stack);
                    instance.data.put("count", amountRequired - amountLeft);
                    if ((amountLeft <= 0)) {
                        instance.finish();
                    } else {
                        instance.getQuestInstance().sendDisplay();
                    }
                }
            }
        }
    }


    @Override
    public String getDisplay() {
        return null;
    }

    @Override
    public String formatDisplay(String display, ObjectiveInstance instance) {
        return null;
    }
}
