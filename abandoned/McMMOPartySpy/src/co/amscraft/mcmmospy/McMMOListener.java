package co.amscraft.mcmmospy;

import co.amscraft.ultrachat.ChatData;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.UltraPlayer;
import com.gmail.nossr50.api.PartyAPI;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;
import com.gmail.nossr50.mcMMO;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class McMMOListener implements Listener {

    @EventHandler
    public void onChatEvent(McMMOPartyChatEvent evt) {
        //System.out.println("McMMOSPY: " + evt.getSender() + ", " + evt.getParty() +", " + evt.getMessage());
        mcMMO plugin = JavaPlugin.getPlugin(mcMMO.class);
        List<Player> players = PartyAPI.getOnlineMembers(evt.getParty());
        Player sender = Bukkit.getPlayer(evt.getSender());
        for (UltraPlayer player : UltraPlayer.getPlayers()) {
            if (!players.contains(player.getBukkit()) && player.getData(ChatData.class).isSpying()) {
                EditorSettings s = player.getData(EditorSettings.class);
                player.getBukkit().sendMessage(s.getColon() + "[" + s.getVariable() + evt.getParty() + s.getColon() + "] (" + s.getValue() + sender.getDisplayName() + s.getColon() + ") " + s.getHelp() + evt.getMessage());
            }
        }

    }
}
