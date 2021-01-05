package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.editor.EditorCheck;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class CopyAndPaste extends Action {
    @FieldDescription(help = "The first corner of the structure to be copied")
    public int x1 = 0, y1 = 0, z1 = 0;
    @FieldDescription(help = "The second corner of the structure to be copied")
    public int x2 = 0, y2 = 0, z2 = 0;
    public String world = Bukkit.getWorlds().get(0).getName();
    @FieldDescription(help = "The center location which the build will be pasted relative to")
    public int x3 = 0, y3 = 0, z3 = 0;
    @FieldDescription(unit = "seconds")
    public double undo = 10;
    @FieldDescription(help = "A list of materials that wont be copied")
    public List<Material> ignore = new ArrayList<>();

    public CopyAndPaste() {
        super();
        try {
            EditorCheck.register(CopyAndPaste.class.getField("world"), new EditorCheck("You must enter a valid world name!") {
                @Override
                public boolean check(Object object, CommandSender sender) {
                    return Bukkit.getWorld(object + "") != null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        if (target != null) {
            World world = Bukkit.getWorld(this.world);
            Location cS = new Location(world, x1 < x2 ? x1 : x2, y1 < y2 ? y1 : y2, z1 < z2 ? z1 : z2);
            Location cL = new Location(world, x1 < x2 ? x2 : x1, y1 < y2 ? y2 : y1, z1 < z2 ? z2 : z1);
            for (int x = cS.getBlockX(); x <= cL.getBlockX(); x++) {
                for (int y = cS.getBlockY(); y <= cL.getBlockY(); y++) {
                    for (int z = cS.getBlockZ(); z <= cL.getBlockZ(); z++) {
                        Location copy = new Location(world, x, y, z);
                        Block block = copy.getBlock();
                        if (!ignore.contains(block.getType())) {
                            int dx = copy.getBlockX() - x3;
                            int dy = copy.getBlockY() - y3;
                            int dz = copy.getBlockZ() - z3;
                            Location paste = target.getLocation().clone().add(dx, dy, dz);
                            ChangeBlock.changeBlocks(spell, paste, undo, block);
                            //System.out.println("Copy(" + copy.getBlockX() + ", " + copy.getBlockY() + ", " + copy.getBlockZ() + ")");
                            //System.out.println("Copy(" + paste.getBlockX() + ", " + paste.getBlockY() + ", " + paste.getBlockZ() + ")");
                        }
                    }
                }
            }
        }
    }
}
