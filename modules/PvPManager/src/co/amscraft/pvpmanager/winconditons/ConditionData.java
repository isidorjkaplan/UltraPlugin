package co.amscraft.pvpmanager.winconditons;

import co.amscraft.pvpmanager.Team;

public abstract class ConditionData {
    private final Team team;

    public ConditionData(Team team) {
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    public abstract WinCondition getCondition();

    public abstract boolean isMet();

    public abstract String getDisplay();
}
