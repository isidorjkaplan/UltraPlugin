package co.amscraft.pvpmanager.events;

import co.amscraft.pvpmanager.Game;

public abstract class GameEvent {
    //@FieldDescription(help = "You can use the flags <victors> <losers> <players> to make an action cycle for each of the people who meet the requirement")
    private Condition condition = Condition.GAME_START;

    public abstract void run(Game game);

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public enum Condition {
        GAME_START, GAME_WON, GAME_CANCELLED, TEAM_QUIT
    }
}
