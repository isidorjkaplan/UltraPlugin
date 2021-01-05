package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Glow extends Action {
    private String color = "WHITE";
    @FieldDescription(help = "Who can see the glow")
    private Recipient recipient = Recipient.EVERYONE;
    @FieldDescription(help = "The duration of the glow", unit = "seconds")
    private double duration = 10;

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        if (target.getObject() instanceof Entity) {
            List<Player> targets = new ArrayList();
            switch (recipient) {
                case EVERYONE:
                    targets.addAll(Bukkit.getOnlinePlayers());
                    break;
                case CASTER:
                    if (spell.CASTER.getObject() instanceof Player) {
                        targets.add((Player) spell.CASTER.getObject());
                    }
                    break;
                case TARGET:
                    if (target.getObject() instanceof Player) {
                        targets.add((Player) target.getObject());
                    }
                    break;
            }
            org.inventivetalent.glow.GlowAPI.Color color = org.inventivetalent.glow.GlowAPI.Color.valueOf(this.color.toUpperCase());
            org.inventivetalent.glow.GlowAPI.setGlowing((Entity) target.getObject(), color, targets);
            new BukkitRunnable() {
                public void run() {
                    try {
                        org.inventivetalent.glow.GlowAPI.setGlowing((Entity) target.getObject(), null, Bukkit.getOnlinePlayers());
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.runTaskLater(UltraLib.getInstance(), Math.round(20 * duration));
        }

    }

    public enum Recipient {
        EVERYONE, CASTER, TARGET
    }

}
