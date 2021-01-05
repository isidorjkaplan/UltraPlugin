package co.amscraft.pvpmanager;

import co.amscraft.pvpmanager.events.GameEvent;
import co.amscraft.pvpmanager.exceptions.ArenaInUseException;
import co.amscraft.pvpmanager.winconditons.ConditionData;
import co.amscraft.pvpmanager.winconditons.WinCondition;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private static List<Game> games = new ArrayList<>();
    private Arena arena;
    private List<Team> teams = new ArrayList<>();
    private List<Team> quit = new ArrayList<>();
    private boolean started = false;

    public Game(Arena arena) throws ArenaInUseException {
        if (arena.getGame() != null) {
            throw new ArenaInUseException();
        }
        this.arena = arena;
        games.add(this);
    }

    public static Game getGame(Arena arena) {
        return arena.getGame();
    }

    public static List<Game> getGames() {
        return games;
    }

    public static Game getGame(Player player) {
        if (player != null) {
            UltraPlayer uplayer = UltraPlayer.getPlayer(player);
            return uplayer != null ? uplayer.getData(GameData.class).getGame() : null;
        }
        return null;
    }

    public void respawn(Player player) {
        player.teleport(this.getArena().getSpawns().get((int) (this.getArena().getSpawns().size() * Math.random())).getLocation());
    }

    public void respawn() {
        for (int i = 0; i < this.getTeams().size(); i++) {
            this.getTeams().get(i).teleport(this.getArena().getSpawns().get(i % this.getArena().getSpawns().size()).getLocation());
        }
    }

    public void addTeam(Team team) {
        this.getTeams().add(team);
        if (this.hasStarted()) {
            team.respawn();
        } else {
            team.teleport(this.getArena().getLounge().getLocation());
        }
        //this is not done yet
    }

    public void sendStats(CommandSender sender) {
        EditorSettings s = EditorSettings.getSettings(sender);
        sender.sendMessage(s.getVariable() + "Game Stats");
        //sender.sendMessage(s.getVariable() + "Started" + s.getColon() + ": " + s.getValue() + this.hasStarted());
        sender.sendMessage(s.getVariable() + "Arena" + s.getColon() + ": " + s.getValue() + this.getArena().getName());
        Player player = Bukkit.getPlayer(sender.getName());
        if (Game.getGame(player) == this) {
            Team team = this.getTeam(player);
            int deaths;
            if (this.getArena().isTeamLives()) {
                deaths = team.getTeamDeaths();
            } else {
                deaths = team.getDeaths(player);
            }
            sender.sendMessage(s.getVariable() + "Lives" + s.getColon() + ": " + s.getValue() + (this.getArena().getLives() - deaths));
            if (!team.getConditions().isEmpty()) {
                sender.sendMessage(s.getVariable() + "Objectives" + s.getColon() + ": ");
                for (ConditionData data : team.getConditions()) {
                    if (data.getDisplay() != null) {
                        sender.sendMessage(s.getColon() + " - " + s.getValue() + data.getDisplay());
                    }
                }
            }
        }
        sender.sendMessage(s.getVariable() + "Teams" + s.getColon() + ": ");
        for (Team team : this.getTeams()) {
            sender.sendMessage(s.getColon() + " - " + s.getValue() + team);
        }
        if (!this.hasStarted()) {
            sender.sendMessage(s.getError() + "This game has not yet started");
        }
    }

    public void removeTeam(Team team) {
        if (this.getTeams().contains(team)) {
            this.getTeams().remove(team);
            team.teleport(this.getArena().getLounge().getLocation());
            this.getQuit().add(team);
            for (Player player : team.getPlayers()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', EditorSettings.getSettings(player).getError() + this.getArena().getLoseMessage()));
            }
            this.runEvent(GameEvent.Condition.TEAM_QUIT);
            if (this.getTeams().size() <= 1) {
                this.finish();
            }
        }
    }

    public Arena getArena() {
        return arena;
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }

    public List<Team> getQuit() {
        return quit;
    }

    public boolean hasStarted() {
        return started;
    }

    public void runEvent(GameEvent.Condition condition) {
        for (GameEvent evt : this.getArena().getEvents(condition)) {
            evt.run(this);
        }
    }

    public void start() {
        if (!this.hasStarted()) {
            this.respawn();
            this.started = true;
            for (Player player : this.getPlayers()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', EditorSettings.getSettings(player).getHelp() + this.getArena().getStartMessage()));
            }
            for (Team team : this.getTeams()) {
                for (WinCondition condition : this.getArena().getConditions()) {
                    team.addCondition(condition);
                }
            }
            this.runEvent(GameEvent.Condition.GAME_START);
        }
    }

    public void attemptFinish() {
        if (this.isOver()) {
            this.finish();
        }
    }

    public boolean isOver() {
        return this.getWinner() != null;
    }

    public Team getWinner() {
        if (this.getTeams().size() == 1) {
            return this.getTeams().get(0);
        }
        for (Team team : this.getTeams()) {
            if (team.hasWon()) {
                return team;
            }
        }
        return null;
    }

    public void finish() {
        if (this.isOver()) {
            this.runEvent(GameEvent.Condition.GAME_WON);
            for (Player player : this.getWinner().getPlayers()) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', EditorSettings.getSettings(player).getSuccess() + this.getArena().getWinMessage()));
            }
        } else {
            this.runEvent(GameEvent.Condition.GAME_CANCELLED);
        }
        for (Team team : this.getTeams()) {
            team.teleport(this.getArena().getLounge().getLocation());
        }
        getGames().remove(this);
    }

    public List<Player> getPlayers() {
        List<Player> list = new ArrayList<>();
        for (Team team : this.getTeams()) {
            list.addAll(team.getPlayers());
        }
        return list;
    }

    public Team getTeam(Player player) {
        for (Team team : this.getTeams()) {
            if (team.getPlayers().contains(player)) {
                return team;
            }
        }
        return null;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
}
