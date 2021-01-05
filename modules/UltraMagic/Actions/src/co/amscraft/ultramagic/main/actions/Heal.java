package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;
import org.bukkit.entity.LivingEntity;

public class Heal extends Action {
    @FieldDescription(help = "The amount of health to restore", unit = "half-hearts")
    public double health = 4;

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        if (target.getObject() instanceof LivingEntity) {
            ((LivingEntity) target.getObject()).setHealth(((LivingEntity) target.getObject()).getHealth() + health);
        }
    }
}
