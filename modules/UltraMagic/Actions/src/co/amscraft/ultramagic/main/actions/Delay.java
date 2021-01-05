package co.amscraft.ultramagic.main.actions;


import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;

/**
 * Created by Izzy on 2017-06-03.
 */
public class Delay extends Action {
    @FieldDescription(help = "The amount of time the delay will last for", unit = "seconds")
    public double delay = 1; //in seconds

    @Override
    public void run(SpellInstance context, Target target, Target caster) {
        //this.playEffects(target);
        try {
            Thread.sleep(Math.round((delay * 1000)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //nextAction(context.target, context.origin, this, context.spellInstance);
    }

    @Override
    public boolean isAsyncThread() {
        return true;
    }
}
