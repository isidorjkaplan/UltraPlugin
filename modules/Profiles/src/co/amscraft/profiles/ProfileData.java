package co.amscraft.profiles;

import co.amscraft.ultralib.player.PlayerData;

import java.util.ArrayList;
import java.util.List;

public class ProfileData extends PlayerData {
    public List<Profile> profiles = new ArrayList<>();
    public int current = 0;

    public Profile getProfile(String name) {
        for (Profile profile : profiles) {
            if (profile.name.equalsIgnoreCase(name)) {
                return profile;
            }
        }
        return null;
    }

    public Profile newProfile(String name) {
        Profile profile = new Profile();
        profile.name = name;
        profiles.add(profile);
        return profile;
    }

    public Profile getCurrentProfile() {
        if (current > profiles.size() - 1) {
            current = 0;
            if (profiles.isEmpty()) {
                Profile profile = newProfile(this.getPlayer().getBukkit().getName());
                profile.save();
                return profile;
            }
        }
        return profiles.get(current);
    }

    public List<Profile> getProfiles() {
        return this.profiles;
    }
}
