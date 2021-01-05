package co.amscraft.ultramagic.main.actions;


import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.utils.NMSUtils;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.SpellThread;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.AbstractProjectileAction;
import co.amscraft.ultramagic.actions.TargetSelectorAction;
import co.amscraft.ultramagic.exceptions.InvalidTargetException;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Created by Izzy on 2017-06-16.
 */
public class ProjectileAction extends TargetSelectorAction implements Listener {
    private static HashMap<Entity, ProjectileAction> actions = new HashMap<>();
    private static HashMap<Entity, Target> targetsMap = new HashMap<>();
    @FieldDescription(help = "The type of projectile that should be fired! Put \"DROPPED_ITEM\" for custom item!")
    public EntityType type = EntityType.ARROW;
    @FieldDescription(help = "The speed that it travels at", unit = "m/s")
    public double speed = 1;
    @FieldDescription(help = "The force of gravity on the object", unit = "m/s^2")
    public float gravity = 0;
    @FieldDescription(help = "The lifetime that the object travels for", unit = "1000 = 1 second")
    public int lifetime = 15000;
    @FieldDescription(help = "Wether or not the caster can control this projectile by crouching")
    public AbstractProjectileAction.ControlMode controlMode = AbstractProjectileAction.ControlMode.DISABLED;
    @FieldDescription(help = "The accelleration that your control force will induce on the projectile", unit = "m/s^2")
    public double controlForce = 1;
    @FieldDescription(help = "The item set if the user the type is a DROPPED_ITEM or BLOCK, do not use if not the case")
    public Material item = null;
    @FieldDescription(help = "The item data for the item, only use if type is DROPPED_ITEM")
    public short data = 0;
    @FieldDescription(help = "The hitbox for selecting a target (set to 0 for vanilla projectile contact)", unit = "blocks")
    public double hitbox = 1.5;

    public static void remove(Entity e) {
        //ProjectileAction action = new ProjectileAction();
        actions.remove(e);
        targetsMap.remove(e);
    }

    @EventHandler
    public static void onProjectileHit(ProjectileHitEvent evt) throws InvalidTargetException {
        //System.out.println(evt + ",  " + evt.getHitBlock() + ", " + evt.getHitEntity());
        //System.out.println(actions);
        if (actions.containsKey(evt.getEntity())) {
            if (evt.getHitBlock() != null && actions.get(evt.getEntity()).hasTarget(Block.class)) {
                targetsMap.put(evt.getEntity(), new Target(evt.getHitBlock()));
            }
            if (evt.getHitEntity() != null) {
                if (actions.get(evt.getEntity()).hasTarget(evt.getHitEntity().getClass())) {
                    targetsMap.put(evt.getEntity(), new Target(evt.getHitEntity()));
                    // System.out.println("Added target");
                }
            }
        }
    }

    @EventHandler
    public static void onItemPickup(PlayerPickupItemEvent evt) {

            String string = NMSUtils.read(evt.getItem().getItemStack(), "Projectile", String.class);
            if (string != null && string.equals("true")) {
                evt.setCancelled(true);
            }


    }

