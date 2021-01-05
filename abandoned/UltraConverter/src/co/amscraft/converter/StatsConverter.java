package co.amscraft.converter;

import co.amscraft.UltraPlugin;
import co.amscraft.objects.modules.stats.MagicLevelPath;
import co.amscraft.ultralib.utils.ObjectUtils;
import co.amscraft.ultramagic.Spell;
import co.amscraft.ultrastats.Stat;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by Izzy on 2017-11-22.
 */
public class StatsConverter {
    public static void convertStats() {
        for (MagicLevelPath oldStat : UltraPlugin.getInstance().magicLevelPaths) {
            try {
                Stat stat = new Stat();
                stat.maxLevel = 100;
                stat.minMana = 1;
                stat.maxMana = oldStat.mana[1];
                stat.minMana = oldStat.mana[0];
                stat.name = oldStat.name.replace("Child", "").replace(" ", "");
                for (int i : oldStat.spells.keySet()) {
                    int level = oldStat.spells.get(i);
                    co.amscraft.objects.modules.magic.Spell oldSpell = co.amscraft.objects.modules.magic.Spell.getSpell(i);
                    List<Spell> list = stat.spells.getOrDefault(level, new ArrayList<>());
                    list.add(Spell.getSpell(oldSpell.getName()));
                    stat.spells.put(level, list);
                }
                stat.save();
                ObjectUtils.debug(Level.WARNING, "Successfully converted Stat: " + stat.name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
