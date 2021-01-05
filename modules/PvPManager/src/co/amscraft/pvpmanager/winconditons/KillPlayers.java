package co.amscraft.pvpmanager.winconditons;

import co.amscraft.pvpmanager.Game;
import co.amscraft.pvpmanager.Team;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillPlayers extends WinCondition {
    private int kills = 10;

    @EventHandler
    public static void onEntityDeathByEntityEvent(PlayerDeathEvent evt) {
        if (evt.getEntity().getKiller() != null) {
            Player player = (Player) evt.getEntity().getKiller();
            Game game = Game.getGame(player);
            if (game != null) {
                for (KillPlayers killPlayers : game.getArena().getConditions(KillPlayers.class)) {
                    KillPlayersData data = (KillPlayersData) game.getTeam(player).getData(killPlayers);
                    data.setKills(data.getKills() + 1);
                    game.attemptFinish();
                }
            }
        }
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    @Override
    public ConditionData newInstance(Team team) {
        return this.new KillPlayersData(team);
    }

    public class KillPlayersData extends ConditionData {
        private int kills = 0;

        public KillPlayersData(Team team) {
            super(team);
        }

        @Override
        public KillPlayers getCondition() {
            return KillPlayers.this;
        }

        @Override
        public boolean isMet() {
            return this.getKills() >= this.getCondition().getKills();
        }

        @Override
        public String getDisplay() {
            return "Kill " + this.getKills() + "/" + this.getCondition().getKills() + " players";
        }

        public int getKills() {
            return kills;
        }

        public void setKills(int kills) {
            this.kills = kills;
        }
    }
}
