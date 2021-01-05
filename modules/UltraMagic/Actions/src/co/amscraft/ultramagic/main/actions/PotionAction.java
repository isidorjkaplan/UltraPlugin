package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Izzy on 2017-06-06.
 */
public class PotionAction extends Action {
    @FieldDescription(help = "The potion effect type")
    public PotionEffectType effect = PotionEffectType.WITHER;
    @FieldDescription(help = "The power multiplier")
    public int power = 1;
    @FieldDescription(help = "The duration of the effect", unit = "seconds")
    public int duration = 10;

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        if (target != null && target.getObject() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) target.getObject();
            PotionEffect effect = new PotionEffect(this.effect, (int) Math.round((duration * 20)), (int) Math.round(power));
            new BukkitRunnable() {
                public void run() {
                    try {
                        entity.addPotionEffect(effect);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.runTask(UltraLib.getInstance());
            //if (entity.hasPotionEffect(effect.getType())) {
            //context.spellInstance.addExpTarget(entity);
            //}
        }
        //nextAction(context.target, context.origin, this, context.spellInstance);
        //nextAction(context);
    }

}
