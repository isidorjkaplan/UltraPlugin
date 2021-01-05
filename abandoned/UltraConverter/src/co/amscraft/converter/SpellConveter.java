package co.amscraft.converter;


import co.amscraft.objects.modules.magic.Target;
import co.amscraft.objects.modules.magic.actions.*;
import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.editor.EditorData;
import co.amscraft.ultralib.modules.Module;
import co.amscraft.ultramagic.Spell;
import co.amscraft.ultramagic.actions.Action;
import co.amscraft.ultramagic.actions.ParentAction;
import co.amscraft.ultramagic.actions.TargetSelectorAction;
import co.amscraft.ultramagic.effects.EffectAction;
import co.amscraft.ultramagic.effects.ParticleEffect;
import co.amscraft.ultramagic.main.actions.*;
import co.amscraft.ultramagic.main.actions.CastAction;
import co.amscraft.ultramagic.main.actions.CustomProjectileAction;
import co.amscraft.ultramagic.main.actions.PotionAction;
import co.amscraft.ultramagic.main.actions.ProjectileAction;
import co.amscraft.ultramagic.main.actions.TeleportAction;
import co.amscraft.ultramagic.main.actions.VolumeAction;
import co.amscraft.ultramagic.main.effects.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Izzy on 2017-10-20.
 */
public class SpellConveter {

