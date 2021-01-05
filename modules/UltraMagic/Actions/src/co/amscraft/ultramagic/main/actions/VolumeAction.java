package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.TargetSelectorAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Izzy on 2017-06-23.
 */
public class VolumeAction extends TargetSelectorAction {
    @FieldDescription(help = "The radius in a directional plane")
    public int x, y, z;
    @Override
    public boolean isAsyncThread() {
        return false;
    }
    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        Target origin = target != null ? target : caster;
        List<Target> targets = new ArrayList<>();
        for (Class<?> type : getTargets()) {
            try {
                targets.addAll(Arrays.asList(Target.getTargetsInBox(type, origin.getLocation(), x, y, z)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Target t : targets) {
            runActions(spell, t, target);
        }
    }
}
