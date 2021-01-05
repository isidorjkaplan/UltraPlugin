package co.amscraft.traits;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.editor.Editor;
import co.amscraft.ultralib.player.UltraPlayer;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Vanish extends UltraObject {
    public List<Integer> npcs = new ArrayList<>();
    public Requirement requirement = null;

    public Vanish() {
        super();
    }

    public Vanish(int npc) {
        this();
        if (getNPC(npc) != null) {
            this.npcs.add(npc);
        }
    }

    public static Vanish getVanish(NPC npc) {
        for (Vanish vanish : Vanish.getList(Vanish.class)) {
            if (vanish.contains(npc)) {
                return vanish;
            }
        }
        return null;
    }

    public static NPC getNPC(int id) {
        return CitizensAPI.getNPCRegistry().getById(id);
    }

    public static List<Vanish> getVanishes(NPC npc) {
        List<Vanish> vanishes = new ArrayList<>();
        for (Vanish v : Vanish.getList(Vanish.class)) {
            if (v.contains(npc)) {
                vanishes.add(v);
            }
        }
        return vanishes;
    }

    public static boolean hasDeta(NPC npc) {
        for (Vanish vanish : Vanish.getList(Vanish.class)) {
            if (vanish.contains(npc)) {
                return true;
            }
        }
        return false;
    }

    public static void clear(NPC npc) {
        for (Vanish vanish : Vanish.getList(Vanish.class)) {
            if (vanish.contains(npc)) {
                vanish.npcs.remove(npc.getId());
                if (!vanish.npcs.isEmpty()) {
                    vanish.save();
                }
            }
            if (vanish.npcs.isEmpty()) {
                vanish.delete();
            }
        }
    }

    public static boolean canSee(NPC npc, Player player) {
        if (player != null && UltraPlayer.getPlayer(player).hasData(Editor.class)) {
            return true;
        }
        for (Vanish deta : Vanish.getList(Vanish.class)) {
            if (deta != null && deta.contains(npc) && !deta.canSee(player)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return requirement + npcs.toString();
    }

    public boolean contains(NPC npc) {
        return npcs.contains(npc.getId());
    }

    public boolean canSee(Player player) {
        return requirement == null || requirement.canSee(player);
    }
}