    public static void convertSpells() {
        for (co.amscraft.objects.modules.magic.Spell ultraSpell : co.amscraft.UltraPlugin.getInstance().spells) {
            try {
                if (Spell.getSpell(ultraSpell.getName()) == null) {
                    Spell spell = new Spell();
                    spell.description = ultraSpell.description;
                    //spell.undo = ultraSpell.undo;
                    spell.name = ultraSpell.name;
                    spell.icon = ultraSpell.iconURL;
                    spell.cooldown = ultraSpell.cooldown;
                    spell.mana = ultraSpell.energy;
                    spell.materials = ultraSpell.items;
                    spell.actions = clone(ultraSpell.baseActions);
                    spell.save();
                    System.out.println("Converted spell: " + spell);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //Spell.save(Spell.class);
    }

    private static List<Action> clone(List<co.amscraft.objects.modules.magic.Action> old) {
        List<Action> list = new ArrayList<>();
        for (co.amscraft.objects.modules.magic.Action action : old) {
            switch (action.actionType) {
                case COMMAND:
                    Command command = new Command();
                    CommandAction commandAction = (CommandAction) action;
                    command.command = commandAction.command;
                    command.user = Command.Type.valueOf(commandAction.user.toString());
                    list.add(command);
                    break;
                case TELEPORT:
                    TeleportAction teleport = new TeleportAction();
                    co.amscraft.objects.modules.magic.actions.TeleportAction oldT = (co.amscraft.objects.modules.magic.actions.TeleportAction) action;
                    teleport.mode = oldT.mode ? TeleportAction.Mode.CASTER_TO_TARGET : TeleportAction.Mode.TARGET_TO_CASTER;
                    list.add(teleport);
                    break;
                case GAMEMODE:
                    ChangeGamemode mode = new ChangeGamemode();
                    mode.duration = ((GameModeAction) action).duration;
                    mode.mode = ((GameModeAction) action).mode;
                    list.add(mode);
                    break;
                case SUMMON_NPC:
                    SummonNPC npc = new SummonNPC();
                    npc.lifetime = ((SummonNPCAction) action).lifetime;
                    npc.name = ((SummonNPCAction) action).name;
                    npc.NPC = ((SummonNPCAction) action).NPC;
                    npc.skin = ((SummonNPCAction) action).skin;
                    list.add(npc);
                    break;
                case CAST: {
                    CastAction a = new CastAction();
                    a.spell_name = ((co.amscraft.objects.modules.magic.actions.CastAction) action).spell.getName();
                    list.add(a);
                }
                break;
                case VOLUME:
                    VolumeAction volume = new VolumeAction();
                    volume.x = ((co.amscraft.objects.modules.magic.actions.VolumeAction) action).x;
                    volume.y = ((co.amscraft.objects.modules.magic.actions.VolumeAction) action).y;
                    volume.z = ((co.amscraft.objects.modules.magic.actions.VolumeAction) action).z;
                    list.add(volume);
                    break;
                case OFFSET:
                    TargetOffset offset = new TargetOffset();
                    offset.direction = ((TargetOffsetAction) action).direction;
                    offset.pitchOffset = ((TargetOffsetAction) action).pitchOffset;
                    offset.random = ((TargetOffsetAction) action).random;
                    offset.xOffset = ((TargetOffsetAction) action).xOffset;
                    offset.yawOffset = ((TargetOffsetAction) action).yawOffset;
                    offset.yOffset = ((TargetOffsetAction) action).yOffset;
                    offset.zOffset = ((TargetOffsetAction) action).zOffset;
                    list.add(offset);
                    break;
                case PROJECTILE: {
                    ProjectileAction a = new ProjectileAction();
                    a.lifetime = ((co.amscraft.objects.modules.magic.actions.ProjectileAction) action).lifetime;
                    //a.allowControl = ((co.amscraft.objects.modules.magic.actions.ProjectileAction) action).allowControl;
                    a.data = ((co.amscraft.objects.modules.magic.actions.ProjectileAction) action).data;
                    a.gravity = ((co.amscraft.objects.modules.magic.actions.ProjectileAction) action).gravity;
                    a.item = ((co.amscraft.objects.modules.magic.actions.ProjectileAction) action).item;
                    a.speed = ((co.amscraft.objects.modules.magic.actions.ProjectileAction) action).speed;
                    a.type = ((co.amscraft.objects.modules.magic.actions.ProjectileAction) action).type;
                    list.add(a);
                }
                break;
                case CONDITION: {
                    Conditional a = new Conditional();
                    a.conditon = Conditional.Conditon.valueOf(((ConditonalAction) action).conditon + "");
                    a.delay = ((ConditonalAction) action).delay;
                    a.invert = ((ConditonalAction) action).invert;
                    a.loop = ((ConditonalAction) action).loop;
                    a.targetType = Conditional.TargetType.valueOf(((ConditonalAction) action).targetType + "");
                    list.add(a);
                }
                break;
                case LIGHTNING:
                    list.add(new Lightning());
                    break;
                case REPEAT:
                    Repeat repeat = new Repeat();
                    repeat.count = ((RepeatAction) action).count;
                    list.add(repeat);
                    break;
                case CHANGE_BLOCK:
                    ChangeBlock change = new ChangeBlock();
                    change.block = ((ChangeBlockAction) action).block;
                    list.add(change);
                    break;
                case EXPLODE:
                    Explosion e = new Explosion();
                    e.size = ((ExplodeAction) action).size;
                    list.add(e);
                    break;
                case POTION:
                    PotionAction potion = new PotionAction();
                    potion.duration = ((co.amscraft.objects.modules.magic.actions.PotionAction) action).duration;
                    potion.effect = ((co.amscraft.objects.modules.magic.actions.PotionAction) action).effect;
                    potion.power = ((co.amscraft.objects.modules.magic.actions.PotionAction) action).power;
                    list.add(potion);
                    break;
                case BURN:
                    Burn burn = new Burn();
                    burn.time = ((BurnAction) action).time;
                    list.add(burn);
                    break;
                case VELOCITY:
                    Velocity velocity = new Velocity();
                    //velocity.origin_control = ((VelocityAction) action).origin_control;
                    velocity.velocity = ((VelocityAction) action).velocity;
                    list.add(velocity);
                    break;
                case CUSTOM_PROJECTILE:
                    CustomProjectileAction customProjectileAction = new CustomProjectileAction();
                    //customProjectileAction.allowControl = ((co.amscraft.objects.modules.magic.actions.CustomProjectileAction) action).allowControl;
                    customProjectileAction.setBlockAction(CustomProjectileAction.BlockAction.valueOf(((co.amscraft.objects.modules.magic.actions.CustomProjectileAction) action).blockAction + ""));
                    customProjectileAction.setDistance(((co.amscraft.objects.modules.magic.actions.CustomProjectileAction) action).distance);
                    customProjectileAction.setGravity(((co.amscraft.objects.modules.magic.actions.CustomProjectileAction) action).gravity);
                    customProjectileAction.setHitbox(((co.amscraft.objects.modules.magic.actions.CustomProjectileAction) action).hitbox);
                    //customProjectileAction.homing = ((co.amscraft.objects.modules.magic.actions.CustomProjectileAction) action).homing;
                    customProjectileAction.setLifetime(((co.amscraft.objects.modules.magic.actions.CustomProjectileAction) action).lifetime);
                    customProjectileAction.setMis(((co.amscraft.objects.modules.magic.actions.CustomProjectileAction) action).mis);
                    //customProjectileAction.pitch = ((co.amscraft.objects.modules.magic.actions.CustomProjectileAction) action).pitch;
                    customProjectileAction.setYaw(((co.amscraft.objects.modules.magic.actions.CustomProjectileAction) action).yaw);
                    list.add(customProjectileAction);
                    break;
                case AREA_OF_EFFECT:
                    AreaOfEffect aoe = new AreaOfEffect();
                    aoe.radius = ((AreaOfEffectAction) action).radius;
                    list.add(aoe);
                    break;
                case DAMAGE:
                    Damage damage = new Damage();
                    damage.bypassArmour = ((DamageAction) action).bypassArmour;
                    damage.damage = ((DamageAction) action).damage;
                    list.add(damage);
                    break;
                case DELAY:
                    Delay delay = new Delay();
                    delay.delay = ((DelayAction) action).delay;
                    list.add(delay);
                    break;
            }

            if (list.get(list.size() - 1) instanceof TargetSelectorAction) {
                for (Target.TargetType type : action.targetTypes) {
                    ((TargetSelectorAction) list.get(list.size() - 1)).targetTypes.add(TargetSelectorAction.TargetType.valueOf(type.toString().replace("MOB", "LIVING_ENTITY")));
                }
            }
            if (list.get(list.size() - 1) instanceof ParentAction) {
                ((ParentAction) list.get(list.size() - 1)).actions = clone(action.actionList);
            }
            list.get(list.size() - 1).effects = convertEffects(action.particleActions);
            list.get(list.size() - 1).isAsync = action.async;
        }
        return list;
    }

    private static List<EffectAction> convertEffects(List<co.amscraft.objects.modules.magic.EffectAction> oldList) {
        List<EffectAction> effects = new ArrayList<>();
        for (co.amscraft.objects.modules.magic.EffectAction effect : oldList) {
            EffectAction effectAction = null;
            switch (effect.configuration) {
                case SPIRAL:
                    effectAction = new SpiralEffect();
                    break;
                case CIRCLE:
                    effectAction = new CircleEffect();
                    break;
                case PILLAR:
                    effectAction = new PillarEffect();
                    break;
                case BURST:
                case DOT:
                    effectAction = new CloudEffect();
                    break;
                case SPHERE:
                    effectAction = new SphereEffect();
                    break;
                case SOUND:
                    effectAction = new SoundEffect();
                    break;

            }
            if (effectAction instanceof ParticleEffect) {
                ((ParticleEffect) effectAction).particle = effect.particle;
                ((ParticleEffect) effectAction).R = effect.color[0];
                ((ParticleEffect) effectAction).G = effect.color[1];
                ((ParticleEffect) effectAction).B = effect.color[2];
                ((ParticleEffect) effectAction).count = effect.count;
                ((ParticleEffect) effectAction).radius = effect.radius;
                ((ParticleEffect) effectAction).speed = effect.speed;
                if (effect.configuration == co.amscraft.objects.modules.magic.EffectAction.Configuration.DOT) {
                    ((ParticleEffect) effectAction).radius = 0;
                    ((ParticleEffect) effectAction).count = 1;
                }
            } else if (effectAction instanceof SoundEffect) {
                ((SoundEffect) effectAction).radius = (float) effect.radius;
                ((SoundEffect) effectAction).sound = effect.sound;
                ((SoundEffect) effectAction).volume = effect.count;
            }
            effectAction.lifetime = effect.lifetime;
            effects.add(effectAction);
        }
        return effects;
    }

    private void registerUltraMagic() throws Exception {
        Class<?> ultra = Class.forName("co.amscraft.objects.modules.magic.Spell");
        EditorData.registerParse(ultra, ultra.getMethod("getSpell", String.class));
        //EditorData.registerConstructors(ultra, ultra.getConstructors());
        Object ultraplugin = JavaPlugin.getPlugin((Class<JavaPlugin>) Class.forName("co.amscraft.UltraPlugin"));
        EditorData.registerRoot(ultra, (List) Class.forName("co.amscraft.UltraPlugin").getField("spells").get(ultraplugin));
        for (String type : Module.getClasseNames("plugins/UltraPlugin.jar")) {
            if (type.contains("co.amscraft.objects.modules.magic.actions")) {
                //EditorData.registerConstructors(Class.forName("co.amscraft.objects.modules.magic.Action"), Class.forName(type.replace(".class", "")).getConstructors());
            }
        }
        new BukkitRunnable() {
            public void run() {
                File file = new File("plugins/UltraLib/old_spells.yml");
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                for (Map map : config.getMapList("spells")) {
                    try {
                        //System.out.println(UltraObject.read(map));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.runTaskLater(UltraLib.getInstance(), 1);
    }
}
