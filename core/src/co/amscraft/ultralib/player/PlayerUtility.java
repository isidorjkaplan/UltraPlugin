package co.amscraft.ultralib.player;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.tic.ServerTic;
import io.puharesource.mc.titlemanager.TitleManagerPlugin;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Izzy on 2017-10-03.
 */
public class PlayerUtility extends PlayerData {
    private HashMap<String, Long> cooldowns = new HashMap<>();
    @FieldDescription(save = false)
    private boolean actionbarSent = false;

    @ServerTic(isAsync = true)
    public static void updateActionbars() {
        for (UltraPlayer player : UltraPlayer.getPlayers()) {
            PlayerUtility utils = player.getData(PlayerUtility.class);
            if (utils != null && utils.actionbarSent && utils.getCooldown("actionbar") == 0) {
                utils.clearActionbar();
            }
        }
    }

    public HashMap<String, Long> getCooldowns() {
        return cooldowns;
    }

    public void sendActionbar(String message, double duration) {
        TitleManagerPlugin plugin = TitleManagerPlugin.getPlugin(TitleManagerPlugin.class);
        plugin.sendActionbar(this.getPlayer().getBukkit(), message);
        this.setCooldown("actionbar", duration);
        this.actionbarSent = true;
    }

    public void sendActionbar(String message) {
        sendActionbar(message, 1);
    }

    public void setSidebar(int slot, String message) {
        TitleManagerPlugin api = TitleManagerPlugin.getPlugin(TitleManagerPlugin.class);
        if (!api.hasScoreboard(this.getPlayer().getBukkit())) {
            api.giveScoreboard(this.getPlayer().getBukkit());
        }
        String[] id = new String[]{"a", "b", "c", "d", "e", "f", "0", "1", "2", "3", "4", "5", "6", "7", "8"};
        api.setScoreboardValue(this.getPlayer().getBukkit(), slot, "§" + id[slot] + message);
    }

    public void clearSidebar() {
        TitleManagerPlugin api = TitleManagerPlugin.getPlugin(TitleManagerPlugin.class);
        api.removeScoreboard(this.getPlayer().getBukkit());
    }

    public void clearActionbar() {
        this.setCooldown("actionbar", 0);
        this.actionbarSent = false;
        JavaPlugin.getPlugin(TitleManagerPlugin.class).clearActionbar(this.getPlayer().getBukkit());
    }

    public void setCooldown(String key, double seconds) {
        if (seconds <= 0) {
            cooldowns.remove(key);
        } else {
            cooldowns.put(key, System.currentTimeMillis() + (long) (seconds * 1000));
        }
    }


    public List<String> getAllPermissions() {

        /*Player player = this.getPlayer().getBukkit();
        ru.tehkode.permissions.PermissionUser user = ru.tehkode.permissions.bukkit.PermissionsEx.getUser(player);
        List<String> list = new ArrayList<>(user.getPermissions(player.getWorld().getName()));
        for (ru.tehkode.permissions.PermissionGroup group: user.getRankLadders().values()) {
            list.addAll(group.getPermissions(player.getWorld().getName()));
        }
        for (int i = 0; i < list.size(); i++) {
            list.set(i, list.get(i).toLowerCase());
        }
        return list;*/
        List<String> perms = new ArrayList<>();
        for (PermissionAttachmentInfo perm : this.getPlayer().getBukkit().getEffectivePermissions()) {
            perms.add(perm.getPermission());
        }
        return perms;
    }


    public boolean hasPermission(String node) {
        node = node.toLowerCase();
        for (String string: this.getAllPermissions()) {
            if (string.equals(node)) {
                return true;
            }
        }
        return false;
    }

    public void updateCooldowns() {
        if (cooldowns == null) {
            cooldowns = new HashMap<>();
        }
        long now = System.currentTimeMillis();
        for (String key : new HashSet<>(cooldowns.keySet())) {
            if (cooldowns.get(key) == null) {
                cooldowns.remove(key);
            } else if (cooldowns.get(key) <= now) {
                cooldowns.remove(key);
            }
        }
    }

    public double getCooldown(String key) {
        if (cooldowns == null) {
            cooldowns = new HashMap<>();
        }
        updateCooldowns();
        long now = System.currentTimeMillis();
        long time = cooldowns.getOrDefault(key, now);
        return (time - now) / 1000.0;
    }


}
