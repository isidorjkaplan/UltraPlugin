package co.amscraft.ultramagic.main.actions;


import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.ParentAction;

/**
 * Created by Izzy on 2017-06-12.
 */
public class Repeat extends ParentAction {
    @FieldDescription(help = "The amount of times the sub-actions will run")
    public int count = 1;

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        for (int i = 0; i < count; i++) {
            runActions(spell, target, caster);
        }
    }

    @Override
    public boolean isAsyncThread() {
        return true;
    }
}
