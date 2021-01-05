package co.amscraft.ultralib.network;


import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.network.events.NetworkConnectedEvent;
import co.amscraft.ultralib.network.events.NetworkPacketReceivedEvent;
import co.amscraft.ultralib.network.exceptions.PacketTimedOutException;
import co.amscraft.ultralib.utils.BinaryHexConverter;
import co.amscraft.ultralib.utils.ObjectUtils;
import com.google.common.primitives.Ints;
import org.bukkit.configuration.file.FileConfiguration;

import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;
import java.util.logging.Level;

public class NetworkConnection {
    /**
     * The list of active network connections
     */
    public static Set<NetworkConnection> connections = new HashSet<>();
    /**
     * A list of all the saved encryptions for different IP addresses
     */
    private static HashMap<String, String> encryptions = null;
    /**
     * A private thread for sending packets
     */
    public Thread sendingThread = null;
    /**
     * A private thread for recieving packets
     */
    public Thread receivingThread = null;
    /**
     * The Internet Socket it is communicating with
     */
    private Socket socket;
    /**
     * The encryption key that any recieving packets must be encrypted with
     */
    private byte[] encryption;
    /**
     * A variable to state weather or not it is currently listening for packets
     */
    private boolean listening = false;
    /**
     * A list of all the packets pending to be sent
     */
    private Set<Packet> sending = new HashSet<>();
    /**
     * A runnable to send packets
     */
    private Runnable sendingRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                send();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private int receiving = 0;
    private Runnable receivingRunnable = new Runnable() {
        public void run() {
            try {
                listen();
            } catch (Exception e) {
                e.printStackTrace();
                close();
            }
        }
    };
    private double timedOut = 2;
    private Map<Integer, PacketData> received = new HashMap<>();

    private long lastPacketTime;

    /**
     * The constructor to create a NetworkConnection from an Internet Socket and an encryption
     *
     * @param socket     The internet socket
     * @param encryption The encryption key
     */
    public NetworkConnection(Socket socket, byte[] encryption) {
        this.socket = socket;
        this.setListening(true);
        connections.add(this);
        if (encryption != null) {
            this.setEncryption(encryption);
        }
        new NetworkConnectedEvent(this).dispatch();
    }


    /**
     * The method to create a network connection with an IP, port, and encryption
     *
     * @param address    The IP
     * @param port       The port
     * @param encryption The encryption key for the AES encryption
     * @throws IOException
     */
    public NetworkConnection(String address, int port, byte[] encryption) throws IOException {
        this(new Socket(address, port), encryption);
    }

    /**
     * The constructor to create a connection from an IP and port
     *
     * @param address The IP
     * @param port    The port
     * @throws IOException
     */
    public NetworkConnection(String address, int port) throws IOException {
        this(new Socket(address, port), null);
    }

    /**
     * A static method to get a connection with an IP
     *
     * @param ip   The IP
     * @param port The port
     * @return The network connection
     */
    public static NetworkConnection getOrCreate(String ip, int port) {
        List<NetworkConnection> connections = NetworkConnection.getConnections(ip, port);
        NetworkConnection connection = null;
        if (connections.isEmpty()) {
            try {
                connection = new NetworkConnection(ip, port);
            } catch (IOException e) {
                ObjectUtils.debug(Level.WARNING, e.getClass().getSimpleName() + " while trying to connect to " + ip + ":" + port);
            }
        } else {
            connection = connections.get(0);
        }
        return connection;
    }

    /**
     * A method to retrive the saved encryption keys
     *
     * @return The saved encryption keys
     */
    public static HashMap<String, String> getSavedEncryptions() {
        if (encryptions == null) {
            if (getConfig().getConfigurationSection("saved") != null) {
                encryptions = (HashMap<String, String>) ObjectUtils.read(getConfig().getConfigurationSection("saved"));
            }
            if (encryptions == null) {
                encryptions = new HashMap<>();
            }

        }
        return encryptions;
    }

    /**
     * A method to get the config for NetworkConnections
     *
     * @return The config
     */
    public static FileConfiguration getConfig() {
        return NetworkListener.getConfig();
    }

    /**
     * A method to get all the existing connections with a given IP and Port
     *
     * @param ip   The IP
     * @param port The port
     * @return The list of connections
     */
    public static List<NetworkConnection> getConnections(String ip, int port) {
        List<NetworkConnection> connections = new ArrayList<>();
        for (NetworkConnection connection : NetworkConnection.connections) {
            if (connection.getSocket().getPort() == port && connection.getSocket().getInetAddress().getHostAddress().equalsIgnoreCase(ip)) {
                connections.add(connection);
            }
        }
        return connections;
    }

    /**
     * Get the encryption key of the server the plugin is running on
     *
     * @return This server's encryption key
     */
    public static byte[] getEncryptionKey() {
        FileConfiguration config = NetworkListener.getConfig();
        if (config.getString("key") == null) {
            regenEncryptionKey();
        }
        return BinaryHexConverter.parseHexBinary(config.getString("key"));
    }

