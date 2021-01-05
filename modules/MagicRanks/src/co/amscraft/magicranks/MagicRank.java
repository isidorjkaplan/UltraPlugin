package co.amscraft.magicranks;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.Spell;

import java.util.ArrayList;
import java.util.List;

public class MagicRank extends UltraObject {
    @FieldDescription(help = "The group that gets spells from this rank")
    public List<String> ranks = new ArrayList<>();
    @FieldDescription(help = "A list of spells that this rank has access to by default")
    public ArrayList<Spell> spells = new ArrayList<>();
    @FieldDescription(help = "The maximum amount of mana that people of this rank can get. Note: The highest out of all their ranks overrides")
    public int maxMana = -1;

    public static MagicRank getRank(String group) {
        for (MagicRank rank : getList(MagicRank.class)) {
            for (String pex : rank.getRanks()) {
                if (pex.equalsIgnoreCase(group)) {
                    return rank;
                }
            }
        }
        return null;
    }

    public int getMaxMana() {
        return this.maxMana;
    }

    public List<Spell> getSpells() {
        return this.spells;
    }

    public List<String> getRanks() {
        return this.ranks;
    }

    @Override
    public String toString() {
        return ranks.toString();
    }
}
