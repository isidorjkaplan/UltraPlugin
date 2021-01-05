package co.amscraft.ultramagic.actions;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Izzy on 2017-10-15.
 */
public abstract class ParentAction extends Action {
    @FieldDescription(help = "The sub actions of this action")
    public List<Action> actions = new ArrayList<>();

    public void runActions(SpellInstance spell, Target target, Target caster) {
        spell.runActions(actions, target, caster);
    }
}
