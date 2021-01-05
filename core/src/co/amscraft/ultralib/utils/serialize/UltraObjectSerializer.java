package co.amscraft.ultralib.utils.serialize;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.network.PacketData;
import co.amscraft.ultralib.utils.ObjectSerializer;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Set;

public class UltraObjectSerializer extends AbstractSerializer<UltraObject> {
    @Override
    public boolean use(Class type) {
        return UltraObject.class.isAssignableFrom(type);
    }

    private boolean containsPacket(Set added) {
        for (Object o : added) {
            if (o instanceof PacketData) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void write(ConfigurationSection config, UltraObject object, Set added) {
        if (added.size() <= 1 || containsPacket(added)) {
            //System.out.println("Size is small enough ergo writing UltraObject using normal seralizer: " + added);
            ObjectSerializer.getSerializerByType(NormalObjectSerializer.class).write(config, object, added);
        } else {
            //System.out.println(((UltraObject)object).ID + " " + object);
            config.set("IDReference", ((UltraObject) object).ID);
        }
    }

    @Override
    public UltraObject read(ConfigurationSection config) {
        try {
            if (config.get("ID") != null && UltraObject.getObject((Class<? extends UltraObject>) getClass(config), "ID", (int) ObjectSerializer.read(config.getConfigurationSection("ID"))) == null) {
                return (UltraObject) ObjectSerializer.getSerializerByType(NormalObjectSerializer.class).read(config);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return UltraObject.getObject((Class<? extends UltraObject>) getClass(config), "ID", config.getInt("IDReference"));
    }

}
