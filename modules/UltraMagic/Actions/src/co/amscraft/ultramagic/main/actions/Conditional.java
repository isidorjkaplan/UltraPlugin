package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.TargetSelectorAction;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Created by Izzy on 2017-11-10.
 */
public class Conditional extends TargetSelectorAction {
    public boolean loop = false;
    public double delay = 1;
    public Conditon conditon = Conditon.SHIFTING;
    public TargetType targetType = TargetType.CASTER;
    public boolean invert = false;//true means run it true, false means run if false

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        if (!loop && check(spell, target, caster)) {
            this.runActions(spell, target, caster);
        } else {
            while (check(spell, target, caster)) {
                this.runActions(spell, target, caster);
                try {
                    Thread.sleep((long) (delay * 1000));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean check(SpellInstance spell, Target t, Target caster) {
        Target target;
        switch (targetType) {
            case CASTER:
                target = spell.CASTER;
                break;
            case ORIGIN:
                target = caster;
                break;
            case TARGET:
                target = t;
                break;
            default:
                target = null;
                break;
        }
        boolean conditon = invert;
        switch (this.conditon) {
            case SHIFTING:
                if (target.getObject() instanceof Player && ((Player) target.getObject()).isSneaking()) {
                    conditon = !conditon;
                }
                break;
            case LOOKING_DOWN:
                if (target.getObject() instanceof LivingEntity && ((LivingEntity) target.getObject()).getEyeLocation().getPitch() > 70) {
                    conditon = !conditon;
                }
                break;
            case LOOKING_UP:
                if (target.getObject() instanceof LivingEntity && ((LivingEntity) target.getObject()).getEyeLocation().getPitch() < -70) {
                    conditon = !conditon;
                }
                break;
            case TARGET:
                if (target != null && (this.getTargets().isEmpty() || this.getTargets().contains(target.getType()))) {
                    conditon = !conditon;
                }
                break;
            default:
                break;

        }
        return conditon;
    }


    public enum Conditon {
        LOOKING_UP, LOOKING_DOWN, SHIFTING, TARGET
    }

    public enum TargetType {
        CASTER, ORIGIN, TARGET
    }
}
