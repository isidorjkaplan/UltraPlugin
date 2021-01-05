package co.amscraft.ultralib.utils;

import co.amscraft.ultralib.UltraLib;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Izzy on 2017-10-22.
 */
public class NMSUtils {
    private NMSUtils() {

    }

    public static String getVersion() {
        String version = UltraLib.getInstance().getServer().getClass().getPackage().getName();
        version = version.substring(version.lastIndexOf('.') + 1);
        return version;
    }

    public static String getPackage() {
        return "net.minecraft.server." + getVersion();
    }


    public static <T> T read(ItemStack stack, String key, Class<?> type) {
        try {
            Object nmsStack = Class.forName("org.bukkit.craftbukkit." + getVersion() + ".inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, stack);
            Object tag = nmsStack.getClass().getMethod("getTag").invoke(nmsStack);
            if (tag != null) {
                Object compound = tag.getClass().getMethod("getCompound", String.class).invoke(tag, "ultralib");
                String string = (String) compound.getClass().getMethod("getString", String.class).invoke(compound, key);
                return (T) ObjectUtils.parse(type, string);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ItemStack write(ItemStack stack, String key, Object object) {
        try {
            Object nmsStack = Class.forName("org.bukkit.craftbukkit." + getVersion() + ".inventory.CraftItemStack").getMethod("asNMSCopy", ItemStack.class).invoke(null, stack);
            Object tag = nmsStack.getClass().getMethod("getTag").invoke(nmsStack);
            if (tag == null) {
                tag = Class.forName(getPackage() + ".NBTTagCompound").getConstructor().newInstance();//new net.minecraft.server.v1_12_R1.NBTTagCompound();
            }
            if (!(boolean) tag.getClass().getMethod("hasKey", String.class).invoke(tag, "ultralib")) {//tag.hasKey("ultralib")) {
                tag.getClass().getMethod("set", String.class, Class.forName(getPackage() + ".NBTBase")).invoke(tag, "ultralib", Class.forName(getPackage() + ".NBTTagCompound").newInstance());
            }
            Object tagCompound = tag.getClass().getMethod("getCompound", String.class).invoke(tag, "ultralib");
            if (object != null) {
                tagCompound.getClass().getMethod("setString", String.class, String.class).invoke(tagCompound, key, object.toString());//.setString(key, object.toString());
            } else {
                tagCompound.getClass().getMethod("remove", String.class).invoke(tagCompound, key);
            }
            tag.getClass().getMethod("set", String.class, Class.forName(getPackage() + ".NBTBase")).invoke(tag, "ultralib", tagCompound);
            nmsStack.getClass().getMethod("setTag", Class.forName(getPackage() + ".NBTTagCompound")).invoke(nmsStack, tag);

            return (ItemStack) Class.forName("org.bukkit.craftbukkit." + getVersion() + ".inventory.CraftItemStack").getMethod("asBukkitCopy", Class.forName(getPackage() + ".ItemStack")).invoke(null, nmsStack);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
        //return org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack.asBukkitCopy(nms);
    }


    public static void registerCommand(BukkitCommand command) {
        try {
            String p = "org.bukkit.craftbukkit." + getVersion();
            Class<?> CraftServer = Class.forName(p + ".CraftServer");
            CommandMap map = (CommandMap) CraftServer.getMethod("getCommandMap").invoke(CraftServer.cast(UltraLib.getInstance().getServer()));
            map.register(UltraLib.getInstance().getDescription().getName(), command);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
