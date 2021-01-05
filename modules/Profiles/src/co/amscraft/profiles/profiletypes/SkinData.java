package co.amscraft.profiles.profiletypes;

import co.amscraft.profiles.ProfileType;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class SkinData extends ProfileType {
    public String skin = null;

    @Override
    public void save() {
        //JavaPlugin.getPlugin(com.github.games647.changeskin.bukkit.ChangeSkinBukkit.class).
        Object current = getCurrentSkin();
        if (current != null) {
            skin = current.toString();
        }
        //skin = JavaPlugin.getPlugin(com.github.games647.changeskin.bukkit.ChangeSkinBukkit.class).getLoginSession(this.getPlayer().getId()).getTargetSkin().getProfileId().toString();
        //System.out.println("Skin saved to: " + skin);
    }

    private UUID getCurrentSkin() {
        return JavaPlugin.getPlugin(com.github.games647.changeskin.bukkit.ChangeSkinBukkit.class).getStorage().getPreferences(this.getPlayer().getId()).getTargetSkin().getProfileId();
    }

    @Override
    public boolean isEnabled() {
        //System.out.println("Is enabled: " + Bukkit.getPluginManager().isPluginEnabled("ChangeSkin"));
        return Bukkit.getPluginManager().isPluginEnabled("ChangeSkin");
    }

    @Override
    public void enable() {
        if (skin == null) {
            skin = this.getPlayer().getBukkit().getUniqueId().toString();
        }
        if (!getCurrentSkin().toString().equals(skin)) {
            JavaPlugin.getPlugin(com.github.games647.changeskin.bukkit.ChangeSkinBukkit.class).setSkin(this.getPlayer().getBukkit(), UUID.fromString(skin), true);
            //Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "setskin " + this.getPlayer().getBukkit().getName() + " " + skin);
        }
    }
}
