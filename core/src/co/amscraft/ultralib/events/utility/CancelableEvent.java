package co.amscraft.ultralib.events.utility;

/**
 * Created by Izzy on 2017-10-19.
 */
public abstract class CancelableEvent extends UltraEvent {
    private boolean isCancelled = false;

    public boolean isCancelled() {
        return this.isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }
}
