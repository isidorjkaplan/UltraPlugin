package co.amscraft.antibot;

import co.amscraft.ontime.OntimeData;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.PlayerData;
import co.amscraft.ultralib.player.UltraPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.Set;

public class BotData extends PlayerData implements Listener {
    private long messages = 0;
    private double blocksMoved = 0;
    private long timesJoined = 0;

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent evt) {
        try {
            UltraPlayer p = UltraPlayer.getPlayer(evt.getPlayer());
            if (p != null) {
                BotData data = p.getData(BotData.class);
                data.timesJoined++;
                IPGraph.getGraph().addConnection(evt.getPlayer().getUniqueId(), evt.getPlayer().getAddress().getHostString());
                Set<IPGraph.Path> alts = IPGraph.getGraph().getAllPathsList(evt.getPlayer().getUniqueId(), 1);
                if (!alts.isEmpty()) {
                    Set<String> players = new HashSet<>();
                    for (IPGraph.Path path : alts) {
                        if (path.getTopVertex() instanceof IPGraph.PlayerVertex) {
                            players.add(Bukkit.getOfflinePlayer(((IPGraph.PlayerVertex) path.getTopVertex()).getPlayer()).getName());
                        }
                    }
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (player.hasPermission("ultralib.antibot.notify")) {
                            EditorSettings s = EditorSettings.getSettings(player);
                            TextComponent component = new TextComponent(s.getError() + "WARNING" + s.getColon() + ": " + s.getHelp() + "Player " + s.getValue() + evt.getPlayer().getName() + s.getHelp() + " may be alts of " + players);
                            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(s.getHelp() + "Click for more info")}));
                            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ip lookup " + evt.getPlayer().getName()));
                            component.setColor(ChatColor.getByChar(s.getHelp().charAt(1)));
                            player.spigot().sendMessage(component);
                        }
                    }
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    @EventHandler
    public static void onPlayerMove(PlayerMoveEvent evt) {
        if (!evt.isCancelled()) {
            BotData data = UltraPlayer.getPlayer(evt.getPlayer()).getData(BotData.class);
            double move = evt.getTo().distance(evt.getFrom());
            if (move < 5) {
                data.blocksMoved += move;
            }
        }
    }

    @EventHandler
    public static void onPlayerChat(AsyncPlayerChatEvent evt) {
        BotData data = UltraPlayer.getPlayer(evt.getPlayer()).getData(BotData.class);
        data.messages++;
    }

    public double getBlocksMoved() {
        return blocksMoved;
    }

    public long getTimesJoined() {
        return timesJoined;
    }

    public long getMessages() {
        return messages;
    }

    /**
     * @return Messages per second
     */
    public double getMessagesRate() {
        return (double) messages / getOntime();
    }

    private long getOntime() {
        return this.getPlayer().getData(OntimeData.class).getTotalOntime();
    }

    /**
     * @return Blocks per second
     */
    public double getAverageSpeed() {
        return (double) blocksMoved / getOntime();
    }

}
