package co.amscraft.pvpmanager;

import co.amscraft.ultralib.player.PlayerData;

public class GameData extends PlayerData {
    public Game getGame() {
        for (Game game : Game.getGames()) {
            if (game.getPlayers().contains(this.getPlayer().getBukkit())) {
                return game;
            }
        }
        return null;
    }
}
