package co.amscraft.ultramagic;


import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.PlayerUtility;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultralib.tic.ServerTic;
import co.amscraft.ultramagic.events.GetBoundSpellsEvent;
import co.amscraft.ultramagic.events.GetPlayerManaEvent;
import co.amscraft.ultramagic.events.GetPlayerManaRegenEvent;
import co.amscraft.ultramagic.events.SpellCastEvent;
import co.amscraft.ultramagic.wands.WandObject;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.PermissionsUserData;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Izzy on 2017-10-18.
 */
public class MagicListener implements Listener {

    @ServerTic(delay = 1, isAsync = true)
    public static void spellEndListener() {
        try {
            for (List<SpellInstance> spells : SpellInstance.instances.values().toArray(new List[SpellInstance.instances.size()])) {
                for (SpellInstance instance : spells.toArray(new SpellInstance[spells.size()])) {
                    try {
                        if (instance.isFinished()) {
                            instance.end();
                        }
                    } catch (Exception e) {

                    }
                }
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    @EventHandler
    public static void onPlayerManaCheckEvent(GetPlayerManaEvent evt) {
        int mana = -1;
        for (String string: evt.getPlayer().getData(PlayerUtility.class).getAllPermissions()) {
            String s = string.toLowerCase();
            if (s.startsWith("ultramagic.mana.")) {
                try {
                    int perm = Integer.parseInt(s.replace("ultramagic.mana.", ""));
                    if (perm > mana) {
                        mana = perm;
                    }
                } catch (Exception e) {

                }
            }
        }
        if (mana >= 0) {
            evt.setMaxMana(mana);
        }
    }

    @EventHandler
    public static void onPlayerManaRegenEvent(GetPlayerManaRegenEvent evt) {

        double mana = -1;
        for (String string: evt.getPlayer().getData(PlayerUtility.class).getAllPermissions()) {
            String s = string.toLowerCase();
            if (s.startsWith("ultramagic.regen.")) {
                try {
                    double perm = Double.parseDouble(s.replace("ultramagic.regen.", ""));
                    if (perm > mana) {
                        mana = perm;
                    }
                } catch (Exception e) {

                }
            }
        }
        if (mana >= 0) {
            evt.setManaRegen(mana);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent evt) {
        UltraPlayer.getPlayer(evt.getPlayer()).getData(MagicData.class).getBar().removeAll();
    }



    /*@EventHandler
    public void onPlayerExplodePainfullyEvent(AsyncPlayerChatEvent evt) {
        if (evt.getMessage().equals("cast_bound")) {
            Spell spell = UltraPlayer.getPlayer(evt.getPlayer()).getData(MagicData.class).getActiveSpell();
            if (spell != null) {
                try {
                    spell.cast(new Target(evt.getPlayer()));
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (evt.getMessage().contains("bind: ")) {
            Spell spell = Spell.getSpell(evt.getMessage().replace("bind: ", ""));
            ItemStack stack= WandInterface.addSpell(evt.getPlayer().getInventory().getItemInMainHand(), spell);
            evt.getPlayer().sendMessage(spell + " - " +stack.toString());
            evt.getPlayer().getInventory().setItemInMainHand(stack);
        } else if (evt.getMessage().contains("info: ")) {
            try {
                evt.getPlayer().sendMessage(NMSUtils.read(evt.getPlayer().getInventory().getItemInMainHand(), evt.getMessage().replace("info: ", ""), String.class).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (Spell spell : Spell.getSpells()) {
            if (spell.name.equalsIgnoreCase(evt.getMessage())) {
                try {
                    spell.cast(new Target(evt.getPlayer()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }*/

    @EventHandler
    public static void onFinishEditingWand(InventoryCloseEvent evt) {
        if (evt.getView().getTitle().equals("Editing Spells")) {
            ItemStack stack = evt.getPlayer().getInventory().getItemInMainHand();
            //stack = WandInterface.finalizeWand(stack, evt.getInventory());
            EditorSettings s = EditorSettings.getSettings(evt.getPlayer());
            if (stack != null) {
                WandObject wand = WandObject.load(stack);
                wand.load(evt.getInventory());
                stack = wand.save(stack);
                evt.getPlayer().getInventory().setItemInMainHand(stack);
                evt.getPlayer().sendMessage(s.getSuccess() + "Successfully updated your wand!");
            } else {
                evt.getPlayer().sendMessage(s.getError() + "There was an error saving your wand!");
            }
        }
    }

    @EventHandler
    public static void onPlaceItemInInventory(InventoryClickEvent evt) {
        //System.out.println(evt.getInventory().getTitle().equals("Editing Spells") + ", " +  WandInterface.getSpellFromIcon(evt.getCurrentItem()) + ", " +  evt);
        if (evt.getView().getTitle().equals("Editing Spells")) {
            Spell spell = WandObject.getSpellFromIcon(evt.getCurrentItem());
            if (evt.getAction().toString().startsWith("PICKUP")) {
                if (WandObject.getSpellFromIcon(evt.getCurrentItem()) == null) {
                    evt.setCancelled(true);
                }
            } else if (evt.getAction().toString().startsWith("PLACE")) {
                if (evt.getSlot() >= 54 && spell != null) {
                    evt.setCancelled(true);
                }
            } else {
                evt.setCancelled(true);
            }
        }
    }

    @EventHandler
    public static void onPlayerDropItemEvent(PlayerDropItemEvent evt) {
        if (!evt.isCancelled() && WandObject.getSpellFromIcon(evt.getItemDrop().getItemStack()) != null) {
            evt.getItemDrop().setItemStack(null);
            //evt.getPlayer().sendMessage(EditorSettings.getSettings(evt.getPlayer()).getError() + "You cannot throw a spell from your inventory!");
        }
    }

    @EventHandler
    public static void onPlayerShiftEvent(PlayerToggleSneakEvent evt) {
        if (evt.isSneaking()) {
            ItemStack stack = evt.getPlayer().getInventory().getItemInMainHand();
            if (WandObject.isWand(stack)) {
                WandObject wand = WandObject.load(stack);
                if (wand != null && wand.shift) {
                    List<Spell> spells = Arrays.asList(wand.getSpells().toArray(new Spell[wand.getSpells().size()]));
                    Spell spell = spells.get((spells.indexOf(wand.getBound()) + 1) % spells.size());
                    wand.setBound(spell);
                    stack = wand.save(stack);
                    evt.getPlayer().getInventory().setItemInMainHand(stack);
                }
            }
        }
    }

    @EventHandler
    public static void onPlayerDeathEvent(PlayerDeathEvent evt) {
        if (evt.getEntity() != null && UltraPlayer.getPlayer(evt.getEntity()) != null) {
            MagicData data = UltraPlayer.getPlayer(evt.getEntity()).getData(MagicData.class);
            if (data.getBar() != null) {
                data.getBar().removePlayer((Player) evt.getEntity());
            }
        }
    }

    @EventHandler
    public void getBoundSpells(GetBoundSpellsEvent evt) {
        for (int i = 0; i < 10; i++) {
            ItemStack stack = evt.getPlayer().getBukkit().getInventory().getItem(i);
            //System.out.println(stack);

            if (WandObject.isWand(stack)) {
                WandObject wand = WandObject.load(stack);
                if (wand != null) {
                    evt.set(i, wand.getBound());
                }
            }
        }
    }

    @EventHandler
    public void spellCooldownCheckEvent(SpellCastEvent evt) {
        if (!evt.isCancelled() && evt.getCaster().getObject() instanceof Player) {
            UltraPlayer player = UltraPlayer.getPlayer(evt.getCaster().getName());
            MagicData data = player.getData(MagicData.class);
            if (!data.hasSpell(evt.getSpell())) {
                evt.setCancelled(true);
                evt.setFailMessage("You must have the spell unlocked to cast it!");
            } else {
                double cooldown = player.getData(PlayerUtility.class).getCooldown("Spell: " + evt.getSpell().name);
                if (cooldown > 0) {
                    evt.setCancelled(true);
                    evt.setFailMessage("You must wait " + cooldown + " seconds before you are able to cast this spell again!");
                } else if (data.getMana() < evt.getSpell().mana) {
                    evt.setCancelled(true);
                    evt.setFailMessage("You do not have enough mana to cast this spell!");
                }
            }
        }
    }

    @EventHandler
    public void spellItemHeldEvent(SpellCastEvent evt) {
        if (!evt.isCancelled() && evt.getCaster().getObject() instanceof Player) {
            Player player = ((Player) evt.getCaster().getObject());
            ItemStack stack = player.getInventory().getItemInMainHand();
            if (!evt.getSpell().materials.isEmpty() && !evt.getSpell().materials.contains(stack.getType())) {
                evt.setCancelled(true);
                evt.setFailMessage("You must be holding one of the following items to cast this spell: " + evt.getSpell().getHeldItems());
            }
        }
    }

    @EventHandler
    public void onPlayerOpenWandEvent(PlayerDropItemEvent evt) {
        if (WandObject.isWand(evt.getItemDrop().getItemStack())) {
            UltraPlayer.getPlayer(evt.getPlayer()).getData(MagicData.class).setLastDropEvent(System.currentTimeMillis());
            evt.setCancelled(true);
            //evt.getPlayer().getInventory().setItemInMainHand(evt.getItemDrop().getItemStack());
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (evt.getPlayer().getInventory().getItemInMainHand().equals(evt.getItemDrop().getItemStack())) {
                        WandObject.load(evt.getItemDrop().getItemStack()).open(evt.getPlayer());
                    }
                }
            }.runTaskLater(UltraLib.getInstance(), 1);
        }
    }

    //PID 19818
    @EventHandler
    public void onItemClick(InventoryClickEvent evt) {
        UltraPlayer ultraPlayer = UltraPlayer.getPlayer(evt.getWhoClicked().getName());
        if (evt.getView().getTitle().equals("Spells")) {
            if (ultraPlayer.getBukkit().getInventory().getItemInMainHand() != null && ultraPlayer.getBukkit().getInventory().getItemInMainHand().getType() != Material.AIR && WandObject.isWand(ultraPlayer.getBukkit().getInventory().getItemInMainHand())) {
                Spell spell = WandObject.getSpellFromIcon(evt.getCurrentItem());
                if (spell != null) {
                    ultraPlayer.getBukkit().closeInventory();
                    WandObject wand = WandObject.load(ultraPlayer.getBukkit().getInventory().getItemInMainHand());
                    wand.setBound(spell);
                    ultraPlayer.getBukkit().getInventory().setItemInMainHand(wand.save(ultraPlayer.getBukkit().getInventory().getItemInMainHand()));
                    //ultraPlayer.getBukkit().getInventory().setItemInMainHand(WandInterface.setBound(ultraPlayer.getBukkit().getInventory().getItemInMainHand(), spell));

                }
            }
            evt.setCancelled(true);
        } else {
            ItemStack item = evt.getWhoClicked().getInventory().getItemInMainHand();
            if (WandObject.isWand(item) && WandObject.load(item).isOpen()) {
                evt.setCancelled(true);
                //System.out.println(evt.getAction());

                Spell spell = WandObject.getSpellFromIcon(evt.getCurrentItem().getType() != Material.AIR?evt.getCurrentItem():evt.getCursor());
                //System.out.println(evt.getCurrentItem() + ", " + evt.getCursor() + ", " + spell + ", " + evt.getAction());
                if (evt.getAction() == InventoryAction.PLACE_ALL && spell != null && !evt.getCurrentItem().equals(item)) {
                    evt.setCancelled(false);
                }
                if (evt.isCancelled()) {
                    ultraPlayer.getData(PlayerUtility.class).sendActionbar(EditorSettings.getSettings(evt.getWhoClicked()).getError() + "You may not use your inventory until you close your wand", 3);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent evt) {
        ItemStack stack = evt.getPlayer().getInventory().getItemInMainHand();
        if (WandObject.isWand(stack)) {
            WandObject wand = WandObject.load(stack);
            if (wand.isOpen()) {
                evt.setCancelled(true);
                UltraPlayer.getPlayer(evt.getPlayer()).getData(PlayerUtility.class).sendActionbar(EditorSettings.getSettings(evt.getPlayer()).getError() + "You cannot pick up items while your wand is open!", 3);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void playerCastListener(PlayerInteractEvent evt) {
        Spell spell = null;
        MagicData data = UltraPlayer.getPlayer(evt.getPlayer()).getData(MagicData.class);
        if (System.currentTimeMillis() - data.getLastDropEvent() < 50) return;//Cannot cast spell, it was actually a drop event
        //evt.getAction() == Action.
        if (evt.getAction() == Action.LEFT_CLICK_AIR || evt.getAction() == Action.LEFT_CLICK_BLOCK) {
            spell = data.getActiveSpell();

        } else if (evt.getAction() == Action.RIGHT_CLICK_AIR || evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (WandObject.isWand(evt.getPlayer().getInventory().getItemInOffHand())) {
                spell = WandObject.load(evt.getPlayer().getInventory().getItemInOffHand()).getBound();
            }
        }
        if (spell != null) {
            evt.setCancelled(true);
            try {
                if (spell.cast(new Target(evt.getPlayer())) != null) {
                    data.useMana(spell.mana);
                    if (data.getCooldownReduction() > 100) {
                        data.setCooldownReduction(100);
                    } else if (data.getCooldownReduction() < 0) {
                        data.setCooldownReduction(0);
                    }
                    UltraPlayer.getPlayer(evt.getPlayer()).getData(PlayerUtility.class).setCooldown("Spell: " + spell.name, spell.cooldown * ((100.0 - data.getCooldownReduction()) / 100.0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @EventHandler
    public void onPlayerChangeInventorySlotEvent(PlayerItemHeldEvent evt) {
        int i = evt.getNewSlot();
        UltraPlayer player = UltraPlayer.getPlayer(evt.getPlayer());
        Spell spell = player.getData(MagicData.class).getBound()[i];
        if (spell != null) {
            player.getData(PlayerUtility.class).sendActionbar(player.getData(EditorSettings.class).getHelp() + spell.description);
        }
        PlayerInventory inventory = evt.getPlayer().getInventory();
        //System.out.println(1 + ": " + inventory.getItem(evt.getPreviousSlot()) + ", " + inventory.getItem(i));
        if (WandObject.isWand(inventory.getItem(evt.getPreviousSlot()))) {
            //System.out.println(2);
            WandObject wand = WandObject.load(inventory.getItem(evt.getPreviousSlot()));
            if (wand.isOpen()) {
                ItemStack stack = inventory.getItem(i);
                spell = WandObject.getSpellFromIcon(stack);
                if (spell != null) {
                    //System.out.println(3);
                    wand.setBound(spell);
                    inventory.setItem(evt.getPreviousSlot(), wand.save(inventory.getItem(evt.getPreviousSlot())));
                }
                evt.setCancelled(true);
            }
        }
    }
}
