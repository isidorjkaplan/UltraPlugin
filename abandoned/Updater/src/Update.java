import co.amscraft.networking.Server;
import co.amscraft.ultralib.network.NetworkConnection;
import co.amscraft.ultralib.network.PacketCommand;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Update {

    public static String[] files = new String[]{
            "/users/izzy6/Dropbox/AMS/TestPluginServer/plugins/UltraLib",
            "/users/izzy6/Dropbox/AMS/TestPluginServer/plugins/UltraLib/modules/UltraMagic",
            "/users/izzy6/Dropbox/AMS/TestPluginServer/plugins/UltraLib/modules/UltraMagic",
            "/users/izzy6/Dropbox/AMS/TestPluginServer/plugins/UltraLib/modules/ErrorFilter",
            "/users/izzy6/Dropbox/AMS/TestPluginServer/plugins/UltraLib/modules/Quests",
            "/users/izzy6/Dropbox/AMS/TestPluginServer/plugins/UltraLib/modules/Profiles",
            "/users/izzy6/Dropbox/AMS/TestPluginServer/plugins/UltraLib/modules/PvPManager",
            "/users/izzy6/Dropbox/AMS/TestPluginServer/plugins/UltraLib/modules/UltraChat"
    };

    public static void main(String[] args) {
        ArrayList<Server> servers = new ArrayList<>();
        servers.add(new Server("Creative", "158.69.1.87", 3184, "D712BDCCEFAC8288227C2B68C216D8D2"));
        for (Server server : servers) {
            NetworkConnection connection = NetworkConnection.getOrCreate(server.getIp(), server.getPort());
            if (connection != null) {
                connection.setListening(false);
                try {
                    connection.setEncryption("D712BDCCEFAC8288227C2B68C216D8D2");
                } catch (Exception e) {

                }
                server.getConnections().add(connection);
                //server.setupConnection(connection);
                connection.upload(new PacketCommand("identify", 3183, "2FA7D400514B58980157B1DD0E9DA86B"));
                System.out.println("Connection established: " + server);
                for (String file : files) {
                    try {
                        server.uploadFile(new File(file + ".jar"), file.replace("/users/izzy6/Dropbox/AMS/TestPluginServer/", "") + ".jar", true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            //NetworkConnection connection = server.getConnection();
        }

    }
}
