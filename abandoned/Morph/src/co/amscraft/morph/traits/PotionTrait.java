package co.amscraft.morph.traits;

import co.amscraft.morph.Trait;
import co.amscraft.ultralib.editor.FieldDescription;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionTrait extends Trait {
    private PotionEffectType type;
    private int power;
    @FieldDescription(save = false, show = false)
    private PotionEffect effect;

    @Override
    public void onMorph(Player player) {
        player.addPotionEffect(this.getEffect());
    }

    @Override
    public void onUnmorph(Player player) {
        player.removePotionEffect(this.getType());
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public PotionEffect getEffect() {
        if (effect == null) {
            effect = new PotionEffect(type, Integer.MAX_VALUE, power);
        }
        return effect;
    }

    public PotionEffectType getType() {
        return type;
    }

    public void setType(PotionEffectType type) {
        this.type = type;
    }
}
