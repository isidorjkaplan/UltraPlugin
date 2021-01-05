package co.amscraft.quests.objectives;

import co.amscraft.quests.Objective;
import co.amscraft.quests.player.ObjectiveInstance;
import co.amscraft.quests.player.QuestInstance;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class KillPlayer extends Objective {
    public int kills = 1;
    @FieldDescription(help = "Only count the kill if the player had one of the listed permissions, leave empty for all kills to count")
    public List<String> permissions = new ArrayList<>();
    @FieldDescription(help = "A list of names of required players, leave empty for all players to be counted")
    public List<String> names = new ArrayList<>();
    private boolean checkName(String name) {
        return names.isEmpty() || names.contains(name);
    }

    private boolean checkPermissions(Player player) {
        if (permissions.isEmpty()) {
            return true;
        }
        for (String permission: permissions) {
            if (player.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAllowed(Player player) {
        return checkName(player.getName()) && checkPermissions(player);
    }

    @EventHandler
    public static void onPlayerKillEvent(PlayerDeathEvent evt) {
        Player killer = evt.getEntity().getKiller();
        if (killer != null) {
            for (ObjectiveInstance instance : QuestInstance.getObjectiveInstances(UltraPlayer.getPlayer(killer), KillPlayer.class)) {
                if (((KillPlayer)instance.getObjective()).isAllowed(evt.getEntity())) {
                    int count = (int) instance.data.getOrDefault("count", 0);
                    count += 1;
                    instance.data.put("count", count);
                    if (count >= ((KillPlayer) instance.getObjective()).kills) {
                        instance.finish();
                    }
                }
            }
        }
    }

    @Override
    public String getDisplay() {
        return "Kill {COUNT}/{MAX_COUNT} players!";
    }

    @Override
    public String formatDisplay(String display, ObjectiveInstance instance) {
        return display
                .replace("{COUNT}", instance.data.getOrDefault("count", 0) + "")
                .replace("{MAX_COUNT}", this.kills + "");
    }
}
