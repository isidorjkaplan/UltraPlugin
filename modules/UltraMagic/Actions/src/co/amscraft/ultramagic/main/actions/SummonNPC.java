package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.modules.Module;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.SpellThread;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.UltraMagic;
import co.amscraft.ultramagic.actions.Action;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.npc.SimpleNPCDataStore;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.api.trait.trait.Inventory;
import net.citizensnpcs.api.util.Storage;
import net.citizensnpcs.api.util.YamlStorage;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.mcmonkey.sentinel.SentinelTrait;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Izzy on 2017-06-26.
 */
public class SummonNPC extends Action {
    private static NPCRegistry registry = null;
    public int NPC = 0;
    public long lifetime = 60;//in seconds
    public String name = "none";
    public String skin = "default";

    private static NPCRegistry getNPCRegistry() {
        if (registry == null) {
            File file = new File(Module.get(UltraMagic.class).getDataFolder() + "/npcs.yml");
            Storage storage = new YamlStorage(file);
            NPCDataStore store = SimpleNPCDataStore.create(storage);
            registry = CitizensAPI.createAnonymousNPCRegistry(store);
        }
        return registry;
    }

    public SentinelTrait clone(SentinelTrait clone) {
        SentinelTrait trait = new SentinelTrait();
        //trait.targets = (HashSet<SentinelTarget>) ((HashSet) clone.targets).clone();
        trait.accuracy = clone.accuracy;
        trait.entityNameTargets = (List<String>) ((ArrayList<String>) (clone.entityNameTargets)).clone();
        trait.autoswitch = clone.autoswitch;
        trait.attackRate = clone.attackRate;
        trait.armor = clone.armor;
        trait.closeChase = clone.closeChase;
        trait.chased = clone.chased;
        trait.enemyTargetTime = clone.enemyTargetTime;
        trait.eventTargets = clone.eventTargets;
        trait.groupTargets = clone.groupTargets;
        trait.heldItemTargets = clone.heldItemTargets;
        trait.fightback = clone.fightback;
        trait.npcNameTargets = clone.npcNameTargets;
        trait.otherTargets = clone.otherTargets;
        trait.playerNameTargets = clone.playerNameTargets;
        trait.damage = clone.damage;
        trait.health = clone.health;
        trait.healRate = clone.healRate;
        return trait;
    }

    @Override
    public void run(SpellInstance spell, Target input, Target caster) {
        Target target = input != null ? input : spell.CASTER;
        NPCRegistry registry = getNPCRegistry();
        final NPC npc = registry.createNPC(EntityType.PLAYER, name.replace("<target>", target != null ? target.getName() : "<target>").replace("<caster>", spell.CASTER.getName()));
        if (!npc.isSpawned()) {
            npc.spawn(target.getLocation());
        }
        LivingEntity entity = (LivingEntity) npc.getEntity();
        entity.teleport(target.getLocation());
        NPC clone = CitizensAPI.getNPCRegistry().getById(NPC);
        if (clone != null) {
            if (clone.hasTrait(SentinelTrait.class)) {
                npc.addTrait(clone(clone.getTrait(SentinelTrait.class)));
                //trait. = clone.getTrait(SentinelTrait.class).;
                /*for (Target living : context.spellInstance.getTargets(Target.TargetType.LIVING_ENTITY)) {
                    if (!living.getObject().equals(context.spellInstance.origin)) {
                        if (living.getObject() instanceof LivingEntity) {
                            npc.getTrait(SentinelTrait.class).addTarget(((LivingEntity) living.getObject()).getUniqueId());
                            npc.getTrait(SentinelTrait.class).tryAttack(((LivingEntity) living.getObject()));
                            System.out.println(npc.getTrait(SentinelTrait.class).isTargeted((LivingEntity) living.getObject()) + "," + (npc.getTrait(SentinelTrait.class) == clone.getTrait(SentinelTrait.class)));
                        }
                    }
                }*/
            }
            if (clone.hasTrait(Inventory.class)) {
                npc.addTrait(Inventory.class);
                Inventory trait = npc.getTrait(Inventory.class);
                trait.setContents(clone.getTrait(Inventory.class).getContents());
            }
            if (clone.hasTrait(Equipment.class)) {
                npc.addTrait(Equipment.class);
                for (Equipment.EquipmentSlot slot : clone.getTrait(Equipment.class).getEquipmentBySlot().keySet())
                    npc.getTrait(Equipment.class).set(slot, clone.getTrait(Equipment.class).getEquipmentBySlot().get(slot));
            }
            npc.setBukkitEntityType(clone.getEntity().getType());
        }
        if (clone != null && !skin.equals("default") && npc.getEntity() instanceof Player) {
            npc.data().set("cached-skin-uuid", clone.data().get("cached-skin-uuid"));
            npc.data().set("player-skin-name", clone.data().get("player-skin-name"));
        } else {
            npc.data().set("player-skin-name", !skin.equals("default") ? skin.replace("<target>", target != null ? target.getName() : "<target>").replace("<caster>", spell.CASTER.getName()) : null);
        }

        if (clone != null && name.equals("none")) {
            npc.setName(clone.getName());
        }

        SpellThread thread = new SpellThread() {
            public void cast() {
                getNPCRegistry().deregister(npc);
            }
        };
        thread.runTaskLater(UltraLib.getInstance(), lifetime * 20);
        spell.getThreads().add(thread);
    }

}
