package co.amscraft.ultramagic.wands;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultramagic.Spell;
import co.amscraft.ultramagic.UltraMagic;
import org.bukkit.inventory.ItemStack;

public class Wand extends UltraObject {
    public WandObject wand = null;
    public String name = "Wand";

    public static Wand getWand(String name) {
        return getObject(Wand.class, "name", name);
    }

    public static Wand getOrCreateWand(String name) {
        if (getWand(name) == null) {
            Wand wand = new Wand();
            wand.name = name;
        }
        return getWand(name);
    }

    public void save(WandObject object) {
        this.wand = object;
        this.save();
    }

    public WandObject getWand() {
        return wand;
    }

    public void convertOldMagic(ItemStack stack) {
        if (UltraMagic.isElmakersEnabled()) {
            com.elmakers.mine.bukkit.api.magic.MagicAPI api = com.elmakers.mine.bukkit.magic.MagicPlugin.getAPI();
            if (api.isWand(stack)) {
                this.wand = new WandObject();
                int i = 0;
                for (String spellName : api.getWand(stack).getSpells()) {
                    if (spellName.startsWith("U-")) {
                        String name = spellName.replaceFirst("U-", "");
                        Spell spell = Spell.getSpell(name);
                        if (spell != null) {
                            this.wand.setSpell(i, spell);
                            i++;
                        }
                    }
                }
                this.save();
            }
        }

    }

    @Override
    public String toString() {
        return this.name;
    }
}
