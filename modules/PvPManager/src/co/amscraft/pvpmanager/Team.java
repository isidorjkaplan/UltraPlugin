package co.amscraft.pvpmanager;

import co.amscraft.pvpmanager.winconditons.ConditionData;
import co.amscraft.pvpmanager.winconditons.WinCondition;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class Team {
    //private List<UUID> players = new ArrayList<>();
    private Map<UUID, Integer> deaths = new HashMap<>();
    private List<ConditionData> conditions = new ArrayList<>();
    private Game game = null;

    public Team(Player player, Game game) {
        this.game = game;
        game.addTeam(this);
        this.addPlayer(player);
    }

    public List<Player> getPlayers() {
        List<Player> list = new ArrayList<>();
        for (UUID uuid : this.deaths.keySet()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                list.add(player);
            }
        }
        return list;
    }

    public void addPlayer(Player player) {
        if (this.getGame().getTeam(player) == null) {
            deaths.put(player.getUniqueId(), 0);
            player.teleport(this.getGame().getArena().getLounge().getLocation());
        }
    }

    public Game getGame() {
        if (game == null) {
            for (Game game : Game.getGames()) {
                if (game.getTeams().contains(this)) {
                    this.game = game;
                    break;
                }
            }
        }
        return game;
    }

    public List<ConditionData> getConditions() {
        return conditions;
    }

    public ConditionData getData(WinCondition condition) {
        for (ConditionData data : this.getConditions()) {
            if (data.getCondition() == condition) {
                return data;
            }
        }
        return null;
    }

    public void addCondition(WinCondition condition) {
        this.getConditions().add(condition.newInstance(this));
    }

    public ConditionData getCondition(WinCondition condition) {
        for (ConditionData data : this.getConditions()) {
            if (data.getCondition() == condition) {
                return data;
            }
        }
        return null;
    }

    public boolean hasWon() {
        return this.getCondition(this.getGame().getArena().getCondition()).isMet();
    }

    public void addDeath(Player player) {
        this.getDeaths().put(player.getUniqueId(), getDeaths(player) + 1);
        this.attemptLoss();
    }

    public void attemptLoss() {
        if (this.getGame().getArena().isTeamLives()) {
            if (this.getTeamDeaths() >= this.getGame().getArena().getLives()) {
                this.getGame().removeTeam(this);
            }
        } else {
            for (Player player : this.getPlayers()) {
                if (this.getDeaths(player) >= this.getGame().getArena().getLives()) {
                    this.removePlayer(player);
                    if (this.getPlayers().isEmpty()) {
                        break;
                    }
                }
            }
        }
    }

    public int getTeamDeaths() {
        int lives = 0;
        for (UUID uuid : this.getDeaths().keySet()) {
            lives += this.getDeaths().get(uuid);
        }
        return lives;
    }

    public void removePlayer(Player player) {
        this.getDeaths().remove(player.getUniqueId());
        if (this.getPlayers().isEmpty()) {
            this.getGame().removeTeam(this);
        }
    }

    public void teleport(Location location) {
        for (Player player : this.getPlayers()) {
            player.teleport(location);
        }
    }

    public void respawn() {
        this.teleport(this.getGame().getArena().getSpawns().get((int) (this.getGame().getArena().getSpawns().size() * Math.random())).getLocation());
    }


    public int getDeaths(Player player) {
        return this.getDeaths().get(player.getUniqueId());
    }


    public Map<UUID, Integer> getDeaths() {
        return deaths;
    }

    @Override
    public String toString() {
        List<String> list = new ArrayList<>();
        for (Player player : this.getPlayers()) {
            list.add(player.getName());
        }
        return list.toString();
    }
}
