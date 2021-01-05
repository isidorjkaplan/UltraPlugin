package co.amscraft.ultralib.network.events;

import co.amscraft.ultralib.events.utility.UltraEvent;
import co.amscraft.ultralib.network.NetworkConnection;
import co.amscraft.ultralib.network.PacketData;
import org.bukkit.event.HandlerList;

public class NetworkPacketReceivedEvent extends UltraEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private PacketData data;
    private NetworkConnection connection;


    public NetworkPacketReceivedEvent(PacketData data, NetworkConnection connection) {
        this.data = data;
        this.connection = connection;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public PacketData getPacket() {
        return data;
    }

    public NetworkConnection getConnection() {
        return connection;
    }
}
