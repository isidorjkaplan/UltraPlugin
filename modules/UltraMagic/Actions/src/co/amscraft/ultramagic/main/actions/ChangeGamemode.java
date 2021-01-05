package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.SpellThread;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

/**
 * Created by Izzy on 2017-07-07.
 */
public class ChangeGamemode extends Action {
    public GameMode mode = GameMode.SURVIVAL;
    public double duration = 1;


    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        if (target != null && target.getObject() instanceof Player) {
            Player player = (Player) target.getObject();
            final GameMode old = player.getGameMode();
            player.setGameMode(mode);
            SpellThread thread = new SpellThread() {
                public void cast() {
                    player.setGameMode(old);
                }
            };
            thread.runTaskLater(UltraLib.getInstance(), Math.round(duration * 20));
            spell.getThreads().add(thread);
        }
    }

}
