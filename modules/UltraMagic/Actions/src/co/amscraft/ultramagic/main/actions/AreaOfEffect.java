package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.TargetSelectorAction;

/**
 * Created by Izzy on 2017-10-15.
 */
public class AreaOfEffect extends TargetSelectorAction {
    @FieldDescription(help = "The radius of the area of effect", unit = "blocks")
    public double radius = 5;

    @Override
    public boolean isAsyncThread() {
        return false;
    }

    @Override
    public void run(SpellInstance spell, Target origin, Target caster) {
        if (origin != null) {
            try {
                Target[] targets;
                if (protectSpellCaster) {
                    targets = Target.getTargets(getTargets(), origin.getLocation().clone(), this.radius, spell.CASTER);
                } else {
                    targets = Target.getTargets(getTargets(), origin.getLocation().clone(), this.radius);
                }
                for (Target target : targets) {
                    this.runActions(spell, target, origin);
                }
            } catch (Exception e) {
                //ObjectUtils.debug(Level.INFO, "Failed to find targets in action AOE from spell: " + spell.getSpell());
                //e.printStackTrace();
            }
        }
    }
}
