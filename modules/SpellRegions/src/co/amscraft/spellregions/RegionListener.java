package co.amscraft.spellregions;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultramagic.events.SpellActionEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

public class RegionListener implements Listener {
    @EventHandler
    public static void onPlayerChatEvent(AsyncPlayerChatEvent evt) {
        //System.out.println("ChatEventRegistered");
    }

    private static WorldGuardPlugin getWorldGuard() {
        Plugin plugin = UltraLib.getInstance().getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) plugin;
    }

    @EventHandler
    public static void onActionCastEvent(SpellActionEvent evt) {
        //System.out.println("CastEventRegstered");
        Location location = evt.getCaster().getLocation();
        World world = location.getWorld();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

        org.bukkit.util.Vector bVec = location.toVector();
        //System.out.println("Action: " + evt.getAction().getClass().getName());
        for (RegionManager manager: container.getLoaded()) {

            for (String region : manager.getApplicableRegionsIDs(BlockVector3.at(bVec.getX(), bVec.getY(), bVec.getZ()))) {
                //System.out.println("Region: " + region);
                SpellRegion sr = SpellRegion.getRegion(world, region);
                if (sr != null && !sr.canCast(evt.getAction())) {
                    // System.out.println("Blocked: true");
                    evt.setCancelled(true);
                    evt.setErrorMessage(sr.getErrorMessage());
                    //evt.setErrorMessage("A portion of the spell you cast was blocked in this area!");
                    break;
                }
            }
        }
    }
}
