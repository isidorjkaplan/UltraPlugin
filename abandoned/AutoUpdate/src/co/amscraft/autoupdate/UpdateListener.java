package co.amscraft.autoupdate;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.network.FilePacket;
import co.amscraft.ultralib.network.events.NetworkPacketReceivedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;

public class UpdateListener implements Listener {

    @EventHandler
    public void onPacketEvent(NetworkPacketReceivedEvent evt) throws IOException {
        if (evt.getPacket().getObject() instanceof FilePacket) {
            FilePacket packet = (FilePacket) evt.getPacket().getObject();
            if (packet.remotePath.startsWith(UltraLib.getInstance().getDataFolder() + "/modules") && packet.name.endsWith(".jar")) {
                File file = new File(packet.remotePath);
                packet.copyTo(file);
            }
        }
    }

}
