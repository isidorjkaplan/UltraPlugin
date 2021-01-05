package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.utils.savevar.SaveVar;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.SpellThread;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Izzy on 2017-06-12.
 */
public class ChangeBlock extends Action implements Listener {
    private static HashMap<String, Long> activeSpells = new HashMap<>();
    private static long frozenTill = -1;
    @SaveVar
    public static final boolean SAVE_TO_CORE_PROTECT = true;

    public Material block = Material.AIR;
    @FieldDescription(unit = "seconds")
    public double undo = 10;

    public static void changeBlocks(final SpellInstance spell, Location location, final double undo, Block block) {
        changeBlocks(spell, location, undo, block.getType(), block.getData());
    }

    public static void freezeWaterFor(double seconds) {
        long till = Math.round(seconds * 1000 + System.currentTimeMillis()) + 5000;
        if (till > frozenTill) {
            frozenTill = till;
        }
    }

    private static boolean isFlowing(Material block) {
        return block == Material.LAVA || block == Material.WATER;
    }


    @Deprecated
    public static void changeBlocks(final SpellInstance spell, final Location LOCATION, final double undo, final Material block, byte data) {
        changeBlocks(spell, LOCATION, undo, block, null);
    }
    public static void changeBlocks(final SpellInstance spell, final Location LOCATION, final double undo, final Material block, BlockData data) {
        if (!Arrays.asList(new Material[]{Material.CHEST, Material.TRAPPED_CHEST}).contains(LOCATION.getBlock().getType())) {
            SpellThread thread = new SpellThread() {
                public void cast() {
                    final long CAST_TIME = System.currentTimeMillis();
                    Location location = LOCATION.clone();
                    String key = location.getWorld().getName() + "-" + location.getBlockX() + "-" + location.getBlockY() + "-" + location.getBlockZ();
                    if (!activeSpells.containsKey(key)) {
                        activeSpells.put(key, CAST_TIME);
                        if (isFlowing(block)) {
                            freezeWaterFor(undo);
                        }
                        final Location loc = location;
                        final String NAME = spell.CASTER.getObject() instanceof Entity ? ((Entity) spell.CASTER.getObject()).getName() : spell.getSpell().name;
                        final BlockData DATA = location.getBlock().getBlockData();
                        final Material TYPE = location.getBlock().getType();
                        final MaterialData MATERIAL_DATA = location.getBlock().getState().getData().clone();
                        if (SAVE_TO_CORE_PROTECT) {
                            try {
                                spell.getTargets().add(new Target<Block>(location.getBlock()));
                                CoreProtectAPI api = JavaPlugin.getPlugin(CoreProtect.class).getAPI();
                                api.logRemoval(NAME, location, TYPE, DATA);
                                api.logPlacement(NAME, location, block, data);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        location.getBlock().setType(block);
                        if (data != null) {
                            location.getBlock().setBlockData(data);
                        }
                        //System.out.println(undo);
                        if (undo > 0) {
                            SpellThread undoThread = new SpellThread() {
                                public void cast() {
                                    try {
                                        //System.out.println(locat + ", " + TYPE + ":" + DATA);
                                        loc.getBlock().setType(TYPE);
                                        loc.getBlock().setBlockData(DATA);
                                        loc.getBlock().getState().setData(MATERIAL_DATA);
                                        activeSpells.remove(key);
                                        if (SAVE_TO_CORE_PROTECT) {
                                            new BukkitRunnable() {
                                                public void run() {
                                                    CoreProtectAPI api = JavaPlugin.getPlugin(CoreProtect.class).getAPI();
                                                    api.logRemoval(NAME, loc, block, data);
                                                    api.logPlacement(NAME, loc, TYPE, DATA);
                                                }
                                            }.runTaskAsynchronously(UltraLib.getInstance());
                                        }
                                        //List<String[]> list = api.performLookup((int)Math.round(undo*100), NAME, null, null, null, null, 1, null);
                                        //List<String> users = new ArrayList<>();
                                        //users.add(NAME);
                                        //api.performRollback((int) ((System.currentTimeMillis() - CAST_TIME) / 1000.0), users, null, null, null, null, 1, location);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            spell.getThreads().add(undoThread);
                            undoThread.runTaskLater(UltraLib.getInstance(), Math.round(undo * 20));
                        }
                    }
                }
            };
            if (!Bukkit.isPrimaryThread()) {
                spell.getThreads().add(thread);
                thread.runTask(UltraLib.getInstance());
            } else {
                thread.cast();
            }
        }

    }

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        if (target != null) {
            changeBlocks(spell, target.getLocation(), undo, block, (byte) 0);
        }
    }

    @EventHandler
    public static void onBlockPhysics(BlockFromToEvent evt) {
        if (System.currentTimeMillis() < frozenTill) {
            //if (isFlowing(evt.getBlock().getType())) {
                evt.setCancelled(true);
            //}
        }
    }


    /*@EventHandler
    public void onSpellFinishCastingEvent(SpellFinishCastingEvent evt) {
        new SpellThreaad() {
            public void run() {
                SpellInstance spell = evt.getSpell();
                boolean undo = false;
                for (Action action: spell.getSpell().getAllActions()) {
                    if (action instanceof ChangeBlock) {
                        undo = true;
                        break;
                    }
                }
                if (!undo) {
                    return;
                }
                List<Block> blocks = new ArrayList<>();
                for (Target target: spell.getTargets()) {
                    if (target.getObject() instanceof Block) {
                        blocks.add((Block) target.getObject());
                    }
                }
                Plugin plugin = UltraLib.getInstance().getServer().getPluginManager().getPlugin("CoreProtect");
// Check that CoreProtect is loaded
                if (plugin == null || !(plugin instanceof CoreProtect) || blocks.isEmpty()) {
                    return;
                }
                new SpellThreaad() {
                    public void run() {
                        CoreProtectAPI api = JavaPlugin.getPlugin(CoreProtect.class).getAPI();
                        List<String> users = new ArrayList<>();
                        final String NAME = spell.CASTER.getObject() instanceof Entity ? ((Entity) spell.CASTER.getObject()).getName() : spell.getSpell().name;
                        users.add(NAME);
                        double x = 0;
                        double y = 0;
                        double z = 0;
                        for (Block block : blocks) {
                            Location location = block.getLocation();
                            x += location.getX();
                            y += location.getY();
                            z += location.getZ();
                        }
                        x = x / blocks.size();
                        y = y / blocks.size();
                        z = z / blocks.size();
                        Location center = new Location(blocks.get(0).getWorld(), x, y, z);
                        double furthest = 0;
                        for (Block block : blocks) {
                            Location location = block.getLocation();
                            if (location.distanceSquared(center) > furthest) {
                                furthest = location.distanceSquared(center);
                            }
                        }
                        furthest = Math.sqrt(furthest);
                        api.performRollback((int) ((System.currentTimeMillis() - spell.CAST_TIME) / 1000.0), users, null, null, null, null, (int) (furthest - (furthest - (int) furthest)), center);
                    }
                }.runTaskAsynchronously(UltraLib.getInstance());
            }
        }.runTaskLaterAsynchronously(UltraLib.getInstance(), Math.round(evt.getSpell().getSpell().cooldown * 20));


    }*/


}
