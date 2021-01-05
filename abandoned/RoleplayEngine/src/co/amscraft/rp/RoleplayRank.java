package co.amscraft.rp;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.editor.FieldDescription;

import java.util.*;

public class RoleplayRank extends UltraObject {
    @FieldDescription(help = "The PEX groups of the players")
    private Set<String> ranks = new HashSet<>();
    private String race = "";
    private Map<RoleplayPower, RoleplayPower.PowerLevel> powers = new HashMap<>();//Key is the power, the int is the level
    public static RoleplayRank getRank(String rank) {
        for (RoleplayRank r: getList(RoleplayRank.class)) {
            if (r.ranks.contains(rank)) {
                return r;
            }
        }
        return null;
    }

    public String getRace() {
        return race;
    }

    public Map<RoleplayPower, RoleplayPower.PowerLevel> getPowers() {
        return powers;
    }

    @Override
    public String toString() {
        return ranks + "";
    }
}
