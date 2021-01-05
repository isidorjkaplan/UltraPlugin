package co.amscraft.pvpmanager.events;

import co.amscraft.pvpmanager.Game;
import co.amscraft.pvpmanager.shapes.Location;
import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultramagic.exceptions.InvalidTargetException;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SpellAction extends GameEvent {
    private static co.amscraft.ultramagic.Spell spell = null;
    public co.amscraft.ultramagic.actions.Action action = null;
    public boolean runAsLocation = true;
    public boolean runAsWinners = false;
    public boolean runAsLosers = false;
    public boolean runAsEveryone = false;
    public Location location = null;

    public co.amscraft.ultramagic.Spell getSpell() {
        if (spell == null) {
            spell = new co.amscraft.ultramagic.Spell();
            UltraObject.getList(spell.getClass()).remove(spell);
            spell.name = "A spell interface for casting spells";
        }
        return spell;
    }

    @Override
    public void run(Game game) {
        try {
            List<co.amscraft.ultramagic.Target> targets = new ArrayList<>();
            if (this.runAsEveryone) {
                for (Player player : game.getPlayers()) {
                    targets.add(new co.amscraft.ultramagic.Target(player));
                }
            } else {
                if (this.runAsLosers) {
                    for (Player player : game.getQuit().get(game.getQuit().size() - 1).getPlayers()) {
                        targets.add(new co.amscraft.ultramagic.Target(player));
                    }
                }
                if (this.runAsWinners && this.getCondition() == Condition.GAME_WON) {
                    for (Player player : game.getWinner().getPlayers()) {
                        targets.add(new co.amscraft.ultramagic.Target(player));
                    }
                }
            }
            if (this.runAsLocation) {
                targets.add(new co.amscraft.ultramagic.Target(location.getLocation()));
            }
            co.amscraft.ultramagic.Spell spell = getSpell();
            spell.actions.clear();
            spell.actions.add(action);
            for (co.amscraft.ultramagic.Target t : targets) {
                spell.cast(t);
            }
        } catch (InvalidTargetException e) {
            e.printStackTrace();
        }
    }
}
