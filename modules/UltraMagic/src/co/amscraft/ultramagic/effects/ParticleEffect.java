package co.amscraft.ultramagic.effects;

import co.amscraft.ultralib.editor.FieldDescription;
import org.bukkit.Particle;

import java.util.Arrays;

/**
 * Created by Izzy on 2017-10-18.
 */
public abstract class ParticleEffect extends EffectAction {
    private static Particle[] coloured = {Particle.REDSTONE, Particle.SPELL_WITCH, Particle.FIREWORKS_SPARK, Particle.SPELL_MOB, Particle.SPELL_INSTANT, Particle.SPELL_MOB_AMBIENT, Particle.NOTE};
    @FieldDescription(help = "The particle which is being set")
    public Particle particle = Particle.REDSTONE;
    @FieldDescription(help = "The radius of the effect")
    public double radius = 10;
    @FieldDescription(help = "The count of particles")
    public double count = 5;
    @FieldDescription(help = "The speed the particle lasts for")
    public double speed = 0.05;
    @FieldDescription(help = "The red color (if particle has RGB enabled)")
    public double R = 0;
    @FieldDescription(help = "The green color (if particle has RGB enabled)")
    public double G = 0;
    @FieldDescription(help = "The blue color (if particle has RGB enabled)")
    public double B = 0;

    public static boolean isColoured(Particle particle) {
        return (Arrays.asList(coloured).contains(particle));
    }


}
