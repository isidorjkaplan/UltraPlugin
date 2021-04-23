package co.amscraft.magicranks;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultralib.utils.savevar.SaveVar;
import co.amscraft.ultramagic.events.GetPlayerManaEvent;
import co.amscraft.ultramagic.events.PlayerSpellCheckEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class RanksListener implements Listener {
    @SaveVar
    public static boolean ENABLE_PERMISSIONS = false;

    @EventHandler
    public static void onSpellCheckEvent(PlayerSpellCheckEvent evt) {
        if (!evt.hasSpell()) {
            /*PermissionUser user = PermissionsEx.getUser(evt.getPlayer().getBukkit());
            //System.out.println(user.getPermissions(evt.getPlayer().getBukkit().getWorld().getName()));*/

            if (ENABLE_PERMISSIONS && evt.getPlayer().hasPermission("ultraLib.magic.spell." + evt.getSpell().getName())) {
                evt.setHasSpell(true);
                return;
            }
            for (MagicRank rank : UltraObject.getList(MagicRank.class)) {
                for (String group : rank.getRanks()) {
                    if (evt.getPlayer().hasPermission("group." + group) && rank.getSpells().contains(evt.getSpell())) {
                        evt.setHasSpell(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public static void getMaxManaEvent(GetPlayerManaEvent evt) {
        //PermissionUser user = PermissionsEx.getUser(evt.getPlayer().getBukkit());
        for (MagicRank rank : UltraObject.getList(MagicRank.class)) {
            for (String group : rank.getRanks()) {
                if (evt.getPlayer().hasPermission("group." + group) && rank.getMaxMana() > evt.getMaxMana()) {
                    evt.setMaxMana(rank.getMaxMana());
                    break;
                }
            }
        }
    }
}