    @EventHandler
    public void onBlockHit(EntityChangeBlockEvent evt) {
        if (actions.containsKey(evt.getEntity())) {
            evt.setCancelled(true);
            if (actions.get(evt.getEntity()).hasTarget(Block.class) || actions.get(evt.getEntity()).hasTarget(Location.class)) {
                try {
                    targetsMap.put(evt.getEntity(), new Target(evt.getBlock()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void run(Entity entity, float pitch, float yaw, SpellInstance spell) {
        long start = System.currentTimeMillis();
        while (targetsMap.get(entity) == null && System.currentTimeMillis() - start < lifetime && entity != null) {
            try {
                Location location = null;
                Vector vector = entity.getVelocity();
                if ((controlMode == AbstractProjectileAction.ControlMode.SHIFTING && spell.CASTER.getObject() instanceof Player && ((Player) spell.CASTER.getObject()).isSneaking()) || controlMode == AbstractProjectileAction.ControlMode.ALWAYS) {
                    location = spell.CASTER.getEyeLocation().clone();
                    vector = vector.add(location.getDirection().multiply(controlForce / 10.0));
                }
                vector = vector.add(new Vector(0, -(gravity / 10.0), 0));
                final Vector velocity = vector;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        entity.setVelocity(velocity);
                    }
                }.runTask(UltraLib.getInstance());
                try {
                    Target<Entity> entityTarget = new Target<>(entity);
                    if (protectSpellCaster) {
                        targetsMap.put(entity, Target.getTarget(getTargets(), entity.getLocation(), hitbox, spell.CASTER, entityTarget));
                    } else {
                        targetsMap.put(entity, Target.getTarget(getTargets(), entity.getLocation(), hitbox, entityTarget));
                    }
                } catch (Exception e) {

                }
                try {
                    this.playEffects(new Target(entity));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Target target = targetsMap.get(entity);
        remove(entity);
        entity.remove();
        if (target != null) {
            try {
                runActions(spell, target, new Target(entity));
            } catch (Exception e) {
                e.printStackTrace();
            }
            //nextAction(list, context.origin, this, context.spellInstance);
        }

    }

    @Override
    public boolean isAsyncThread() {
        return false;
    }

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        Location location = (target != null ? target : caster).getEyeLocation();
        double y = Math.sin(Math.toRadians(location.getPitch())) * -2;
        double radius = Math.cos(Math.toRadians(location.getPitch())) * 2;
        double a = Math.toRadians(location.getYaw() + 90);
        double x = Math.cos(a) * radius;
        double z = Math.sin(a) * radius;
        location.add(x, y, z);
        Entity entity;
        if (type == EntityType.DROPPED_ITEM) {
            ItemStack stack = new ItemStack(item);
            ItemMeta meta = stack.getItemMeta();
            meta.setUnbreakable(true);
            stack.setItemMeta(meta);
            stack.setDurability(data);
            try {
                stack = NMSUtils.write(stack, "Projectile", "true");
            } catch (Exception e) {
                e.printStackTrace();
            }
            entity = location.getWorld().dropItemNaturally(location, stack);
        } else if (type == EntityType.FALLING_BLOCK) {
            entity = (target != null ? target : caster).getLocation().getWorld().spawnFallingBlock(location, item.getNewData((byte) 0));
        } else {
            entity = (target != null ? target : caster).getLocation().getWorld().spawnEntity(location, type);
        }
        if (entity != null && entity instanceof Projectile) {
            ((Projectile) entity).setBounce(false);
        }
        actions.put(entity, this);
        targetsMap.put(entity, null);
        float pitch = ((target != null ? target : caster).getEyeLocation().getPitch());
        float yaw = ((target != null ? target : caster).getEyeLocation().getYaw());
        location.setPitch(pitch);
        location.setYaw(yaw);
        entity.teleport(location);
        entity.setVelocity(location.getDirection().multiply(speed));
        entity.setGravity(false);
        //entity.setVelocity(context.origin.getEyeLocation().toVector().add(context.origin.getEyeLocation().getDirection()).multiply(speed));
        SpellThread thread = new SpellThread() {
            public void cast() {
                ProjectileAction.this.run(entity, pitch, yaw, spell);
            }
        };
        spell.getThreads().add(thread);
        thread.runTaskAsynchronously(UltraLib.getInstance());
    }


/*    private void oldRun(ActionContext context, Entity entity, float pitch, float yaw) {
        long start = System.currentTimeMillis();
        while (context.target == null && System.currentTimeMillis() - start < updateNumber(context, lifetime)) {
            pitch = pitch + gravity > -90 && pitch + gravity < 90 ? pitch + gravity : (pitch + gravity <= -90 ? -90 : 90);
            final float PITCH = pitch;
            final float YAW = yaw;
            double speed = updateNumber(context, this.speed);
            if (allowControl && context.spellInstance.origin.getObject() instanceof Player && ((Player) context.spellInstance.origin.getObject()).isSneaking()) {
                pitch = (context.spellInstance.origin.getEyeLocation().
                        getPitch());
                yaw = (context.spellInstance.origin.getEyeLocation().
                        getYaw());
                new SpellThreaad() {
                    public void run() {
                        entity.setVelocity(entity.getVelocity().
                                add(context.spellInstance.origin.getEyeLocation().
                                        getDirection()).multiply(speed));
                        entity.getLocation().
                                setPitch(context.spellInstance.origin.getEyeLocation().
                                        getPitch());
                        entity.getLocation().
                                setYaw(context.spellInstance.origin.getEyeLocation().
                                        getYaw());
                    }
                }.runTask(UltraPlugin.getInstance());
            } else {
                new SpellThreaad() {
                    public void run() {
                        Location location = entity.getLocation().clone();
                        location.setPitch(PITCH);
                        location.setYaw(YAW);
                        entity.setVelocity(entity.getVelocity().add(location.getDirection()).multiply(speed));
                    }
                }.runTask(UltraPlugin.getInstance());
            }
            this.playEffects(new ActionContext(context.spellInstance, context.origin, new Target(entity, Target.TargetType.ENTITY)));
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (context.target != null) {
            int i = (context.spellInstance.action + 1);
            for (Action action : actionList) {
                context.spellInstance.actions.add(i, action);
                i++;
                List<Target[]> list = new ArrayList<>();
                Target[] targets = {new Target(entity.getLocation(), Target.TargetType.LOCATION), context.target};
                list.add(targets);
                context.spellInstance.actionTargets.put(action, list);
            }
        } else {
            context.spellInstance.actions.removeAll(this.actionList);
            entity.remove();
        }
        context.spellInstance.objects.remove("Projectile");
        context.spellInstance.objects.remove("Context");
        new SpellThreaad() {
            public void run() {
                ProjectileAction.nextAction(context);
            }
        }.runTask(UltraPlugin.getInstance());
    }*/


}
