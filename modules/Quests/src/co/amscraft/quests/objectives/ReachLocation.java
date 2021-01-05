package co.amscraft.quests.objectives;

import co.amscraft.quests.Objective;
import co.amscraft.quests.QuestLocation;
import co.amscraft.quests.player.ObjectiveInstance;
import co.amscraft.quests.player.QuestInstance;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class ReachLocation extends Objective {
    public QuestLocation location = new QuestLocation();
    public double radius = 1;

    @EventHandler
    public static void onPlayerMoveEvent(PlayerMoveEvent evt) {
        for (ObjectiveInstance instance : QuestInstance.getObjectiveInstances(UltraPlayer.getPlayer(evt.getPlayer()), ReachLocation.class)) {
            if (((ReachLocation) instance.getObjective()).isLocationInside(evt.getPlayer().getLocation())) {
                instance.finish();
            }
        }
    }

    public boolean isLocationInside(Location location) {
        if (!location.getWorld().getName().equals(this.location.world)) {
            return false;
        }
        return (this.location.getLocation().distance(location) <= this.radius);
    }

    @Override
    public String getDisplay() {
        return "Go to " + this.location.toString();
    }

    @Override
    public String formatDisplay(String display, ObjectiveInstance instance) {
        return display.replace("{COUNT}", "0").replace("{MAX_COUNT}", "1");
    }
}
