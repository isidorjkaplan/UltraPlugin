package co.amscraft.ultralib.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class FilePacket {
    public String bytes;
    public String name;
    public String remotePath;

    public FilePacket() {

    }

    public FilePacket(File file) throws IOException {
        this();
        name = file.getName();
        this.remotePath = file.getPath();
        FileInputStream stream = new FileInputStream(file);
        byte[] bytes = new byte[stream.available()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) stream.read();
        }
        this.bytes = Base64.getEncoder().encodeToString(bytes);

    }

    public void copyTo(File location) throws IOException {
        FileOutputStream stream = new FileOutputStream(location);
        stream.write(Base64.getDecoder().decode(this.bytes));
    }
}
