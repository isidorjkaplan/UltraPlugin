package co.amscraft.ultralib.network.events;

import co.amscraft.ultralib.events.utility.UltraEvent;
import co.amscraft.ultralib.network.NetworkConnection;
import org.bukkit.event.HandlerList;

public class NetworkConnectedEvent extends UltraEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private NetworkConnection connection;

    public NetworkConnectedEvent(NetworkConnection connection) {

        this.setConnection(connection);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public NetworkConnection getConnection() {
        return connection;
    }

    public void setConnection(NetworkConnection connection) {
        this.connection = connection;
    }
}
