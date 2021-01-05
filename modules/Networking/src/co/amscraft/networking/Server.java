package co.amscraft.networking;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.network.*;
import co.amscraft.ultralib.network.exceptions.PacketTimedOutException;
import co.amscraft.ultralib.tic.ServerTic;
import co.amscraft.ultralib.utils.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

public class Server extends UltraObject {
    /**
     * A static list of all the online players across all connected servers
     * */
    private static List<String> onlinePlayers = new ArrayList<>();
    /**
     * The name of the server
     */
    private String name = "Server";
    /**
     * The word IP of the server
     */
    private String wordIP = "mc.example.co";
    /**
     * The actual IP of the server
     */
    private String ip = "1.2.3.4";
    /**
     * The server's NetworkPort
     */
    private int port = 3183;
    /**
     * The Encryption Key of the remote server
     */
    private String encryption = "Must Set Key";
    /**
     * A list of all the network connections to that server
     */
    private List<NetworkConnection> connections = new ArrayList<>();
    /**
     * A map of all the packets they recieved in response to a given command
     */
    private Map<String, PacketData> packets = new HashMap<>();
    /**
     * The packet update time
     */
    private int packetUpdate = 30;
    /**
     * The amount of time in seconds before a packet is considered to have timed out
     */
    private int timedOut = 2;

    /**
     * The no args constructor
     */
    public Server() {
        super();
    }

