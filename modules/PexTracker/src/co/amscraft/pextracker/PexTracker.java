package co.amscraft.pextracker;

import co.amscraft.ultralib.modules.Module;
import org.bukkit.OfflinePlayer;
import ru.tehkode.permissions.*;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import javax.swing.*;
import java.util.*;

public class PexTracker extends Module {
    @Override
    public String[] getModuleDependancies() {
        return new String[0];
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }


    public static Map<String, String> getPlayers(String permission) {
        List<PermissionGroup> groups = new ArrayList<>();
        PermissionManager manager = PermissionsEx.getPermissionManager();
        for (PermissionGroup group: manager.getGroupList()) {
            if (group.has(permission)) {
                groups.add(group);
            }
        }
        Map<String, String> sources = new HashMap<>();
        for (PermissionUser user: manager.getUsers()) {
            if (user.has(permission)) {
                boolean put = false;
                for (PermissionGroup group : groups) {
                    if (user.inGroup(group)) {
                        sources.put(user.getName(), group.getName());
                        put = true;
                        break;
                    }
                }
                if (!put) {
                    sources.put(user.getName(), "Direct");
                }
            }
        }
        return sources;
    }
}
