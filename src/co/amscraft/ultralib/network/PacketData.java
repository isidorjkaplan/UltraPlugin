package co.amscraft.ultralib.network;

import co.amscraft.ultralib.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * A class that represents PacketData, objects of this class can be deconstructed and reconstructed across networks
 */
public class PacketData implements Serializable {
    private Object object;//The object that is being sent
    private long created;//The creation time of the packet
    private int sender;//A sender ID for the packet, this is used to listen for response packets / handshakes
    private Integer target;//The target that this packet is directed at, it is null if there is no target to send to

    /**
     * The no args constructor for a packett
     */
    public PacketData() {
        this((Object) null, null);
    }

    /**
     * The constructor to create a packet to send
     *
     * @param object The object you want to send
     * @param target The ID of the packet this is responding to
     */
    public PacketData(Object object, Integer target) {
        this.object = object;
        this.created = System.currentTimeMillis();
        this.sender = Integer.hashCode(this.hashCode());
        this.target = target;
    }

    /**
     * A method to create a non-addressed packet to send to the server, will be handled by the recieving server's general packet listener
     *
     * @param object The object to send
     */
    public PacketData(Object object) {
        this(object, null);
    }

    /**
     * The constructor to store a File as PacketData
     *
     * @param file The file to sejnd
     * @throws IOException
     */
    public PacketData(File file) throws IOException {
        this(new FilePacket(file), null);//creates a FilePacket and stores it as the object
    }

    /**
     * Get the target this packet is addressed to
     *
     * @return The target
     */
    public Integer getTarget() {
        return target;
    }

    /**
     * Get the sender of the packet
     *
     * @return The sender
     */
    public int getSender() {
        return sender;
    }

    /**
     * Get the time the packet was created acording to the sending server's clock
     *
     * @return The time the packet was created
     */
    public long getCreated() {
        return created;
    }

    /**
     * A method to find out how long since the packet was created
     *
     * @return The time elapsed since the packet was created.
     */
    public double secondsSinceCreation() {
        return (System.currentTimeMillis() - this.getCreated()) / 1000.0;
    }

    /**
     * A method to get the non-encrypted bytes that represent this packet
     *
     * @return
     */
    public byte[] getBytes() {
        try {
            String string = ObjectUtils.write(this);
            //System.out.println(ObjectUtils.toString(this));
            //System.out.println(string);
            return string.getBytes();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * A method to get the object associated with this packet data
     *
     * @return The object from this packet data
     */
    public Object getObject() {
        return object;
    }

    @Override
    public String toString() {
        return "PacketData[" + this.getObject() + "]";
    }
}
