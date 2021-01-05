package co.amscraft.quests;

import co.amscraft.quests.player.QuestsData;
import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultralib.tic.ServerTic;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Effect;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class QuestsListener implements Listener {

    @ServerTic(isAsync = false, delay = 5)
    public static void serverTickQuest() {
        for (UltraPlayer player : UltraPlayer.getPlayers()) {
            if (player != null && player.getBukkit() != null) {
                Player bukkit = player.getBukkit();
                for (Entity e : bukkit.getLocation().getWorld().getNearbyEntities(bukkit.getLocation(), 10, 10, 10)) {
                    if (e != null && CitizensAPI.getNPCRegistry().isNPC(e) && e instanceof LivingEntity) {
                        try {
                            for (Quest quest : Quest.getQuests()) {
                                if (quest.getNPC() == CitizensAPI.getNPCRegistry().getNPC(e).getId()) {
                                    if (quest.getRequirement() != null && quest.getRequirement().meetsRequirements(bukkit) && player.getData(QuestsData.class).getQuestInstance(quest) == null) {
                                        bukkit.playEffect(((LivingEntity) e).getEyeLocation().add(0, 0.5, 0), Effect.RECORD_PLAY, 0);
                                        bukkit.spawnParticle(Particle.NOTE, ((LivingEntity) e).getEyeLocation().add(0, 0.5, 0), 1);
                                        //player.getBukkit().spigot().p.playEffect(player.getBukkit().getLocation(), Particle.NOTE, 0, 0, 0, 0, 0, 0, 1, 0);
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e1) {
                            System.out.println("Glitched while preforming Quests note thingy!");
                            //e1.printStackTrace();
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onNPCClick(NPCRightClickEvent evt) {
        UltraPlayer player = UltraPlayer.getPlayer(evt.getClicker());
        if (player != null) {
            QuestsData data = player.getData(QuestsData.class);
            data.getPending().clear();
            for (Quest quest : Quest.getQuests()) {
                if (quest.getNPC() == evt.getNPC().getId()) {
                    if (quest.getRequirement().meetsRequirements(player.getBukkit())) {
                        if (data.getQuestInstance(quest) == null) {
                            data.addPending(quest);
                        }
                    }
                }
            }
        }
    }
}
