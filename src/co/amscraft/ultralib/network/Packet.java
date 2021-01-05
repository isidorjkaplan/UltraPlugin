package co.amscraft.ultralib.network;

import co.amscraft.ultralib.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;

/**
 * A class that represents a packet to send across a network, stores the bytes of the object in  an unencrypted format
 */
public class Packet {
    //The bytes that represent the object data
    private byte[] object;

    /**
     * The constructor for a packet, pass it an object and it will store it as bytes
     *
     * @param object The object to store
     */
    public Packet(Object object) {
        this(new PacketData(object));//Calls upon the constructor with the packet ddata
    }

    /**
     * The constructor for a packet object to send across a network
     *
     * @param data The PacketData object to send
     */
    public Packet(PacketData data) {
        this(data.getBytes());//Call on the constructor to save the bytes
    }

    /**
     * A constructor to send a file across a network
     *
     * @param file The file to send
     * @throws IOException
     */
    public Packet(File file) throws IOException {
        this(new PacketData(file));//Calls on the packet data constructor, acts differently if its a file so had to have a seperate call
    }

    public Packet(byte[] bytes) {
        this.object = bytes;//Set the stored bytes to match the packet bytes
    }

    public byte[] getBytes() {
        return this.object;//a method to get the bytes
    }

    public PacketData getData() {//A method to reconstruct the packet data from the bytes
        return (PacketData) ObjectUtils.read(new String(this.getBytes()));
    }
}
