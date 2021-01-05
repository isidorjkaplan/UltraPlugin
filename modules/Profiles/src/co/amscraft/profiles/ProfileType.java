package co.amscraft.profiles;

import co.amscraft.ultralib.player.UltraPlayer;

public abstract class ProfileType {
    public UltraPlayer getPlayer() {
        for (UltraPlayer player : UltraPlayer.getPlayers()) {
            if (player.hasData(ProfileData.class)) {
                for (Profile profile : player.getData(ProfileData.class).getProfiles()) {
                    if (profile.profileTypes.contains(this)) {
                        return player;
                    }
                }
            }
        }
        return null;
    }

    public abstract void save();

    public abstract void enable();

    public boolean isEnabled() {
        return true;
    }

    public String getFailMessage() {
        return null;
    }
}
