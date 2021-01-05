package co.amscraft.pvpmanager.winconditons;

import co.amscraft.pvpmanager.Team;

public class AllConditions extends ParentCondition {
    @Override
    public ConditionData newInstance(Team team) {
        return this.new AllConditionsData(team);
    }

    public class AllConditionsData extends ConditionData {

        public AllConditionsData(Team team) {
            super(team);
        }

        @Override
        public AllConditions getCondition() {
            return AllConditions.this;
        }

        @Override
        public boolean isMet() {
            for (WinCondition condition : this.getCondition().getConditions()) {
                ConditionData data = this.getTeam().getData(condition);
                if (data != null && !data.isMet()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public String getDisplay() {
            return null;
        }
    }
}
