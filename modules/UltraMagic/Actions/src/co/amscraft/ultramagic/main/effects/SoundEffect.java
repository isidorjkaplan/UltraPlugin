package co.amscraft.ultramagic.main.effects;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.effects.EffectAction;

/**
 * Created by Izzy on 2017-10-27.
 */
public class SoundEffect extends EffectAction {
    @FieldDescription(help = "https://www.digminecraft.com/lists/sound_list_pc.php")
    public String sound = "";
    public int volume = 1;
    public float radius = 10;

    @Override
    protected void run(Target target) {
        target.getLocation().getWorld().playSound(target.getLocation(), sound, volume, (float) radius);
    }
}
