package co.amscraft.rpgitems;

import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.player.PlayerUtility;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultralib.utils.ObjectUtils;
import co.amscraft.ultramagic.MagicData;
import co.amscraft.ultramagic.Spell;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.exceptions.InvalidTargetException;
import org.bukkit.entity.Player;

public class RPGItemPower {
    @FieldDescription (help = "Set to -1 for spell default")
    private int cooldown = -1;
    @FieldDescription(help = "Weather or not the spell should use mana")
    private boolean useMana = false;
    @FieldDescription(help = "The Spell must exist and have identical capitlization or it will not work")
    private String spell = "<name>";
    private Action action = Action.RIGHT_CLICK;
    private boolean useDurability = true;

    public boolean isUseDurability() {
        return useDurability;
    }

    public Action getAction() {
        return action;
    }

    public boolean cast(Player player) {
        EditorSettings s = EditorSettings.getSettings(player);
        Target t;
        Spell spell = Spell.getSpell(this.spell);
        if (spell != null) {
            UltraPlayer uplayer = UltraPlayer.getPlayer(player);
            PlayerUtility uptil = uplayer.getData(PlayerUtility.class);
            MagicData data = uplayer.getData(MagicData.class);
            double time = uptil.getCooldown(toString());
            if (time == 0 ) {
                if ((!useMana || data.getMana() > spell.mana)) {
                    try {
                        t = new Target<Player>(player);
                        new Thread() {
                            public void run() {
                                new SpellInstance(t, spell);
                            }
                        }.start();
                        uptil.setCooldown(toString(), cooldown != -1 ? cooldown : spell.cooldown);
                        if (useMana) {
                            data.useMana(spell.mana);
                        }
                        return true;
                    } catch (InvalidTargetException | NullPointerException e) {
                        e.printStackTrace();
                    }
                } else {
                    uptil.sendActionbar(s.getError() + "You do not have enough mana to use this power!");
                }
            } else {
                uptil.sendActionbar(s.getError() + "You must wait " + time + " more seconds until using this skill!");
            }

        }
        return false;
    }

    public enum Action {
        RIGHT_CLICK, LEFT_CLICK, TAKE_DAMAGE, SHIFT, TICK
    }

    @Override
    public String toString() {
        return ObjectUtils.toString(this);
    }
}
