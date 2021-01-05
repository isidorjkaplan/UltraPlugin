package co.amscraft.ultralib.network;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.network.events.NetworkConnectedEvent;
import co.amscraft.ultralib.network.events.NetworkConnectionEvent;
import co.amscraft.ultralib.network.events.NetworkPacketReceivedEvent;
import co.amscraft.ultralib.network.events.PacketCommandEvent;
import co.amscraft.ultralib.utils.ObjectUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

public class NetworkListener implements Listener {
    public static ServerSocket socket = null;
    private static FileConfiguration config = null;
    private static boolean running = false;

    public static ServerSocket getSocket() throws IOException {
        if (socket == null) {
            socket = new ServerSocket(getConfig().getInt("port"));
        }
        return socket;
    }

    public static FileConfiguration getConfig() {
        if (config == null) {
            UltraLib.getInstance().saveResource("network.yml", false);
            config = YamlConfiguration.loadConfiguration(new File(UltraLib.getInstance().getDataFolder() + "/network.yml"));
        }
        return config;
    }

    public static void stop() {
        running = false;
    }

    /*public static void main(String[] args) throws IOException {
        socket =  new ServerSocket(1234);
        run();
    }*/

    public static void run() {
        if (!running) {
            running = true;
            new Thread() {
                @Override
                public void run() {
                    try {
                        ObjectUtils.debug(Level.INFO, "Started network listener on port: " + getSocket().getLocalPort());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        while (running && !getSocket().isClosed()) {
                            try {
                                Socket socket = getSocket().accept();
                                NetworkConnectionEvent event = new NetworkConnectionEvent(socket);
                                event.dispatch();
                                if (!event.isCancelled()) {
                                    if (socket != null) {
                                        NetworkConnection connection = new NetworkConnection(socket, null);
                                        ObjectUtils.debug(Level.INFO, "Accepted new network connection: " + socket);
                                        new NetworkConnectedEvent(connection).dispatch();
                                    }
                                } else {
                                    socket.close();
                                    ObjectUtils.debug(Level.INFO, "Rejected network connection from socket: " + socket);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    @EventHandler
    public static void onCommandReceivedEvent(PacketCommandEvent evt) {
        if (PacketCommand.getCommands().get(evt.getCommand().getCommand()) != null) {
            PacketCommand.getCommands().get(evt.getCommand().getCommand()).run(evt);
        }
    }

    @EventHandler
    public static void onNetworkPacketReceivedEvent(NetworkPacketReceivedEvent evt) {
        //evt.getConnection().setLastRecievedPacket(System.currentTimeMillis());
        if (evt.getPacket().getTarget() == null) {
            if (evt.getPacket().getObject() instanceof UltraObject) {
                UltraObject object = (UltraObject) evt.getPacket().getObject();
                ((UltraObject) evt.getPacket().getObject()).ID = UltraObject.nextId((Class<? extends UltraObject>) evt.getPacket().getObject().getClass());
                UltraObject.getList((Class<? extends UltraObject>) evt.getPacket().getObject().getClass()).remove(evt.getPacket().getObject());
                for (UltraObject o : UltraObject.networkReceivedObjects) {
                    if (object.equals(o)) {
                        return;
                    }
                }
                if (UltraObject.networkReceivedObjects.size() >= 200) {
                    UltraObject.networkReceivedObjects.remove(0);
                }
                UltraObject.networkReceivedObjects.add(object);
                new BukkitRunnable() {
                    public void run() {
                        if (UltraObject.networkReceivedObjects.contains(object)) {
                            ObjectUtils.debug(Level.WARNING, "Packet " + evt.getPacket() + " has expired!");
                            UltraObject.networkReceivedObjects.remove(object);
                        }
                    }
                }.runTaskLater(UltraLib.getInstance(), 20 * 60 * 10);
            } else if (evt.getPacket().getObject() instanceof PacketCommand) {
                // evt.setPrintToConsole(false);
                new PacketCommandEvent((PacketCommand) evt.getPacket().getObject(), evt.getConnection(), evt.getPacket()).dispatch();
            }
        } else {
            // System.out.println("Recieved: " + evt.getPacket().getTarget());
            evt.getConnection().getReceived().put(evt.getPacket().getTarget(), evt.getPacket());
            new BukkitRunnable() {
                public void run() {
                    evt.getConnection().getReceived().remove(evt.getPacket().getTarget());
                }
            }.runTaskLater(UltraLib.getInstance(), Math.round(evt.getConnection().getTimedOut() * 20));
        }
    }

}
