package co.amscraft.morph;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.Spell;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class Morph extends UltraObject {
    private EntityType mob = EntityType.SHEEP;
    private Set<Trait> powers = new HashSet<>();
    private String name = "Morph";
    private Set<Spell> spells = new HashSet<>();
    @FieldDescription(save = false, show = false)
    private MobDisguise disguise;

    public static Morph getMorph(String name) {
        for (Morph morph : getList(Morph.class)) {
            if (morph.getName().equalsIgnoreCase(name)) {
                return morph;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    public Set<Trait> getPowers() {
        return powers;
    }

    public Set<Spell> getSpells() {
        return spells;
    }

    public MobDisguise getDisguise() {
        if (disguise == null) {
            this.disguise = new MobDisguise(DisguiseType.getType(mob));
        }
        return disguise;
    }

    public void activate(Player player) {
        DisguiseAPI.disguiseEntity(player, this.getDisguise());
        for (Trait trait : powers) {
            trait.onMorph(player);
        }
    }

    public void disable(Player player) {
        DisguiseAPI.undisguiseToAll(player);
        for (Trait trait : powers) {
            trait.onUnmorph(player);
        }
    }
}
