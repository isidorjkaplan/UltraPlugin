package co.amscraft.morph;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.player.PlayerData;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.List;

public class MorphData extends PlayerData {
    @FieldDescription(save = false)
    private Morph active;

    private static boolean hasPerm(List<String> list, String perm) {
        for (String s : list) {
            if (perm.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    public Morph getActive() {
        return active;
    }

    public void setActive(Morph morph) {
        if (this.getActive() != null) {
            this.getActive().disable(this.getPlayer().getBukkit());
        }
        this.active = morph;
        if (morph != null) {
            this.getActive().activate(this.getPlayer().getBukkit());
        }
    }

    public boolean hasMorph(Morph morph) {
        return (hasPerm(PermissionsEx.getUser(this.getPlayer().getBukkit())
                .getPermissions(this.getPlayer().getBukkit().getWorld().getName()), "UltraLib.Morph." + morph.getName()));
    }

}
