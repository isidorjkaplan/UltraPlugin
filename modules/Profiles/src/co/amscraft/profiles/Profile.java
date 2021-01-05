package co.amscraft.profiles;

import co.amscraft.ultralib.modules.Module;
import co.amscraft.ultralib.player.PlayerUtility;
import co.amscraft.ultralib.player.UltraPlayer;

import java.util.ArrayList;
import java.util.List;

public class Profile {
    public List<ProfileType> profileTypes = new ArrayList<>();
    public String name = "name";

    public Profile() {
        updateDataTypes();
    }

    public static List<Class<? extends ProfileType>> getProfileTypes() {
        List<Class<? extends ProfileType>> list = new ArrayList<>();
        for (Class type : Module.getModule(ProfileModule.class).getClasses()) {
            if (!type.equals(ProfileType.class) && ProfileType.class.isAssignableFrom(type)) {
                list.add((Class<? extends ProfileType>) type);
            }
        }
        return list;
    }

    public void updateDataTypes() {
        for (Class<? extends ProfileType> type : getProfileTypes()) {
            if (!containsDataType(type)) {
                try {
                    profileTypes.add(type.newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean containsDataType(Class<? extends ProfileType> type) {
        for (ProfileType data : profileTypes) {
            if (type.isAssignableFrom(data.getClass())) {
                return true;
            }
        }
        return false;
    }

    public UltraPlayer getPlayer() {
        for (UltraPlayer player : UltraPlayer.getPlayers()) {
            if (player.hasData(ProfileData.class) && player.getData(ProfileData.class).profiles.contains(this)) {
                return player;
            }
        }
        return null;
    }

    public void delete() {
        UltraPlayer player = this.getPlayer();
        ProfileData data = player.getData(ProfileData.class);
        if (data.getProfiles().size() == 1 || data.getCurrentProfile() != this) {
            data.profiles.remove(this);
        } else if (data.getCurrentProfile() == this) {
            for (Profile profile : data.getProfiles()) {
                if (profile != this) {
                    profile.enable();
                    data.getProfiles().remove(this);
                    break;
                }
            }
        }
    }

    public void save() {
        updateDataTypes();
        for (ProfileType data : profileTypes) {
            try {
                if (data.isEnabled()) {
                    data.save();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getFailMessage() {
        for (ProfileType data : profileTypes) {
            String s = data.getFailMessage();
            if (s != null) {
                return s;
            }
        }
        return null;
    }

    public boolean isEnabled() {
        return this == this.getPlayer().getData(ProfileData.class).getCurrentProfile();
    }

    public void enable() {
        ProfileData player = this.getPlayer().getData(ProfileData.class);
        player.getCurrentProfile().save();
        player.current = player.getProfiles().indexOf(this);
        for (ProfileType data : profileTypes) {
            try {
                if (data.isEnabled()) {
                    data.enable();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        player.getPlayer().getData(PlayerUtility.class).setCooldown("profile", 10);
    }
}
