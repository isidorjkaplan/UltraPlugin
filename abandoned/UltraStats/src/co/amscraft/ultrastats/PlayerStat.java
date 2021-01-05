package co.amscraft.ultrastats;

import co.amscraft.ultralib.player.PlayerUtility;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultramagic.MagicData;
import co.amscraft.ultramagic.Spell;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.UUID;

/**
 * Created by Izzy on 2017-11-12.
 */
public class PlayerStat {
    public Stat stat = null;
    public int level = 1;
    public double exp = 0;
    private UUID uuid = null;

    public PlayerStat() {

    }


    public PlayerStat(UltraPlayer player, Stat stat) {
        StatsData data = player.getData(StatsData.class);
        for (PlayerStat pd : (List<PlayerStat>) data.getStats().clone()) {
            if (pd.getStat().equals(stat)) {
                data.getStats().remove(pd);
            }

        }
        data.addStat(this);
        this.stat = stat;
        this.uuid = player.getId();
    }


    public Stat getStat() {
        return this.stat;
    }

    public List<Spell> getSpells() {
        return this.getStat().getSpells(this.level);
    }

    public UltraPlayer getPlayer() {
        if (this.uuid == null) {
            for (UltraPlayer player : UltraPlayer.getPlayers()) {
                if (player.hasData(StatsData.class) && player.getData(StatsData.class).getStats().contains(this)) {
                    this.uuid = player.getId();
                    break;
                }
            }
        }
        return UltraPlayer.getPlayer(this.uuid);
    }

    public int getMana() {
        return (int) Math.round(this.getStat().minMana + ((this.getStat().maxMana - this.getStat().minMana) * (this.level / (double) this.getStat().maxLevel)));
    }

    public double getEntityEXPYield(LivingEntity entity) {
        if (this.getStat() != null && entity != null) {
            double exp = Stat.getDefaultExpYields().getOrDefault(entity.getType(), 50.0);
            if (entity.isDead()) {
                exp *= this.getStat().killMultiplier;
            }
            return exp;
        }
        return 1;

    }


    public void onLevelUp() {
        this.getPlayer().getData(PlayerUtility.class).sendActionbar("§eYou have levelled up to level§6: §7" + level, 5);
        int maxMana = 0;
        for (PlayerStat stat : getPlayer().getData(StatsData.class).getStats()) {
            int mana = stat.getStat().getMana(stat.level);
            if (mana > maxMana && mana != 0) {
                maxMana = mana;
            }
        }
        if (maxMana > this.getPlayer().getData(MagicData.class).getMaxMana()) {
            this.getPlayer().getData(MagicData.class).setMaxMana(maxMana);
        }
        for (Spell spell : this.getSpells()) {
            if (!this.getPlayer().getData(MagicData.class).hasSpell(spell)) {
                this.getPlayer().getData(PlayerUtility.class).sendActionbar("§eYou have unlocked spell§6: §7" + spell.getName(), 5);
            }
        }
    }


    public double getLevelEXP(int level) {
        return Math.round((4 * (Math.pow(level, 3))) / 5);
    }

    public void addExp(double exp) {
        if (this.level < 100) {
            this.exp += exp;
            while (this.exp >= getLevelEXP(level)) {
                this.exp -= getLevelEXP(level);
                level++;
                if (level >= 100) {
                    this.exp = 0;
                }
                this.onLevelUp();
            }
        }
    }

    public double getExpFrom(int level, double base, int split) {
        float a;
        //int b = base; //20 to 350 depending on conditons
        //int s = split; //number of pokemon in the battle

        return (((base * level) / (5 * split)) * (Math.pow((2 * level) + 10, 2.5) / Math.pow(level + this.level + 10, 2.5)) + 1) + (base != 0 ? 0.5 * Math.pow(level, 2) : 0);
    }

    public double getExpTillLevel() {
        return getLevelEXP(level) - exp;
    }

}
