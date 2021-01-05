package co.amscraft.ultralib.network.events;

import co.amscraft.ultralib.events.utility.CancelableEvent;
import org.bukkit.event.HandlerList;

import java.net.Socket;

public class NetworkConnectionEvent extends CancelableEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private Socket connection;

    public NetworkConnectionEvent(Socket connection) {
        this.setConnection(connection);
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Socket getConnection() {
        return connection;
    }

    public void setConnection(Socket connection) {
        this.connection = connection;
    }
}
