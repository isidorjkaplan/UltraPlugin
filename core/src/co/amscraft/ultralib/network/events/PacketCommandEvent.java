package co.amscraft.ultralib.network.events;

import co.amscraft.ultralib.events.utility.UltraEvent;
import co.amscraft.ultralib.network.NetworkConnection;
import co.amscraft.ultralib.network.PacketCommand;
import co.amscraft.ultralib.network.PacketData;
import org.bukkit.event.HandlerList;

public class PacketCommandEvent extends UltraEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private PacketCommand command;
    private NetworkConnection connection;
    private PacketData packet;

    public PacketCommandEvent(PacketCommand data, NetworkConnection connection, PacketData packet) {
        this.command = data;
        this.connection = connection;
        this.packet = packet;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public PacketData getPacket() {
        return packet;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public PacketCommand getCommand() {
        return this.command;
    }

    public NetworkConnection getConnection() {
        return connection;
    }
}
