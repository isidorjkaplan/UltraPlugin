package co.amscraft.quests.objectives;

import co.amscraft.quests.Objective;
import co.amscraft.quests.player.ObjectiveInstance;
import co.amscraft.quests.player.QuestInstance;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;

public class KillMobs extends Objective {
    @FieldDescription(help = "The types of monsters that qualify")
    public List<EntityType> monsters = new ArrayList<>();
    @FieldDescription(help = "The amount of monsters you need to kill!")
    public int amount = 1;

    @FieldDescription(help = "The list of valid names for killed mobs, leave this list empty to allow for killing any mobs of the correct type")
    public List<String> allowedNames = new ArrayList<>();

    public boolean isAllowed(Entity entity) {
        if (!monsters.contains(entity.getType())) {
            return false;
        }
        if (allowedNames.isEmpty()) {
            return true;
        } else {
            if (entity.getCustomName() == null) {
                return false;
            }
            String entityName = ChatColor.stripColor(entity.getCustomName()).toLowerCase();
            for (String name: allowedNames) {
                if (name.toLowerCase().contains(entityName)) {
                    return true;
                }
            }
            return false;
        }
    }

    @EventHandler
    public static void onEntityDeath(EntityDeathEvent evt) {
        if (evt.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            Entity entity = ((EntityDamageByEntityEvent) evt.getEntity().getLastDamageCause()).getDamager();
            if (entity instanceof Player) {
                for (ObjectiveInstance instance : QuestInstance.getObjectiveInstances(UltraPlayer.getPlayer(((Player) entity)), KillMobs.class)) {
                    if (((KillMobs)instance.getObjective()).isAllowed(evt.getEntity())) {
                        int count = (int) instance.data.getOrDefault("count", 0);
                        count += 1;
                        instance.data.put("count", count);
                        if (count >= ((KillMobs) instance.getObjective()).amount) {
                            instance.finish();
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getDisplay() {
        return "Kill {COUNT}/{MAX_COUNT} " + monsters;
    }

    @Override
    public String formatDisplay(String display, ObjectiveInstance instance) {
        return display
                .replace("{COUNT}", instance.data.getOrDefault("count", 0) + "")
                .replace("{MAX_COUNT}", this.amount + "");
    }
}
