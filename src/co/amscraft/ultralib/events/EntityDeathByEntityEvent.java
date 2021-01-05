package co.amscraft.ultralib.events;

import co.amscraft.ultralib.events.utility.UltraEvent;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathByEntityEvent extends UltraEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private EntityDamageByEntityEvent damage;
    private EntityDeathEvent event;

    public EntityDeathByEntityEvent(EntityDeathEvent evt) {
        this.event = event;
        if (evt.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
            this.damage = (EntityDamageByEntityEvent) evt.getEntity().getLastDamageCause();
        }
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public EntityDamageByEntityEvent getDamageEvent() {
        return damage;
    }

    public EntityDeathEvent getDeath() {
        return this.event;
    }
}
