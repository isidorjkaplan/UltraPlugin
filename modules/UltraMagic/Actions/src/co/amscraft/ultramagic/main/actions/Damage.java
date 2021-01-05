package co.amscraft.ultramagic.main.actions;


import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Created by Izzy on 2017-06-02.
 */
public class Damage extends Action {
    @FieldDescription(help = "The amount of damage that this action will do", unit = "half-hearts")
    public double damage = 5;
    @FieldDescription(help = "Wether or not this will bypass armour or not")
    public boolean bypassArmour = false;

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        if (target != null) {
            if (target.getObject() instanceof LivingEntity) {
                LivingEntity e = (LivingEntity) target.getObject();
                if (bypassArmour && (!(target.getObject() instanceof Player) || ((Player) target.getObject()).getGameMode() != GameMode.CREATIVE)) {
                    double health = e.getHealth() - damage;
                    if (health < 0) {
                        health = 0;
                    }
                    e.setHealth(health);
                } else {
                    if (spell.CASTER.getObject() instanceof LivingEntity) {
                        e.damage(damage, (LivingEntity) spell.CASTER.getObject());
                    } else {
                        e.damage(damage);
                    }
                }
            }
        }
    }

}