    /**
     * A command to generate a new encryption key
     */
    public static void regenEncryptionKey() {
        FileConfiguration config = NetworkListener.getConfig();
        config.set("key", getEncryptionKeyAsString(ObjectUtils.generateKey()));
        try {
            config.save(UltraLib.getInstance().getDataFolder() + "/network.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            config.save(UltraLib.getInstance().getDataFolder() + "/network.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    public void send(File file) throws IOException {
        OutputStream out = this.getSocket().getOutputStream();
        InputStream in = new FileInputStream(file);
        out.write(OutputCode.FILE.getCode());
        out.write(file.getPath().getBytes().length);
        out.write(file.getPath().getBytes());
        //out.write(in.available());
        copy(in, out);
    }*/

    /**
     * A method to get the encryption key in string format
     *
     * @param key byte array for the encryption key
     * @return The string of the encryption key
     */
    public static String getEncryptionKeyAsString(byte[] key) {
        return BinaryHexConverter.printHexBinary(key);
    }

    /**
     * A command to send this server's encryption key to a remote server
     */
    public void sendEncryption() {
        this.upload(new PacketCommand("encryption", getEncryptionKeyAsString(getEncryptionKey())));
    }

    /**
     * The method to terminate this connection
     */
    public void close() {
        ObjectUtils.debug(Level.WARNING, "Network Connection closed: " + this.getSocket());
        setListening(false);
        connections.remove(NetworkConnection.this);
        try {
            this.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.getReceiving().stop();
        this.getSending().stop();
    }

    /**
     * A method to get the address of the remote server
     *
     * @return
     */
    public String getAddress() {
        return this.getSocket().getInetAddress().getHostAddress() + ":" + this.getSocket().getPort();
    }

    /**
     * A method to get the socket of the remote server
     *
     * @return The Internet Socket of the remote server
     */
    public Socket getSocket() {
        return this.socket;
    }

    /**
     * A method to set if this connection should be listening for packets
     *
     * @param listening Weather this coonnection should be listening for packets
     */
    public void setListening(boolean listening) {
        this.listening = listening;
        if (this.listening && (this.receivingThread == null || !this.getReceiving().isAlive())) {
            this.receivingThread = null;
            this.getReceiving().start();
        }
    }

    /**
     * A method to get the thread for sending data
     *
     * @return The sending thread
     */
    public Thread getSending() {
        if (this.sendingThread == null) {
            this.sendingThread = new Thread(this.sendingRunnable);
        }
        return sendingThread;
    }

    /**
     * A method for getting the recieving thread
     */
    public Thread getReceiving() {
        if (this.receivingThread == null) {
            this.receivingThread = new Thread(this.receivingRunnable);
        }
        return receivingThread;
    }

    /**
     * A method for uploading a packet to the server queae
     *
     * @param packet The packet to upload
     */
    public void upload(Packet packet) {
        this.sending.add(packet);
        if (!this.isSending()) {
            this.sendingThread = null;
            this.getSending().start();
        }
    }

    /**
     * A method for uploading a packet to the server and then waiting until a response is recieved
     *
     * @param string The string for the command
     * @param args   The command arguments
     * @return The packet reply
     * @throws PacketTimedOutException
     */
    public PacketData uploadAndWait(String string, Object... args) throws PacketTimedOutException {
        return this.uploadAndWait(new PacketCommand(string, args));
    }

    /**
     * A method for uploading an object to the server and watiing for a reply
     *
     * @param object The object to upload
     * @return The reply packet
     * @throws PacketTimedOutException
     */
    public PacketData uploadAndWait(Object object) throws PacketTimedOutException {
        return this.uploadAndWait(new PacketData(object), true);
    }

    /**
     * A method for uploading a packet to the server and waiting for a response
     *
     * @param packet Tye packet to upload
     * @param ping   Weather or not to ping the server to see if it will replyy before sending
     * @return The reply packet
     * @throws PacketTimedOutException
     */
    public PacketData uploadAndWait(PacketData packet, boolean ping) throws PacketTimedOutException {
        if (ping) {
            this.ping();
        }
        int address = packet.getSender();
        //System.out.println("Sent: " + address);
        this.upload(packet);
        long start = System.currentTimeMillis();
        while (!received.containsKey(address) && ((System.currentTimeMillis() - start) / 1000.0 < this.getTimedOut())) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (received.containsKey(address)) {
            return received.get(address);
        }
        throw new PacketTimedOutException();
    }

    /**
     * A method to get a list a map of all the recieved packets
     *
     * @return The recieved packets
     */
    public Map<Integer, PacketData> getReceived() {
        return received;
    }

    /**
     * A method to get the amount of time until a packet is considered timed out with no reply
     *
     * @return The amount of time before a packet times out
     */
    public double getTimedOut() {
        return timedOut;
    }


    /**
     * A method for setting how long until a packet times out
     */
    public void setTimedOut(double timedOut) {
        this.timedOut = timedOut;
    }

    /**
     * A method for uploading an object to a server
     *
     * @param object The object to upload
     */
    public void upload(Object object) {
        //System.out.println(object);
        this.upload(new Packet(object));
    }


    public void upload(Object object, int target) {
        this.upload(new PacketData(object, target));
    }

    public void upload(PacketData packet) {
        this.upload(new Packet(packet));
    }

    /**
     * A private method for actually sending a packet, will cause issues if called while a packet is in the middle of sending
     *
     * @param packet The packet to send
     * @throws IOException
     */
    private void send(Packet packet) throws IOException {
        OutputStream out = this.getSocket().getOutputStream();
        byte[] encrypted = ObjectUtils.cipher(packet.getBytes(), this.getEncryption(), Cipher.ENCRYPT_MODE);
        ObjectUtils.debug(Level.INFO, "Uploading " + encrypted.length + " bytes to: " + this.getSocket());
        out.write(Ints.toByteArray(encrypted.length));
        for (int i = 0; i < encrypted.length; i++) {
            out.write(encrypted[i]);
        }
        //ObjectUtils.debug(Level.INFO, "Finished uploading " + encrypted.length + " bytes!");
    }

    /**
     * A command for pinging a remote server
     *
     * @return The time for a round response
     * @throws PacketTimedOutException
     */
    public double ping() throws PacketTimedOutException {
        long start = System.currentTimeMillis();
        try {
            this.uploadAndWait(new PacketData(new PacketCommand("ping")), false);
        } catch (PacketTimedOutException e) {
            this.sendEncryption();
            start = System.currentTimeMillis();
            this.uploadAndWait(new PacketData(new PacketCommand("ping")), false);
        }
        return ((System.currentTimeMillis() - start) / 1000.0);
    }

    /**
     * A method that sends all the current packets that are waiting to be sent serially
     *
     * @throws IOException
     */
    private void send() throws IOException {
        while (!this.sending.isEmpty()) {
            Packet packet = this.sending.iterator().next();
            this.send(packet);
            this.sending.remove(packet);
        }
    }

    /*8
    A method to check if a packet is currently being sent
     */
    public boolean isSending() {
        return this.sendingThread != null && this.sendingThread.isAlive();
    }


    public int getReceivingCount() {
        return this.receiving;
    }


    public static final int PACKET_NOTIFY_THRESHOLD = 500000;

    /**
     * A method to listen for recieving packets
     *
     * @throws IOException
     */
    private void listen() throws IOException {
        InputStream stream = getSocket().getInputStream();
        while (this.listening && !socket.isClosed() && socket.isConnected()) {
            int size = Ints.fromBytes((byte) stream.read(), (byte) stream.read(), (byte) stream.read(), (byte) stream.read());
            byte[] array = new byte[size];
            receiving = array.length;
            for (int i = 0; i < array.length; i++) {
                array[i] = (byte) stream.read();
                receiving = (array.length - 1) - i;
            }
            try {
                array = ObjectUtils.cipher(array, getEncryptionKey(), Cipher.DECRYPT_MODE);
                try {
                    Packet packet = new Packet(array);
                    PacketData data = packet.getData();
                    if (data.getCreated() - lastPacketTime > PACKET_NOTIFY_THRESHOLD) {
                        ObjectUtils.debug(Level.INFO, "Received packet " + data + " from " + this.getSocket());
                    }
                    lastPacketTime = data.getCreated();
                    NetworkPacketReceivedEvent event = new NetworkPacketReceivedEvent(data, this);
                    event.dispatch();
                } catch (Exception e) {
                    ObjectUtils.debug(Level.WARNING, "Corrupt packet of size " + array.length + " bytes from: " + this.getSocket());
                    e.printStackTrace();
                    this.close();
                }
            } catch (Exception e) {
                ObjectUtils.debug(Level.WARNING, "Packet received with invalid encryption key from: " + this.getSocket());
            }
        }
    }

    /**
     * A method to set the saved encryption key for a remote server
     *
     * @param encryption The encryption key
     */
    public void setEncryption(byte[] encryption) {
        this.encryption = encryption;
//        try {
//            getSavedEncryptions().put(this.getAddress(), this.encryption != null ? getEncryptionKeyAsString(this.encryption) : null);
//            ObjectUtils.write(getConfig().createSection("saved"), getSavedEncryptions());
//            getConfig().save(new File(UltraLib.getInstance().getDataFolder() + "/network.yml"));
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * A method to get the saved encryption key for a server
     *
     * @return The encryption key for the server
     */
    public byte[] getEncryption() {
        if (this.encryption == null && getSavedEncryptions().containsKey(this.getAddress())) {
            this.encryption = BinaryHexConverter.parseHexBinary(getSavedEncryptions().get(this.getAddress()));
        }
        return this.encryption;
    }

    /**
     * A method for setting the encyrption key as a string
     *
     * @param encryption The encryption key
     */
    public void setEncryption(String encryption) {
        this.setEncryption(BinaryHexConverter.parseHexBinary(encryption));
    }

    @Override
    public String toString() {
        return "Connection[" + this.getSocket().getInetAddress().getHostAddress() + ":" + this.getSocket().getPort() + ", transmitting: " + this.isSending() + ", receiving: " + (this.getReceivingCount() > 0 ? this.getReceivingCount() + " bytes" : "false");
    }
}
