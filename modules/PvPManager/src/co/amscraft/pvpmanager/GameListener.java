package co.amscraft.pvpmanager;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.PlayerUtility;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class GameListener implements Listener {
    @EventHandler
    public static void onPlayerQuitEvent(PlayerQuitEvent evt) {
        Game game = Game.getGame(evt.getPlayer());
        if (game != null) {
            Team team = game.getTeam(evt.getPlayer());
            if (team.getPlayers().size() == 1) {
                new BukkitRunnable() {
                    public void run() {
                        try {
                            if (team.getPlayers().isEmpty()) {
                                game.removeTeam(team);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.runTaskLater(UltraLib.getInstance(), 20 * 60);
            }
            //team.removePlayer(evt.getPlayer());
        }
    }

    @EventHandler
    public static void onPlayerMove(PlayerMoveEvent evt) {
        Game game = Game.getGame(evt.getPlayer());
        if (game != null && !game.getArena().isAllowExit() && !game.getArena().getShape().isInside(evt.getTo())) {
            //evt.setCancelled(true);
            if (!game.getArena().getShape().isInside(evt.getFrom())) {
                evt.setTo(game.getArena().getSpawns().get(0).getLocation());
            } else {
                evt.setCancelled(true);
            }
            // evt.getPlayer().teleport(game.getArena().getLounge().getLocation());

            //}
            UltraPlayer.getPlayer(evt.getPlayer()).getData(PlayerUtility.class).sendActionbar(EditorSettings.getSettings(evt.getPlayer()).getError() + "You cannot leave the arena during a combat match!");
        }
    }

    /*@EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent evt) {
        Game game = Game.getGame(evt.getPlayer());
        if (game != null) {
            evt.setRespawnLocation(game.getArena().getLounge().getLocation());
        }
    }*/

    @EventHandler
    public static void onEntityDamageByEntity(EntityDamageByEntityEvent evt) {
        if (evt.getDamager() instanceof Player & evt.getDamager() != evt.getEntity()) {
            Game game = Game.getGame((Player) evt.getDamager());
            if (game != null && game.getArena().isProtectingTeam()) {
                Team team = game.getTeam((Player) evt.getDamager());
                if (team.getPlayers().contains(evt.getEntity())) {
                    evt.setCancelled(true);
                    UltraPlayer.getPlayer(evt.getDamager()).getData(PlayerUtility.class).sendActionbar(EditorSettings.getSettings((Player) evt.getDamager()).getError() + "You cannot attack your teammates!");
                }
            } else if (game != null && !game.hasStarted()) {
                evt.setCancelled(true);
                UltraPlayer.getPlayer(evt.getDamager()).getData(PlayerUtility.class).sendActionbar(EditorSettings.getSettings((Player) evt.getDamager()).getError() + "The game has not yet started!");
            }
        }
    }

    @EventHandler
    public static void onSpellCastEvent(co.amscraft.ultramagic.events.SpellCastEvent evt) {
        if (evt.getCaster().getObject() instanceof Player) {
            Game game = Game.getGame((Player) evt.getCaster().getObject());
            if (game != null && (!game.getArena().isAllowingSpells() || !game.hasStarted())) {
                evt.setCancelled(true);
                if (!game.getArena().isAllowingSpells()) {
                    evt.setFailMessage("You cannot cast spells in this game!");
                } else {
                    evt.setFailMessage("The game has not yet started");
                }
            }
        }
    }


    @EventHandler
    public static void onPlayerDeathEvent(PlayerDeathEvent evt) {
        if (evt.getEntity() != null && evt.getEntity() instanceof Player) {
            Game game = Game.getGame(evt.getEntity());
            if (game != null && game.hasStarted()) {
                Team team = game.getTeam(evt.getEntity());
                team.addDeath(evt.getEntity());
            }
        }
    }

}
