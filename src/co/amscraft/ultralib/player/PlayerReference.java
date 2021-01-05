package co.amscraft.ultralib.player;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class PlayerReference {
    public String uuid = null;

    public UltraPlayer getPlayer() {
        if (uuid == null) {
            for (UltraPlayer player : UltraPlayer.getPlayers()) {
                for (PlayerData data : player.getData()) {
                    for (Field field : data.getClass().getFields()) {
                        try {
                            if (field.get(data) != null && (field.get(data) == this || (field.get(data) instanceof List && (((List) field.get(data)).contains(this))))) {
                                uuid = player.getId().toString();
                                return player;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return UltraPlayer.getPlayer(UUID.fromString(uuid));
    }
}
