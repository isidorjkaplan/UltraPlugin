package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.ParentAction;
import org.bukkit.util.Vector;


public class LookingAt extends ParentAction {
    @FieldDescription(help = "The value of the delta dirrections that still qualifies as true", unit = "degrees (0 to 180)")
    private int width = 30;
    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        Vector vector = caster.getLocation().toVector().subtract(target.getLocation().toVector());
        vector = vector.normalize();
        Vector facing = target.getEyeLocation().getDirection().normalize();
        double length = vector.subtract(facing).length()*90;// / ();
        //System.out.println(length);
        if (length < width) {
            this.runActions(spell, target, caster);
        }
    }
    /*public static void main(String[] args) {
        Vector v1 = new Vector(0, 0, 1).normalize();
        Vector v2 = new Vector(0, 1, 1).normalize();
        double length = v1.subtract(v2).length();
        System.out.println(length);
    }*/
}
