package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.editor.EditorCheck;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Created by Izzy on 2017-06-04.
 */
public class Command extends Action {
    @FieldDescription(help = "The command to be executed, placeholders: <x> <y> <z> <world> <target> <caster>")
    public String command = "bcast <caster> has cast spell on <target>";
    public Type user = Type.CONSOLE;

    public Command() {
        try {
            EditorCheck.register(Command.class.getField("command"), new EditorCheck("You do not have permission to use command actions!") {
                @Override
                public boolean check(Object object, CommandSender sender) {
                    return UltraPlayer.getPlayer(sender).hasPermission("UltraLib.editor.Spell.actions.Command");
                }
            });
        } catch (Exception e) {

        }
    }

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        if (target != null) {
            String command = this.command.replace("<caster>", ((Player) spell.CASTER.getObject()).getName());
            if (target.getObject() instanceof Entity) {
                command = command.replace("<target>", ((Entity) target.getObject()).getName());
            }
            command = command.replace("<x>", target.getLocation().getBlockX() + "");
            command = command.replace("<y>", target.getLocation().getBlockY() + "");
            command = command.replace("<z>", target.getLocation().getBlockZ() + "");
            command = command.replace("<world>", target.getLocation().getWorld().getName() + "");
            switch (user) {
                case CASTER:
                    if (spell.CASTER.getObject() instanceof Player) {
                        ((Player) spell.CASTER.getObject()).performCommand(command);
                    }
                    break;
                case CONSOLE:
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                    break;
                case CASTER_OP:
                    if (spell.CASTER.getObject() instanceof Player) {
                        Player player = (Player) spell.CASTER.getObject();
                        boolean isOp = player.isOp();
                        player.setOp(true);
                        try {
                            Bukkit.dispatchCommand(player, command);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        player.setOp(isOp);
                    }
                    break;
            }
        }
    }


    public enum Type {
        CONSOLE, CASTER, CASTER_OP
    }
}
