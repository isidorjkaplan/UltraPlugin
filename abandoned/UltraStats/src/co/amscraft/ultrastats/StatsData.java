package co.amscraft.ultrastats;

import co.amscraft.ultralib.player.PlayerData;
import co.amscraft.ultramagic.MagicData;
import co.amscraft.ultramagic.Spell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Izzy on 2017-11-12.
 */
public class StatsData extends PlayerData {
    public ArrayList<PlayerStat> stats = new ArrayList<>();

    public List<Spell> getSpells() {
        List<Spell> list = new ArrayList<>();
        for (PlayerStat stat : stats) {
            list.addAll(stat.getSpells());
        }
        return list;
    }

    public ArrayList<PlayerStat> getStats() {
        return this.stats;
    }

    public void addStat(PlayerStat stat) {
        if (!hasStat(stat.getStat())) {
            stats.add(stat);
        }
    }

    public void removeStat(Stat stat) {
        for (PlayerStat s : (ArrayList<PlayerStat>) stats.clone()) {
            if (s.getStat().equals(stat)) {
                stats.remove(s);
            }
        }
    }

    public boolean hasStat(Stat stat) {
        for (PlayerStat s : stats) {
            if (s.stat.equals(stat)) {
                return true;
            }
        }
        return false;
    }

    public int getMaxMana() {
        int mana = this.getPlayer().getData(MagicData.class).getMaxMana();
        if (mana == 100) {
            for (PlayerStat stat : this.getStats()) {
                if (stat.getMana() > mana) {
                    mana = stat.getMana();
                }
            }
        }
        return mana;
    }
}
