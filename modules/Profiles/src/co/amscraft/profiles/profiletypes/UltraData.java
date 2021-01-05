package co.amscraft.profiles.profiletypes;

import co.amscraft.profiles.ProfileData;
import co.amscraft.profiles.ProfileType;
import co.amscraft.ultralib.editor.Editor;
import co.amscraft.ultralib.player.PlayerData;
import co.amscraft.ultralib.player.PlayerUtility;
import co.amscraft.ultralib.player.UltraPlayer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;

public class UltraData extends ProfileType {
    public ArrayList<PlayerData> data = new ArrayList<>();

    @Override
    public void save() {
        data.clear();
        for (PlayerData data : this.getPlayer().getData()) {
            if (!(data instanceof ProfileData || data instanceof Editor)) {
                this.data.add(data);
            }
        }
    }

    @Override
    public String getFailMessage() {
        double cooldown = this.getPlayer().getData(PlayerUtility.class).getCooldown("profile");
        if (cooldown > 0) {
            return "You must wait " + new DecimalFormat("#.#").format(cooldown) + " seconds before you can switch profiles";
        }
        return null;
    }

    @Override
    public void enable() {
        UltraPlayer player = this.getPlayer();
        for (PlayerData data : new HashSet<>(player.getData())) {
            if (!(data instanceof ProfileData)) {
                player.removeData(data.getClass());
            }
        }
        for (PlayerData data : this.data) {
            player.setData(data);
        }
    }
}
