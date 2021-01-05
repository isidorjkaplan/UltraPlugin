package co.amscraft.quests.objectives;

import co.amscraft.quests.Objective;
import co.amscraft.quests.player.ObjectiveInstance;
import co.amscraft.quests.player.QuestInstance;
import co.amscraft.ultralib.player.UltraPlayer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCDeathEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.ArrayList;
import java.util.List;

public class KillNPC extends Objective {
    public List<Integer> npcs = new ArrayList<>();
    public int kills = 1;

    @EventHandler
    public static void onNPCClick(NPCDeathEvent evt) {
        if (evt.getNPC().getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            Entity killer = ((EntityDamageByEntityEvent) evt.getNPC().getEntity().getLastDamageCause()).getDamager();
            if (killer instanceof Player) {
                for (ObjectiveInstance instance : QuestInstance.getObjectiveInstances(UltraPlayer.getPlayer(killer), KillNPC.class)) {
                    if (((KillNPC) instance.getObjective()).npcs.contains(evt.getNPC().getId())) {
                        int count = (int) instance.data.getOrDefault("count", 0);
                        count += 1;
                        instance.data.put("count", count);
                        if (count >= ((KillNPC) instance.getObjective()).kills) {
                            instance.finish();
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getDisplay() {
        List<String> npcs = new ArrayList<>();
        for (int id : this.npcs) {
            try {
                npcs.add(CitizensAPI.getNPCRegistry().getById(id).getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "Kill " + npcs + " {COUNT}/{MAX_COUNT}";
    }

    @Override
    public String formatDisplay(String display, ObjectiveInstance instance) {
        return display
                .replace("{COUNT}", instance.data.getOrDefault("count", 0) + "")
                .replace("{MAX_COUNT}", this.kills + "");
    }
}
