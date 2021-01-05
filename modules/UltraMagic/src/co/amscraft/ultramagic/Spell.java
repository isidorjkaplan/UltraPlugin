package co.amscraft.ultramagic;

import co.amscraft.ultralib.UltraObject;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultramagic.actions.Action;
import co.amscraft.ultramagic.actions.ParentAction;
import co.amscraft.ultramagic.wands.WandObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

/**
 * Created by Izzy on 2017-10-01.
 */
public class Spell extends UltraObject {
    @FieldDescription(help = "The name of the spell")
    public String name = "spell";
    @FieldDescription(help = "The spell's cooldown!", unit = "seconds")
    public double cooldown = 0;
    @FieldDescription(help = "The magical energy required to use this spell", unit = "mana")
    public int mana = 0;

    @FieldDescription(help = "The URL of the spell icon (Must point to a player skin file to grab their skull)")
    public String icon = "http://textures.minecraft.net/texture/505b13370fac6a8caa93b6ccb225b11d49ae9eb0e8d7fb84c628c82e234328";

    @FieldDescription(help = "The description for the spell")
    public String description = "A magical spell!";

    @FieldDescription(help = "The items that must be held to cast the spell. Leave blank for no item requirement!")
    public List<Material> materials = new ArrayList<>();

   /* @FieldDescription(help = "The amount of time until the spell will undo any damage done by the spell!", unit = "seconds")
    public double undo = 10;*/

    @FieldDescription(help = "The actions that are included in the spell!")
    public List<Action> actions = new ArrayList<>();

    public static Set<Spell> getSpells() {
        return UltraObject.getList(Spell.class);
    }

    public static Spell getSpell(String name) {
        //System.out.println(UltraObject.getObject(Spell.class, "name", name));
        for (Spell spell : Spell.getSpells()) {
            if (spell.getName().equalsIgnoreCase(name)) {
                return spell;
            }
        }
        return null;
    }


    public static Spell getSpell(int id) {
        return UltraObject.getObject(Spell.class, "ID", id);
    }

    private static List<Action> getActions(Action action) {
        List<Action> list = new ArrayList<>();
        if (action instanceof ParentAction) {
            list.addAll(((ParentAction) action).actions);
        }
        return getActionsRecursivly(list);
    }

    /**
     * A recursive meathod that returns all of the sub-actions of any list of actions.
     *
     * @param level The starting level
     * @return The subactions of that level
     */

    public static List<Action> getActionsRecursivly(List<Action> level) {
        List<Action> list = new ArrayList<>();
        for (Action action : level) {
            list.add(action);
            if (action instanceof ParentAction) {
                list.addAll(getActionsRecursivly(((ParentAction) action).actions));
            }
        }
        return list;
    }

    public SpellInstance cast(Target caster) {
        return SpellInstance.castSpell(this, caster);
    }

    public String getName() {
        return this.name;
    }

    public List<Material> getHeldItems() {
        return this.materials;
    }

    public ItemStack getItem(EditorSettings s) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
        WandObject wand = new WandObject();
        wand.addSpell(this);
        head = wand.save(head);
        ItemMeta meta = head.getItemMeta();
        if (s == null) {
            s = UltraPlayer.getConsole().getData(EditorSettings.class);
        }
        meta.setDisplayName(s.getVariable() + this.name);
        List<String> lore = new ArrayList<>();
        lore.add(s.getVariable() + "Mana" + s.getColon() + ": " + s.getValue() + this.mana);
        lore.add(s.getVariable() + "Cooldown" + s.getColon() + ": " + s.getValue() + this.cooldown + " seconds");
        lore.add(s.getVariable() + "Description" + s.getColon() + ": ");
        String[] words = this.description.split(" ");
        String line = "";
        int length = 0;
        String color = s.getValue() + "";
        for (String word : words) {
            line += word + " ";
            length += word.length();
            if (length >= 20) {
                lore.add(color + line);
                length = 0;
                try {
                    if (line.contains("§")) {
                        color = "§" + Character.toString(line.charAt(line.lastIndexOf("§") + 1));
                    }
                } catch (Exception e) {

                }
                line = "";
            }
        }
        if (!line.equals("")) {
            lore.add(s.getValue() + line);
        }
        meta.setLore(lore);
        head.setItemMeta(meta);

        try {
            URL url = new URL(this.icon);
            SkullMeta headMeta = (SkullMeta) head.getItemMeta();
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());//Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
            profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
            Field profileField = null;
            try {
                profileField = headMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(headMeta, profile);
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
                e1.printStackTrace();
            }
            head.setItemMeta(headMeta);
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return head;
    }

    @Override
    public void delete() {
        File file = new File("plugins/Magic/spells.yml");
        if (file.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            config.set("U-" + this.getName(), null);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "magic:magic load");
        }
        super.delete();

    }

   /* @Override
    public void save() {
        super.save();
       /* new Thread() {
            public void run() {
                UltraMagic.createSpellAliases();
            }
        }.start();
    }*/

    public List<Action> getAllActions() {
        return getActionsRecursivly(this.actions);
    }

    @Override
    public String toString() {
        return this.getName();
    }

}
