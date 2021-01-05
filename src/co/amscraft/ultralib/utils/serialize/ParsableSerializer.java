package co.amscraft.ultralib.utils.serialize;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.utils.ObjectUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffectType;

import java.util.Set;
import java.util.UUID;

public class ParsableSerializer extends AbstractSerializer<Object> {

    @Override
    public boolean use(Class type) {
        if (UltraObject.class.isAssignableFrom(type)) {
            return false;
        }
        return isPrimitive(type) || type.isEnum() || PotionEffectType.class.isAssignableFrom(type) || UUID.class.isAssignableFrom(type);
    }

    @Override
    public void write(ConfigurationSection config, Object object, Set added) {
        config.set("value", object + "");
    }

    @Override
    public Object read(ConfigurationSection config) {
        Object o = ObjectUtils.parse(getClass(config), config.getString("value"));
        //System.out.println("Converted " + config.get("value") + " into " + o);
        return o;
    }

}
