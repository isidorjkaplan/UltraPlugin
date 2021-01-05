package co.amscraft.ultramagic;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.PlayerUtility;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultramagic.actions.Action;
import co.amscraft.ultramagic.events.SpellActionEvent;
import co.amscraft.ultramagic.events.SpellCastEvent;
import co.amscraft.ultramagic.events.SpellFinishCastingEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Izzy on 2017-10-15.
 */
public class SpellInstance {
    public static HashMap<Spell, List<SpellInstance>> instances = new HashMap<>();
    public final Target CASTER;
    public final Spell SPELL;
    public final long CAST_TIME;
    //public List<Action> casting = new ArrayList<>();
    private List<Target> targets = new ArrayList<>();

    private ArrayList<SpellThread> threads = new ArrayList<>();

    public SpellInstance(Target caster, Spell spell) {
        this.SPELL = spell;
        this.CASTER = caster;
        this.CAST_TIME = System.currentTimeMillis();
        List<SpellInstance> list = instances.getOrDefault(spell, new ArrayList<>());
        list.add(this);
        instances.put(spell, list);
        SpellThread thread = new SpellThread() {
            public void cast() {
                SpellInstance.this.runActions(SpellInstance.this.getSpell().actions, SpellInstance.this.CASTER, SpellInstance.this.CASTER);
            }
        };
        this.getThreads().add(thread);
        thread.runTaskAsynchronously(UltraLib.getInstance());

    }

    public static SpellInstance castSpell(Spell spell, Target caster) {
        SpellCastEvent evt = new SpellCastEvent(spell, caster);
        evt.dispatch();
        if (evt.isCancelled()) {
            if (caster.getObject() instanceof Player) {
                UltraPlayer.getPlayer((Player) caster.getObject()).getData(PlayerUtility.class).sendActionbar(EditorSettings.getSettings((Player) caster.getObject()).getError() + evt.getFailMessage());
            }
            return null;
        }
        return new SpellInstance(caster, spell);
    }

    public static boolean canCast(Spell spell, Player player) {
        return (UltraPlayer.getPlayer(player).getData(MagicData.class).getSpells().contains(spell));
    }

    public List<SpellThread> getThreads() {
        return this.threads;
    }

    public void addTarget(Target target) {
        List<Target> keys = new ArrayList<>();
        keys.addAll(targets);
        for (Target t : keys) {
            if (t.getObject().equals(target.getObject())) {
                return;
            }
        }
        targets.add(target);
    }


    public List<Target> getTargets() {
        return this.targets;
    }

    public void runActions(List<Action> actions, Target target, Target caster) {
        addTarget(target);
        // casting.addAll(actions);
        Thread t = new Thread() {
            public void run() {
                for (Action action : actions) {
                    SpellActionEvent evt =(SpellActionEvent) new SpellActionEvent(SpellInstance.this, action, target, caster).dispatch();
                    if (!evt.isCancelled()) {
                        Thread thread = new Thread() {
                            public void run() {
                                SpellThread runnable = new SpellThread() {
                                    public void cast() {
                                        evt.getAction().playEffects(evt.getTarget());
                                        evt.getAction().run(SpellInstance.this, evt.getTarget(), evt.getCaster());
                                        //casting.remove(action);
                                    }
                                };
                                SpellInstance.this.getThreads().add(runnable);
                                if (evt.getAction().isAsyncThread()) {
                                    runnable.run();
                                } else {
                                    runnable.runTask(UltraLib.getInstance());
                                }

                            }
                        };
                        if (!isQuit) {
                            if (action.isAsync) {
                                thread.start();
                            } else {
                                thread.run();
                            }
                        }
                    } else if (CASTER.getObject() instanceof Player){
                        UltraPlayer.getPlayer(((Player)CASTER.getObject())).getData(PlayerUtility.class).sendActionbar(ChatColor.RED + evt.getErrorMessage());

                    }
                }
            }
        };
        if (Bukkit.isPrimaryThread()) {
            t.start();
        } else {
            t.run();
        }

    }

    public boolean isFinished() {
        for (SpellThread thread : new ArrayList<>(this.getThreads())) {
            if (thread != null && !thread.isFinished()) {
                return false;
            } else if (thread == null) {
                this.getThreads().remove(null);
            }
        }
        return true;
    }

    public Spell getSpell() {
        return this.SPELL;
    }

    public void end() {
        if (this.isFinished()) {
            forceEnd();
        }
    }

    private boolean isQuit = false;
    public void forceEnd() {
        isQuit = true;
        List<SpellInstance> list = instances.getOrDefault(this.getSpell(), new ArrayList<>());
        list.remove(this);
        instances.put(this.getSpell(), list);
        SpellFinishCastingEvent evt = (SpellFinishCastingEvent) new SpellFinishCastingEvent(this).dispatch();
        for (Target t : this.getTargets()) {
            if (!t.equals(this.CASTER) && t.getObject() instanceof Player) {
                UltraPlayer.getPlayer((Player) t.getObject()).getData(PlayerUtility.class).sendActionbar(evt.formatMessage(evt.getHitMessage(), (Player) t.getObject()), 3);
            }
        }
        if (this.CASTER.getObject() instanceof Player) {
            UltraPlayer.getPlayer(((Player) this.CASTER.getObject())).getData(PlayerUtility.class).sendActionbar(evt.formatMessage(evt.getCasterMessage(), ((Player) this.CASTER.getObject())), 2);
        }
    }


}