    /**
     * The constructor to creaate a server based on given info
     * @param name  The name
     * @param ip The IP
     * @param port The port
     * @param encryption The encryption key
     */
    public Server(String name, String ip, int port, String encryption) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.encryption = encryption;
    }

    /**
     * The function to get a server by IP and Port
     * @param ip The IP
     * @param port The Port
     * @return
     */
    public static Server getServer(String ip, int port) {
        for (Server server : getServers()) {
            if (server.getIp().equals(ip) && server.getPort() == port) {
                return server;
            }
        }
        return null;
    }

    /**
     * A static function to get a server from a NetworkConnection to that server
     * @param connection The NetworkConnection
     * @return The server
     */
    public static Server getServer(NetworkConnection connection) {
        for (Server server : getServers()) {
            if (server.getConnections().contains(connection)) {
                return server;
            }
        }
        return null;
    }

    /**
     * Get a set of all the loaded servers
     * @return The loaded servers
     */
    public static Set<Server> getServers() {
        return UltraObject.getList(Server.class);
    }

    /**
     *
     * A static function to get the server that a player is on
     * @param player The player
     * @return Their server
     */
    public static Server getServer(String player) {
        if (getLocalPlayers().contains(player)) {
            return null;
        }
        for (Server server : Server.getServers()) {
            if (server.getPlayers().contains(player)) {
                return server;
            }
        }
        return null;
    }

    /**
     * A function to get all the local player's
     * @return All the local player's
     */
    public static List<String> getLocalPlayers() {
        List<String> list = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            list.add(player.getName());
        }
        return list;
    }

    /**
     * A function to update the global online player's, called every 60 seconds in an asyncronous thread
     */
    @ServerTic(delay = 60, isAsync = true)
    public static void updateOnlinePlayers() {
        onlinePlayers.clear();
        for (Server server : getServers()) {
            try {
                for (String player : server.getPlayers()) {
                    if (!onlinePlayers.contains(player)) {
                        onlinePlayers.add(player);
                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }

    /**
     * A function called every 60 seconds in an asyncryonous thread that ensures an open connection to all Servers
     */
    @ServerTic(delay = 60, isAsync = true)
    public static void onServerConnectionUpdate() {
        for (Server server : Server.getServers()) {
            if (server.getConnection() == null) {
                NetworkConnection connection = NetworkConnection.getOrCreate(server.getIp(), server.getPort());
                if (connection != null) {
                    server.setupConnection(connection);
                }
            }
        }
    }

    /**
     * A function to get a list of all the online players
     * @return All the online players
     */
    public static List<String> getOnlinePlayers() {
        List<String> list = getLocalPlayers();
        for (String player : onlinePlayers) {
            if (!list.contains(player)) {
                list.add(player);
            }
        }
        return list;
    }

    /**
     * A function to get the main networkConnection for this server
     * @return The main network connection
     */
    public NetworkConnection getConnection() {
        List<NetworkConnection> connections = this.getConnections();
        return !connections.isEmpty() ? this.getConnections().get(0) : null;
    }

    /**
     * A function to get all the connections with a given server
     * @return All the connections with a given server
     */
    public List<NetworkConnection> getConnections() {
        if (this.connections == null) {
            this.connections = new ArrayList<>();
        }
        for (NetworkConnection connection : this.connections) {
            if (connection == null || connection.getSocket() == null || connection.getSocket().isClosed()) {
                connections.remove(connection);
                break;
            }
        }
        return connections;
    }

    /**
     * An accessor method to get the name
     * @return The name
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * A function to send a cross-server private message
     * @param sender The sender
     * @param player The target
     * @param message The message
     */
    public void sendMsg(Player sender, String player, String message) {
        this.getConnection().upload(new PacketCommand("msg", sender.getName(), player, message));
    }

    public int getTimedOut() {
        return timedOut;
    }

    public void setTimedOut(int timedOut) {
        this.timedOut = timedOut;
    }

    public int getPacketUpdate() {
        return packetUpdate;
    }

    public void setPacketUpdate(int packetUpdate) {
        this.packetUpdate = packetUpdate;
    }

    public boolean isConnected() {
        return this.getConnection() != null;
    }

    public void broadcast(String[] targets, String message) {
        this.broadcast(targets, message, true);
    }

    public void broadcast(String[] targets, String message, boolean prefix) {
        this.getConnection().upload(new PacketCommand("broadcast", targets, ChatColor.translateAlternateColorCodes('§', message), prefix));
    }

    /**
     * A function to get the response from a command
     * @param command The command
     * @return The response
     */
    private Object getCommandResponse(String command) {
        if (this.getConnection() != null && !packets.containsKey(command) || System.currentTimeMillis() - packets.get(command).getCreated() > this.getPacketUpdate() * 1000) {
            try {
                packets.put(command, this.getConnection().uploadAndWait(new PacketCommand(command)));
                new BukkitRunnable() {
                    public void run() {
                        try {
                            packets.remove(command);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.runTaskLater(UltraLib.getInstance(), this.getPacketUpdate() * 20);
            } catch (PacketTimedOutException e) {
                e.printStackTrace();
            }
        }
        PacketData data = packets.get(command);
        return data != null ? data.getObject() : null;
    }

    /**
     * A functiion to initlize a network connection as a server, identifies to the remote server who the connection is from
     * @param connection The remote connection
     */
    public void setupConnection(NetworkConnection connection) {
        connection.setTimedOut(this.getTimedOut());
        connection.sendEncryption();
        try {
            connection.upload(new PacketCommand("identify", NetworkListener.getSocket().getLocalPort(), NetworkConnection.getEncryptionKeyAsString(NetworkConnection.getEncryptionKey())));
            connection.uploadAndWait(new PacketCommand("players"));
            if (!this.getConnections().contains(connection)) {
                this.getConnections().add(connection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PacketTimedOutException e) {
            ObjectUtils.debug(Level.WARNING, "Server timed out while trying to setup connection: " + connection);
            connection.close();
        }

    }

    public void uploadFile(File file) throws IOException {
        this.uploadFile(file, true);
    }

    public void uploadFile(File file, boolean replace) throws IOException {
        this.uploadFile(file, file.getPath(), replace);
    }

    public void uploadFile(File file, String path, boolean replace) throws IOException {
        String[] paths = path.split("[.]");
        String type = paths[paths.length - 1];
        this.getConnection().upload(new PacketCommand("upload", path.substring(0, path.length() - type.length() - 1), new FilePacket(file), replace, type));
    }

    public List<String> getPlayers() {
        return (List<String>) this.getCommandResponse("players");
    }

    public String getWordIP() {
        return wordIP;
    }

    public void setWordIP(String wordIP) {
        this.wordIP = wordIP;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
