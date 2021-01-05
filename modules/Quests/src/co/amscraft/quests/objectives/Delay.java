package co.amscraft.quests.objectives;

import co.amscraft.quests.Objective;
import co.amscraft.quests.player.ObjectiveInstance;
import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.editor.FieldDescription;
import org.bukkit.scheduler.BukkitRunnable;


public class Delay extends Objective {

    @FieldDescription(help = "How long the delay is", unit = "seconds")
    public double delay = 1;

    @Override
    public void start(ObjectiveInstance instance) {
        instance.data.put("start", System.currentTimeMillis());
        new BukkitRunnable() {
            public void run() {
                instance.finish();
            }
        }.runTaskLaterAsynchronously(UltraLib.getInstance(), Math.round(delay * 20));
    }

    @Override
    public String getDisplay() {
        return "You must wait {COUNT} more seconds";
    }

    @Override
    public String formatDisplay(String display, ObjectiveInstance instance) {
        return display.replace("{COUNT}", delay - ((System.currentTimeMillis() - (long) instance.data.get("start")) / 1000) + "");
    }
}
