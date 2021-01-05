package co.amscraft.networking;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.modules.Module;
import co.amscraft.ultralib.network.FilePacket;
import co.amscraft.ultralib.network.PacketCommand;
import co.amscraft.ultralib.network.events.PacketCommandEvent;
import co.amscraft.ultralib.utils.ObjectUtils;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Networking extends Module {
    @Override
    public String[] getModuleDependancies() {
        return new String[0];
    }

    @Override
    public void onEnable() {
        PacketCommand.getCommands().put("players", new PacketCommand.Command() {
            @Override
            public void run(PacketCommandEvent evt) {
                if (Server.getServer(evt.getConnection()) != null) {
                    List<String> list = new ArrayList<>();
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        list.add(player.getName());
                    }
                    evt.getConnection().upload(list, evt.getPacket().getSender());
                }
            }
        });
        PacketCommand.getCommands().put("msg", new PacketCommand.Command() {
            @Override
            public void run(PacketCommandEvent evt) {
                if (Server.getServer(evt.getConnection()) != null) {
                    Object[] args = evt.getCommand().getArgs();
                    Player player = Bukkit.getPlayer(args[1].toString());
                    EditorSettings s = EditorSettings.getSettings(player);
                    player.sendMessage(s.getVariable() + args[0] + s.getColon() + " -> " + s.getVariable() + player.getName() + s.getColon() + ": " + s.getValue() + args[2]);
                }
            }
        });
        PacketCommand.getCommands().put("upload", new PacketCommand.Command() {
            @Override
            public void run(PacketCommandEvent evt) {
                Server server = Server.getServer(evt.getConnection());
                if (server != null) {
                    String path = evt.getCommand().getArgs()[0].toString();
                    boolean replace = Boolean.parseBoolean(evt.getCommand().getArgs()[2] + "");
                    String type = evt.getCommand().getArgs()[3].toString();
                    File file = new File(path + "." + type);
                    if (file.exists() && !replace) {
                        for (int i = 1; file.exists(); i++) {
                            file = new File(path + i + "." + type);
                        }
                    }
                    FilePacket packet = (FilePacket) evt.getCommand().getArgs()[1];
                    try {
                        packet.copyTo(file);
                        ObjectUtils.debug(Level.WARNING, "Downloaded file " + file.getPath() + " from server " + server.getName());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        PacketCommand.getCommands().put("identify", new PacketCommand.Command() {
            @Override
            public void run(PacketCommandEvent evt) {
                Server server = Server.getServer(evt.getConnection().getSocket().getInetAddress().getHostAddress(), (int) evt.getCommand().getArgs()[0]);
                if (server != null) {
                    if (server.getEncryption().equals(evt.getCommand().getArgs()[1] + "")) {
                        while (server.getConnections().contains(evt.getConnection())) {
                            server.getConnections().remove(evt.getConnection());
                        }
                        evt.getConnection().setEncryption(server.getEncryption());
                        server.getConnections().add(evt.getConnection());
                        ObjectUtils.debug(Level.INFO, "Network connection " + evt.getConnection() + " identified itself as server " + server);
                    } else {
                        ObjectUtils.debug(Level.WARNING, "Connection tried to identify as " + server.getName() + " with invalid encryption key: " + evt.getCommand().getArgs()[1]);
                    }
                } else {
                    ObjectUtils.debug(Level.WARNING, "Unidentified connection tried to identify as " + evt.getCommand().getArgs()[0] + ": " + evt.getConnection());
                }
            }
        });
        PacketCommand.getCommands().put("broadcast", new PacketCommand.Command() {
            @Override
            public void run(PacketCommandEvent evt) {
                if (Server.getServer(evt.getConnection()) != null) {
                    for (String player : (List<String>) evt.getCommand().getArgs()[0]) {
                        if (Bukkit.getPlayer(player) != null) {
                            EditorSettings s = EditorSettings.getSettings(Bukkit.getPlayer(player));
                            Bukkit.getPlayer(player).sendMessage(((boolean) evt.getCommand().getArgs()[2] ? (s.getColon() + "(" + s.getValue() + Server.getServer(evt.getConnection()).getName() + s.getColon() + ") " + s.getValue()) : s.getValue()) + ChatColor.translateAlternateColorCodes('&', evt.getCommand().getArgs()[1] + ""));
                        }
                    }
                }
            }
        });
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(PacketAdapter.params(UltraLib.getInstance(), PacketType.Status.Server.OUT_SERVER_INFO).optionAsync()) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        WrappedServerPing serverPing = event.getPacket().getServerPings().read(0);
                        serverPing.setPlayersOnline(Server.getOnlinePlayers().size());
                        serverPing.setPlayersMaximum(Bukkit.getMaxPlayers());
                    }
                });
    }

    @Override
    public void onDisable() {

    }


}
