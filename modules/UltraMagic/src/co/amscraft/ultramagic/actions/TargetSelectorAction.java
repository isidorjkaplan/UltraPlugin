package co.amscraft.ultramagic.actions;

import co.amscraft.ultralib.editor.FieldDescription;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Izzy on 2017-10-15.
 */
public abstract class TargetSelectorAction extends ParentAction {

    @FieldDescription(help = "The valid target types that this can select")
    public List<TargetType> targetTypes = new ArrayList<>();
    @FieldDescription(help = "Weather or not this can select the caster of the spell!")
    public boolean protectSpellCaster = true;

    public List<Class<?>> getTargets() {
        List<Class<?>> list = new ArrayList<>();
        for (TargetType t : targetTypes) {
            switch (t) {
                case LOCATION:
                    list.add(Location.class);
                    break;
                case BLOCK:
                    list.add(Block.class);
                    break;
                case LIVING_ENTITY:
                    list.add(LivingEntity.class);
                    break;
                case PLAYER:
                    list.add(Player.class);
                    break;
                case ENTITY:
                    list.add(Entity.class);
                    break;
            }
        }
        return list;
    }

    @Override
    public boolean isAsyncThread() {
        return true;
    }


    public boolean hasTarget(Class<?> type) {
        for (Class<?> t : getTargets()) {
            if (t.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    public enum TargetType {
        PLAYER, LIVING_ENTITY, ENTITY, BLOCK, LOCATION
    }
}
