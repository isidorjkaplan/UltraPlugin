package co.amscraft.pvpmanager.winconditons;

import co.amscraft.pvpmanager.Team;

public class AnyCondition extends ParentCondition {
    @Override
    public ConditionData newInstance(Team team) {
        return this.new AllConditionsData(team);
    }

    public class AllConditionsData extends ConditionData {

        public AllConditionsData(Team team) {
            super(team);
        }

        @Override
        public AnyCondition getCondition() {
            return AnyCondition.this;
        }

        @Override
        public boolean isMet() {
            for (WinCondition condition : this.getCondition().getConditions()) {
                ConditionData data = this.getTeam().getData(condition);
                if (data == null || data.isMet()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public String getDisplay() {
            return null;
        }
    }
}
