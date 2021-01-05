package co.amscraft.ultramagic.effects;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.Target;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Created by Izzy on 2017-10-18.
 */
public abstract class EffectAction {
    @FieldDescription(help = "The amount of time the effect will remain", unit = "Seconds")
    public double lifetime = 0.5;

    public void play(Target target) {
        if (target != null) {
            final long start = System.currentTimeMillis();
            play(start, target);
        }
    }

    private void play(long start, Target target) {
        new BukkitRunnable() {
            public void run() {
                EffectAction.this.run(target);
                if (start + (lifetime * 1000) > System.currentTimeMillis()) {
                    play(start, target);
                }
            }
        }.runTaskLater(UltraLib.getInstance(), (long) 1);
    }

    protected abstract void run(Target target);

    public String toString() {
        String string = this.getClass().getSimpleName() + "(";
        for (Field field : this.getClass().getFields()) {
            field.setAccessible(true);
            if (!Modifier.isStatic(field.getModifiers())) {
                try {
                    string += field.getName() + "=" + field.get(this) + ", ";
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        string += ")";
        return string;
    }
}
