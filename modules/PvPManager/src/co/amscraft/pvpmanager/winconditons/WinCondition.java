package co.amscraft.pvpmanager.winconditons;

import co.amscraft.pvpmanager.Team;
import co.amscraft.ultralib.utils.ObjectUtils;
import org.bukkit.event.Listener;

public abstract class WinCondition implements Listener {
    public abstract ConditionData newInstance(Team team);

    public WinCondition clone() {
        return (WinCondition) ObjectUtils.clone(this);
    }

    @Override
    public String toString() {
        return ObjectUtils.toString(this);
    }
}
