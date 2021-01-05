package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

public class ChangeHealth extends Action {
    @FieldDescription(help = "The amount of health they should have", unit = "half-hearts")
    public int hearts = 20;
    @FieldDescription(help = "How long the extra hearts last for", unit = "seconds")
    public double duration = 10;
    @FieldDescription(help = "Weather or not the added hearts should be healed or empty")
    public boolean heal = false;

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        if (target.getObject() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) target.getObject();
            double max = entity.getMaxHealth();
            entity.setMaxHealth(this.hearts);
            if (heal) {
                entity.setHealth(entity.getHealth() + Math.abs((hearts - max)));
            }
            new BukkitRunnable() {
                public void run() {
                    try {
                        entity.resetMaxHealth();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskLater(UltraLib.getInstance(), Math.round(20 * duration));
        }
    }
}
