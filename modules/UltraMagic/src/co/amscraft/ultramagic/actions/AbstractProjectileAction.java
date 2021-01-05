package co.amscraft.ultramagic.actions;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.utils.savevar.SaveVar;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.exceptions.InvalidTargetException;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public abstract class AbstractProjectileAction extends TargetSelectorAction {
    @SaveVar
    protected static int AUTO_TARGET_RANGE = 100;
    //private final static double VELOCITY = 1;
    @FieldDescription(help = "The initial velocity of the projectile", unit = "m/s")
    protected double speed = 10; //bps
    @FieldDescription(help = "How many times per second the projectile will tic", unit = "Hz")
    protected double frequency = 15;
    @FieldDescription(help = "The force of gravity on the projectile", unit = "m/s^2")
    protected double gravity = 0;
    @FieldDescription(help = "The coefficient of of air resistance on the projectile (0 to 1)")
    protected double drag = 0;
    @FieldDescription(help = "A force propelling your projectile further", unit = "m/s^2")
    protected double thrust = 0;
    @FieldDescription(help = "The lifetime of the projectile", unit = "seconds")
    protected double lifetime = 10;
    @FieldDescription(help = "The maximum distance the projectile can travel before it fissles out of existance", unit = "blocks")
    protected double distance = 100;
    @FieldDescription(help = "The radius from the projectile that consitutes a hit", unit = "blocks")
    protected float hitbox = (float) 1.5;
    @FieldDescription(help = "The action that the projectile takes once it hits a block")
    protected BlockAction blockAction = BlockAction.STOP;
    @FieldDescription(help = "Weather or not the caster can control the projectile by shifting")
    protected ControlMode controlMode = ControlMode.DISABLED;
    @FieldDescription(help = "The accelleration induced on the object by the force of user-control", unit = "m/s^2")
    protected double controlForce = 9.81;
    @FieldDescription(help = "The yaw-override of the projectile. Leave at 0 for no override. )", unit = "degrees")
    protected float yaw = 0;
    @FieldDescription(help = "The pitch-override of the projectile. Leave at 0 for no override. ", unit = "degrees")
    protected float pitch = 0;
    //public boolean autoTarget = false;
    //@FieldDescription(unit = "blocks")
    //public double autoTargetRange = 10;
    //public boolean homing = false;
    protected boolean mis = false;//false means it misses on a miss. true means it hits on a miss

    public static int getAutoTargetRange() {
        return AUTO_TARGET_RANGE;
    }

    protected Vector updateVelocity(Vector velocity, SpellInstance spell, Location location, double distanceLeft) {
        velocity = velocity.add(new Vector(0, -(getGravity() / getFrequency()), 0));
        velocity = velocity.add(velocity.clone().normalize().multiply(getThrust() / getFrequency()));
        double force = (getDrag() * 1.225 * ((4 * Math.PI * Math.pow(this.getHitbox(), 2) / 2) * Math.pow(velocity.length(), 2)) / 2);
        velocity.add(velocity.clone().multiply(-1).normalize().multiply(force / getFrequency()));
        if (spell.CASTER.getObject() instanceof Player && ((getControlMode() == ControlMode.SHIFTING && ((Player) spell.CASTER.getObject()).isSneaking()) || getControlMode() == ControlMode.ALWAYS)) {
            velocity = velocity.add(((Player) spell.CASTER.getObject()).getEyeLocation().getDirection().multiply(getControlForce() / getFrequency()));
        } else if (getControlMode() == ControlMode.AUTO_TARGET || (spell.CASTER.getObject() instanceof Player && ((getControlMode() == ControlMode.SHIFT_AUTO_TARGET && ((Player) spell.CASTER.getObject()).isSneaking())))) {
            try {
                Target target = Target.getTarget(getTargets(), location, getAutoTargetRange() < distanceLeft ? getAutoTargetRange() : distanceLeft, spell.CASTER);
                Vector vector = target.getLocation().toVector().subtract(location.toVector());
                ///double distance = location.distance(target.getLocation());
                vector = vector.clone().normalize().multiply((getControlForce() / getFrequency()));
                velocity.add(vector);
            } catch (InvalidTargetException e) {

            }
        }
        location.setDirection(velocity);
        return velocity;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getGravity() {
        return gravity;
    }

    public void setGravity(double gravity) {
        this.gravity = gravity;
    }

    public double getDrag() {
        return drag;
    }

    public void setDrag(double drag) {
        this.drag = drag;
    }

    public double getThrust() {
        return thrust;
    }

    public void setThrust(double thrust) {
        this.thrust = thrust;
    }

    public double getLifetime() {
        return lifetime;
    }

    public void setLifetime(double lifetime) {
        this.lifetime = lifetime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public float getHitbox() {
        return hitbox;
    }

    public void setHitbox(float hitbox) {
        this.hitbox = hitbox;
    }

    public BlockAction getBlockAction() {
        return blockAction;
    }

    public void setBlockAction(BlockAction blockAction) {
        this.blockAction = blockAction;
    }

    public ControlMode getControlMode() {
        return controlMode;
    }

    public void setControlMode(ControlMode controlMode) {
        this.controlMode = controlMode;
    }

    public double getControlForce() {
        return controlForce;
    }

    public void setControlForce(double controlForce) {
        this.controlForce = controlForce;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public boolean isMis() {
        return mis;
    }

    public void setMis(boolean mis) {
        this.mis = mis;
    }

    public enum ControlMode {
        SHIFTING, ALWAYS, DISABLED, AUTO_TARGET, SHIFT_AUTO_TARGET
    }

    public enum BlockAction {
        IGNORE, STOP
    }

}
