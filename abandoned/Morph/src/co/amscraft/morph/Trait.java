package co.amscraft.morph;

import org.bukkit.entity.Player;

public abstract class Trait {
    public abstract void onMorph(Player player);

    public abstract void onUnmorph(Player player);
}