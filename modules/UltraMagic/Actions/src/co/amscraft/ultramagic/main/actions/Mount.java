package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class Mount extends Action {
    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        if (target.getObject() instanceof LivingEntity && spell.CASTER.getObject() instanceof LivingEntity) {
            ((LivingEntity) target.getObject()).addPassenger((Entity) spell.CASTER.getObject());
        }
    }
}
