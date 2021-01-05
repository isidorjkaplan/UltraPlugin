package co.amscraft.ultramagic.main.actions;


import co.amscraft.ultralib.editor.EditorCheck;
import co.amscraft.ultramagic.Spell;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.Action;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

/**
 * Created by Izzy on 2017-06-23.
 */
public class CastAction extends Action {
    public String spell_name = "spell";

    public CastAction() {
        try {
            EditorCheck.register(CastAction.class.getField("spell_name"), new EditorCheck("You must enter a valid spell") {
                @Override
                public boolean check(Object object, CommandSender sender) {
                    return Spell.getSpell(object + "") != null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        if (target.getLocation().getY() < Bukkit.getWorlds().get(0).getMaxHeight() && target.getLocation().getY() > 0) {
            new SpellInstance(target, Spell.getSpell(spell_name));
        }
    }


}
