package co.amscraft.ultramagic.actions;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.effects.EffectAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Izzy on 2017-10-15.
 */
public abstract class Action {
    @FieldDescription(help = "The particles/sounds that are played when this action is played")
    public List<EffectAction> effects = new ArrayList<>();

    @FieldDescription(help = "Weather or not you want this task to run in its own private thread!")
    public boolean isAsync = false;


    public abstract void run(SpellInstance spell, Target target, Target caster);

    /**
     * @return Weather or not the action is okay to run in its own thread. If false it will run on the main spigot thread.
     */
    public boolean isAsyncThread() {
        return false;
    }

    public void playEffects(Target target) {
        for (EffectAction effect : this.effects) {
            effect.play(target);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }


}
