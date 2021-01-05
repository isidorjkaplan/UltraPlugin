package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.ParentAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.metadata.MetadataValueAdapter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class SummonMob extends ParentAction implements Listener {


    public EntityType entity = EntityType.ZOMBIE;

    @FieldDescription(help = "The name of the mob")
    public String name = null;
    @FieldDescription(help = "How long it lasts for, set to -1 to last forever", unit = "seconds")
    public double duration = -1;

    public double health = 20;

    public boolean AI = true;

    @FieldDescription(help = "Weather or not this can select the caster of the spell!")
    public boolean protectSpellCaster = true;

    public boolean tamed = true;

    @FieldDescription(help = "The equipment used by the summoned mob if it contains equipment")
    public GiveItem.Item hand = new GiveItem.Item(), chestplate = new GiveItem.Item(), leggings = new GiveItem.Item(), boots = new GiveItem.Item(), helmet = new GiveItem.Item();

    private static boolean isNull(GiveItem.Item item) {
        return item == null || item.getItem() == null;
    }

    @EventHandler
    public void onEntityTargetChange(EntityTargetLivingEntityEvent evt) {
        if (evt.getEntity() != null && evt.getEntity().hasMetadata("summoned")) {
            try {
                LivingEntity caster = (LivingEntity) Bukkit.getEntity((UUID) evt.getEntity().getMetadata("summoned").get(0).value());
                if (evt.getTarget().equals(caster)) {
                    evt.setCancelled(true);
                }
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        Entity entity = target.getLocation().getWorld().spawnEntity(target.getLocation(), this.entity);
        entity.setCustomName(name);
        if (entity instanceof LivingEntity) {
            ((LivingEntity) entity).setAI(AI);
            ((LivingEntity) entity).setMaxHealth(health);
            ((LivingEntity) entity).setHealth(health);
            if (entity instanceof Monster) {
                if (target.getObject() instanceof LivingEntity && !target.getObject().equals(spell.CASTER.getObject())) {
                    ((Monster) entity).setTarget((LivingEntity) target.getObject());
                }
                if (protectSpellCaster && spell.CASTER.getObject() instanceof LivingEntity) {
                    entity.setMetadata("summoned", new MetadataValueAdapter(UltraLib.getInstance()) {
                        @Override
                        public Object value() {
                            return ((LivingEntity) spell.CASTER.getObject()).getUniqueId();
                        }

                        @Override
                        public void invalidate() {

                        }
                    });
                }
            }
            if (entity instanceof Tameable) {
                ((Tameable) entity).setTamed(tamed);
                if (spell.CASTER.getObject() instanceof Player) {
                    ((Tameable) entity).setOwner(((Player) spell.CASTER.getObject()));
                }
            }
            if (!isNull(helmet)) {
                ((LivingEntity) entity).getEquipment().setHelmet(helmet.getItem());
            }
            if (!isNull(chestplate)) {
                ((LivingEntity) entity).getEquipment().setChestplate(chestplate.getItem());
            }
            if (!isNull(leggings)) {
                ((LivingEntity) entity).getEquipment().setLeggings(leggings.getItem());
            }
            if (!isNull(boots)) {
                ((LivingEntity) entity).getEquipment().setBoots(boots.getItem());
            }
            if (!isNull(hand)) {
                ((LivingEntity) entity).getEquipment().setItemInMainHand(hand.getItem());
            }
        }
        if (duration >= 0) {
            new BukkitRunnable() {
                public void run() {
                    try {
                        entity.remove();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }.runTaskLater(UltraLib.getInstance(), Math.round(20 * duration));
        }
        try {
            this.runActions(spell, new Target(entity), caster);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
