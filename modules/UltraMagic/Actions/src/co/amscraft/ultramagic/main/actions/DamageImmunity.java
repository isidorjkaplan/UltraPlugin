package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DamageImmunity extends Action implements Listener {
    private static HashMap<UUID, List<ImmunityData>> data = new HashMap<>();
    @FieldDescription(unit = "seconds", help = "How long they will be immune to damage for")
    public double duration = 10;
    @FieldDescription(help = "A list of all the damage causes they will be immune to")
    public List<EntityDamageEvent.DamageCause> causes = new ArrayList<>();

    private static void prune() {
        new Thread() {
            @Override
            public void run() {
                List<UUID> entities = new ArrayList<>();
                entities.addAll(data.keySet());
                long time = System.currentTimeMillis();
                for (UUID uuid : entities) {
                    List<ImmunityData> list = data.get(uuid);
                    if (list != null && !list.isEmpty()) {
                        List<ImmunityData> immunityDataListCopy = new ArrayList<>();
                        immunityDataListCopy.addAll(list);
                        for (ImmunityData d : immunityDataListCopy) {
                            if (d.until < time) {
                                list.remove(d);
                            }
                        }
                        if (list.isEmpty()) {
                            data.remove(uuid);
                        } else {
                            data.put(uuid, list);
                        }
                    }
                }
            }
        }.start();
    }

    private void addImmunity(LivingEntity entity, EntityDamageEvent.DamageCause cause) {
        prune();
        List<ImmunityData> list = data.getOrDefault(entity.getUniqueId(), new ArrayList<>());
        boolean updated = false;
        long until = System.currentTimeMillis() + Math.round((duration * 1000));
        for (ImmunityData data : list) {
            if (data.cause == cause) {
                data.until = data.until > until ? data.until : until;
                updated = true;
                break;
            }
        }
        if (!updated) {
            list.add(new ImmunityData(until, cause));
            data.put(entity.getUniqueId(), list);
        }
    }

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        if (target != null && target.getObject() instanceof LivingEntity) {
            LivingEntity e = (LivingEntity) target.getObject();
            prune();
            for (EntityDamageEvent.DamageCause cause : causes) {
                addImmunity(e, cause);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent evt) {
        prune();
        //System.out.println(data);
        Entity e = evt.getEntity();
        if (data.containsKey(e.getUniqueId())) {
            for (ImmunityData i : data.get(e.getUniqueId())) {
                if (i.cause == evt.getCause()) {
                    evt.setCancelled(true);
                }
            }
        }

    }

    private class ImmunityData {
        public long until;
        public EntityDamageEvent.DamageCause cause;

        public ImmunityData(long until, EntityDamageEvent.DamageCause cause) {
            this.until = until;
            this.cause = cause;
        }

        public String toString() {
            return cause + "[" + new DecimalFormat("#.00").format((until - System.currentTimeMillis()) / 1000.0) + " seconds remaining]";
        }
    }
}
