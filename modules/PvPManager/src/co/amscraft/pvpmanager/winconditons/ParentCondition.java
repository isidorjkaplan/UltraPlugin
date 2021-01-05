package co.amscraft.pvpmanager.winconditons;

import java.util.ArrayList;
import java.util.List;

public abstract class ParentCondition extends WinCondition {
    private List<WinCondition> conditions = new ArrayList<>();

    public List<WinCondition> getConditions() {
        return conditions;
    }

    public List<WinCondition> getConditionsRecursivly() {
        List<WinCondition> conditions = new ArrayList<>();
        for (WinCondition condition : this.getConditions()) {
            conditions.add(condition);
            if (condition instanceof ParentCondition) {
                conditions.addAll(((ParentCondition) condition).getConditionsRecursivly());
            }
        }
        return conditions;
    }
}
