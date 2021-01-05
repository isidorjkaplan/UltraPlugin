package co.amscraft.pvpmanager.winconditons;

import co.amscraft.pvpmanager.Game;
import co.amscraft.pvpmanager.Team;
import co.amscraft.ultralib.events.EntityDeathByEntityEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

public class KillMobs extends WinCondition {
    private List<EntityType> mobTypes = new ArrayList<>();
    private int kills = 10;

    @EventHandler
    public static void onEntityDeathByEntityEvent(EntityDeathByEntityEvent evt) {
        if (evt.getDamageEvent().getDamager() instanceof Player) {
            Player player = (Player) evt.getDamageEvent().getDamager();
            Game game = Game.getGame(player);
            if (game != null) {
                for (KillMobs killMobs : game.getArena().getConditions(KillMobs.class)) {
                    KillMobsData data = (KillMobsData) game.getTeam(player).getData(killMobs);
                    data.setKills(data.getKills() + 1);
                    game.attemptFinish();
                }
            }
        }
    }

    public List<EntityType> getMobTypes() {
        return mobTypes;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    @Override
    public ConditionData newInstance(Team team) {
        return this.new KillMobsData(team);
    }

    public class KillMobsData extends ConditionData {
        private int kills = 0;

        public KillMobsData(Team team) {
            super(team);
        }

        @Override
        public KillMobs getCondition() {
            return KillMobs.this;
        }

        @Override
        public boolean isMet() {
            return this.getKills() >= this.getCondition().kills;
        }

        @Override
        public String getDisplay() {
            return "Kill " + this.getKills() + "/" + this.getCondition().getKills() + " " + this.getCondition().getMobTypes();
        }

        public int getKills() {
            return kills;
        }

        public void setKills(int kills) {
            this.kills = kills;
        }
    }

}
