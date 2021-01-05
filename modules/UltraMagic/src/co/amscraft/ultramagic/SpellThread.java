package co.amscraft.ultramagic;

import org.bukkit.scheduler.BukkitRunnable;

public abstract class SpellThread extends BukkitRunnable {
    private boolean finished = false;

    public boolean isFinished() {
        return this.finished;
    }


    @Override
    public void run() {
        finished = false;
        cast();
        finished = true;
    }

    public abstract void cast();

}
