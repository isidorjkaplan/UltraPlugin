package co.amscraft.ontime;

import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultralib.tic.ServerTic;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OntimeListener implements Listener {

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent evt) {
        OntimeData data = UltraPlayer.getPlayer(evt.getPlayer()).getData(OntimeData.class);
        if (!data.isToday()) {
            data.setDaily(0);
            data.setToday();
            data.getGivenRewards().clear();
            //evt.getPlayer().get
        }
    }

    @EventHandler
    public static void onPlayerAFK(AfkStatusChangeEvent evt) {
        if (!evt.getValue()) {
            OntimeData data = UltraPlayer.getPlayer(evt.getAffected().getName()).getData(OntimeData.class);
            data.setAfk(data.getAfk() + ((System.currentTimeMillis() - evt.getAffected().getAfkSince()) / 1000));
        }
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent evt) {
        OntimeData data = UltraPlayer.getPlayer(evt.getPlayer()).getData(OntimeData.class);
        data.addTime(data.getCurrentOntime());
    }

    @ServerTic(delay = 2)
    public static void onServerTick() {
        for (UltraPlayer player : UltraPlayer.getPlayers()) {
            OntimeData data = player.getData(OntimeData.class);
            for (int reward : OnTime.getOntimeConfig().dailyRewards.keySet()) {
                if (reward <= data.getDailyOntime() && !data.getGivenRewards().contains((Object) reward)) {
                    data.getGivenRewards().add(reward);
                    for (String command : OnTime.getOntimeConfig().dailyRewards.get(reward)) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("<player>", player.getBukkit().getName()));
                    }
                }
            }
        }
    }
}
