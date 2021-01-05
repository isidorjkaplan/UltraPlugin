package co.amscraft.ultramagic;

import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.player.PlayerData;
import co.amscraft.ultralib.player.PlayerUtility;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultralib.tic.ServerTic;
import co.amscraft.ultramagic.events.GetBoundSpellsEvent;
import co.amscraft.ultramagic.events.GetPlayerManaEvent;
import co.amscraft.ultramagic.events.GetPlayerManaRegenEvent;
import co.amscraft.ultramagic.events.PlayerSpellCheckEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Izzy on 2017-10-15.
 */
public class MagicData extends PlayerData {
    private List<Spell> spells = new ArrayList<>();
    private Spell[] bound = new Spell[10];
    private boolean sidebar = false;
    private int maxMana = 100;
    private double mana = maxMana;
    private double manaRegen = 15;
    private BarStyle manaBarStyle = BarStyle.SEGMENTED_10;
    private BarColor manaBarColor = BarColor.PURPLE;
    private float cooldownReduction = 0;//Percent
    @FieldDescription(show = false, save = false)
    private BossBar bar = null;
    @FieldDescription(show = false, save = false)
    private long lastUpdate = System.currentTimeMillis();

    @ServerTic(isAsync = true, delay = 1)
    public static void updatePlayerSidebars() {
        //System.out.println("Sidebar");
        for (UltraPlayer player : UltraPlayer.getPlayers()) {
            try {
                MagicData data = player.getData(MagicData.class);
                if (data != null) {
                    data.updateSidebar();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @ServerTic(isAsync = true, delay = 0.5)
    public static void updatePlayerManas() {
        for (UltraPlayer player : UltraPlayer.getPlayers()) {
            try {
                //PlayerUtility utils = player.getData(PlayerUtility.class);
                MagicData data = player.getData(MagicData.class);
                //System.out.println(data + ", " + MagicData.class);
                if (data != null) {
                    long current = System.currentTimeMillis();
                    data.addMana(data.getManaRegen() * (current - data.getLastUpdate()) / 1000.0);
                    data.updateManaBar();
                    data.setLastUpdate(current);
                }
            } catch (Exception e) {
                //System.out.println(player.getData(MagicData.class));
                e.printStackTrace();
            }
        }
    }


    public void updateManaBar() {
        this.getBar().setColor(this.getManaBarColor());
        this.getBar().setStyle(this.getManaBarStyle());
        EditorSettings s = this.getPlayer().getData(EditorSettings.class);
        int max = this.getMaxMana();
        double mana = this.getMana();
        this.getBar().setTitle(s.getVariable() + "Mana" + s.getColon() + ": " + s.getValue() + (int)mana + s.getColon() + "/" + s.getValue() + max);
        if (mana != max) {
            this.getBar().setProgress(((mana / max)));
            if (!this.getBar().getPlayers().contains(this.getPlayer().getBukkit())) {
                this.getBar().addPlayer(this.getPlayer().getBukkit());
            }
        } else if (mana == max && this.getBar().getPlayers().contains(this.getPlayer().getBukkit())) {
            this.getBar().removePlayer(this.getPlayer().getBukkit());
        }
    }

    public void addMana(double mana) {
        //this.mana += mana;
        mana = this.getMana() + mana;
        int max = this.getMaxMana();
        if (mana > max) {
            mana = max;
        }
        this.setMana(mana);
    }

    public void sendStats(CommandSender sender) {
        EditorSettings s = EditorSettings.getSettings(sender);
        UltraPlayer player = this.getPlayer();
        sender.sendMessage(s.getVariable() + player.getBukkit().getName() + s.getValue() + "'s stats");
        sender.sendMessage(s.getVariable() + "Mana" + s.getColon() + ": " + s.getValue() + this.getMana() + "/" + this.getMaxMana());
        sender.sendMessage(s.getVariable() + "Spells" + s.getColon() + ": " + s.getValue() + this.getSpells());
       /* Spell[] bound = this.getBound();
        for (int i = 0; i < bound.length; i++) {

        }
        sender.sendMessage(s.getVariable() + "Active Spells" + s.getColon() + ": " + s.getValue());*/
    }


    public void useMana(double mana) {
        this.setMana(this.getMana() - mana);
        if (this.getMana() < 0) {
            this.setMana(0);
        }
    }

    public int getMaxMana() {
        return ((GetPlayerManaEvent) new GetPlayerManaEvent(this.getPlayer()).dispatch()).getMaxMana();
    }

    public void setMaxMana(int maxMana) {
        this.maxMana = maxMana;
    }

    public double getMana() {
        return this.mana;
    }

    public void setMana(double mana) {
        this.mana = mana;
    }

    public List<Spell> getSpells() {
        List<Spell> list = new ArrayList<>();
        for (Spell spell : Spell.getSpells()) {
            if (hasSpell(spell)) {
                list.add(spell);
            }
        }
        return list;
    }

    public void setSpells(List<Spell> spells) {
        this.spells = spells;
    }

    public List<Spell> getSpellsRaw() {
        return this.spells;
    }

    public void addSpell(Spell spell) {
        this.getSpellsRaw().add(spell);
        this.save();
    }

    public void removeSpell(Spell spell) {
        this.getSpellsRaw().remove(spell);
        this.save();
    }

    public Spell[] getBound() {
        return ((GetBoundSpellsEvent) new GetBoundSpellsEvent(this.getPlayer()).dispatch()).getBoundSpells();
    }

    public void setBound(Spell[] bound) {
        this.bound = bound;
    }

    public Spell[] getBoundRaw() {
        return this.bound;
    }

    public void updateSidebar() {
        //this.sidebar = false;
        Spell[] bound = getBound();
        if (isSidebar() && getPlayer().hasPermission("UltraLib.commands.magic")) {
            PlayerUtility utility = getPlayer().getData(PlayerUtility.class);
            EditorSettings settings = getPlayer().getData(EditorSettings.class);
            utility.setSidebar(1, settings.getVariable() + "Mana" + settings.getColon() + ": " + settings.getValue() + new DecimalFormat("#").format(this.getMana()));
            for (int i = 0; i < 9; i++) {
                String bar = i == this.getPlayer().getBukkit().getInventory().getHeldItemSlot() ? settings.getVariable() + " > " : settings.getColon() + " - ";
                if (bound[i] != null) {
                    bar += settings.getValue();
                    //if (!SpellInstance.canCast(bound[i], getPlayer().getBukkit())) {
                      //  bar += ChatColor.STRIKETHROUGH;
                    //}
                    bar += bound[i].name;

                }
                utility.setSidebar(i + 2, bar);
            }
        }
    }

    public Spell getActiveSpell() {
        //System.out.println(this.getPlayer().getBukkit().getInventory().getHeldItemSlot());
        //System.out.println(Arrays.asList(this.getBound()));
        return this.getBound()[this.getPlayer().getBukkit().getInventory().getHeldItemSlot()];
    }

    public boolean hasSpell(Spell spell) {
        PlayerSpellCheckEvent evt = new PlayerSpellCheckEvent(this.getPlayer(), spell);
        evt.dispatch();
        return evt.hasSpell();
    }

    public boolean isSidebar() {
        return sidebar;
    }

    public void setSidebar(boolean sidebar) {
        this.sidebar = sidebar;
    }

    public int getMaxManaRaw() {
        return this.maxMana;
    }

    public double getManaRegen() {
        GetPlayerManaRegenEvent evt = new GetPlayerManaRegenEvent(this.getPlayer());
        evt.dispatch();
        return evt.getManaRegen();
    }

    public double getManaRegenRaw() {
        return manaRegen;
    }

    public void setManaRegen(double manaRegen) {
        this.manaRegen = manaRegen;
    }

    public BarStyle getManaBarStyle() {
        return manaBarStyle;
    }

    public void setManaBarStyle(BarStyle manaBarStyle) {
        this.manaBarStyle = manaBarStyle;
    }

    public BarColor getManaBarColor() {
        return manaBarColor;
    }

    public void setManaBarColor(BarColor manaBarColor) {
        this.manaBarColor = manaBarColor;
    }

    public float getCooldownReduction() {
        return cooldownReduction;
    }

    public void setCooldownReduction(float cooldownReduction) {
        this.cooldownReduction = cooldownReduction;
    }

    public BossBar getBar() {
        if (this.bar == null) {
            this.bar = Bukkit.createBossBar("Bar", this.getManaBarColor(), this.getManaBarStyle());
        }
        return bar;
    }

    public void setBar(BossBar bar) {
        if (this.getBar() != null) {
            this.getBar().removeAll();
        }
        this.bar = bar;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }



}
