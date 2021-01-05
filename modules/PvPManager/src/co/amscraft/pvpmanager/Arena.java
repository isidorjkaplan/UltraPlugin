package co.amscraft.pvpmanager;

import co.amscraft.pvpmanager.events.GameEvent;
import co.amscraft.pvpmanager.shapes.Location;
import co.amscraft.pvpmanager.shapes.Shape;
import co.amscraft.pvpmanager.winconditons.AllConditions;
import co.amscraft.pvpmanager.winconditons.ParentCondition;
import co.amscraft.pvpmanager.winconditons.WinCondition;
import co.amscraft.ultralib.UltraObject;

import java.util.ArrayList;
import java.util.List;

public class Arena extends UltraObject {
    private Shape range = null;
    private String name = "Arena";
    private boolean allowExit = false;
    private boolean allowSpells = true;
    private boolean protectTeam = true;
    private int maxTeams = 2;
    private int maxPlayersOnTeam = 4;
    private int lives = 5;
    private boolean teamLives = false;
    private WinCondition condition = new AllConditions();
    private List<GameEvent> events = new ArrayList<>();
    private String winMessage = "You have won the game";
    private String loseMessage = "You have  lost";
    private String startMessage = "The game has started";
    private Location lounge = new Location();
    private List<Location> spawns = new ArrayList<>();

    public static Arena getArena(String name) {
        if (name == null) {
            return null;
        }
        for (Arena arena : getList(Arena.class)) {
            if (arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }

    public List<GameEvent> getEvents(GameEvent.Condition condition) {
        List<GameEvent> events = new ArrayList<>();
        for (GameEvent event : this.getEvents()) {
            if (event.getCondition() == condition) {
                events.add(event);
            }
        }
        return events;
    }

    public <T extends WinCondition> List<T> getConditions(Class<T> type) {
        List<T> list = new ArrayList<>();
        for (WinCondition win : this.getConditions()) {
            if (type.isAssignableFrom(win.getClass())) {
                list.add((T) win);
            }
        }
        return list;
    }

    public String getStartMessage() {
        return startMessage;
    }

    public void setStartMessage(String startMessage) {
        this.startMessage = startMessage;
    }

    public String getLoseMessage() {
        return loseMessage;
    }

    public void setLoseMessage(String loseMessage) {
        this.loseMessage = loseMessage;
    }

    public String getWinMessage() {
        return winMessage;
    }

    public void setWinMessage(String winMessage) {
        this.winMessage = winMessage;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public List<WinCondition> getConditions() {
        List<WinCondition> conditions;
        if (this.getCondition() instanceof ParentCondition) {
            conditions = ((ParentCondition) this.getCondition()).getConditionsRecursivly();
        } else {
            conditions = new ArrayList<>();
        }
        conditions.add(this.getCondition());
        return conditions;
    }

    public Game getGame() {
        for (Game game : Game.getGames()) {
            if (game.getArena() == this) {
                return game;
            }
        }
        return null;
    }

    public Shape getShape() {
        return getRange();
    }

    public Shape getRange() {
        return range;
    }

    public void setRange(Shape range) {
        this.range = range;
    }

    public boolean isAllowExit() {
        return allowExit;
    }

    public void setAllowExit(boolean allowExit) {
        this.allowExit = allowExit;
    }

    public boolean isAllowingSpells() {
        return allowSpells;
    }

    public void setAllowSpells(boolean allowSpells) {
        this.allowSpells = allowSpells;
    }

    public boolean isProtectingTeam() {
        return protectTeam;
    }

    public void setProtectTeam(boolean protectTeam) {
        this.protectTeam = protectTeam;
    }

    public WinCondition getCondition() {
        return condition;
    }

    public void setCondition(WinCondition condition) {
        this.condition = condition;
    }

    public List<GameEvent> getEvents() {
        return events;
    }

    public void setEvents(List<GameEvent> events) {
        this.events = events;
    }

    public Location getLounge() {
        return lounge;
    }

    public void setLounge(Location lounge) {
        this.lounge = lounge;
    }

    public int getMaxTeams() {
        return maxTeams;
    }

    public void setMaxTeams(int maxTeams) {
        this.maxTeams = maxTeams;
    }

    public boolean isTeamLives() {
        return teamLives;
    }

    public void setTeamLives(boolean teamLives) {
        this.teamLives = teamLives;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public int getMaxPlayersOnTeam() {
        return maxPlayersOnTeam;
    }

    public void setMaxPlayersOnTeam(int maxPlayersOnTeam) {
        this.maxPlayersOnTeam = maxPlayersOnTeam;
    }

    public List<Location> getSpawns() {
        return spawns;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
