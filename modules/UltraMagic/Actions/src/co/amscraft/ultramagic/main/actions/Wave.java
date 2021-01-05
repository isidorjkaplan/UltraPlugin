package co.amscraft.ultramagic.main.actions;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultramagic.SpellInstance;
import co.amscraft.ultramagic.SpellThread;
import co.amscraft.ultramagic.Target;
import co.amscraft.ultramagic.actions.TargetSelectorAction;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Wave extends TargetSelectorAction {

    private static HashMap<SpellInstance, List<WavePulse>> pulsesMap = new HashMap<>();
    @FieldDescription(help = "The amount of pulses the wave will emit before it dies out")
    public int pulses = 1;
    @FieldDescription(help = "The frequency (amount of pulses per second) of the wave", unit = "Hz")
    public double frequency = 0.5;
    @FieldDescription(help = "The amount of times per second that the wave should update", unit = "Hz")
    public double update = 15;
    @FieldDescription(help = "The percent of the blocks in the wave path that will get hit by any sub-actions", unit = "% (percent)")
    public double intensity = 30;
    @FieldDescription(help = "The duration that the blocks stay for")
    public double duration = 1;
    @FieldDescription(help = "The speed at which the wave will pass through the matter around it", unit = "m/s")
    public double velocity = 4;
    @FieldDescription(help = "The amplitude (height) of the wave released", unit = "blocks")
    public double amplitude = 2;
    //@FieldDescription(help = "The wavelength from crest to crest", unit = "blocks")
    //public double wavelength = 3;
    @FieldDescription(help = "Weather or not the wave should include a troph (negetive point) as well")
    public boolean doTrophs = false;
    @FieldDescription(help = "The distancec that the wave will travel before vanishing", unit = "blocks")
    public double range = 20;
    @FieldDescription(help = "The angle in each horizontal direction from the caster that the wave will occupy. Enter 180 degrees for full circular wave", unit = "degrees")
    public float width = 15;
    @FieldDescription(help = "List of the mediums that the wave can (or cant dependig on mediumMode) travel through.")
    public List<Material> mediums = new ArrayList<>();
    @FieldDescription(help = "Weather to whitelist (wave can only travel through listed mediums) or blacklist (wave can travel through all except listed mediums) the wave medium list.")
    public MediumMode mediumMode = MediumMode.BLACKLIST;
    @FieldDescription(help = "Set an override for the type of block the wave creates when it travels. Set to virtual wave for wave to not be made of blocks.")
    public Material blockOverride = Material.AIR;

    //@FieldDescription(help = "Set to true to make the wave made out of block entities instead of physical blocks")
    //public boolean fallingBlock = false;

    public static void updatePulsesMap() {
        ArrayList<SpellInstance> keys = new ArrayList<>();
        keys.addAll(pulsesMap.keySet());
        for (SpellInstance spell : keys) {
            ArrayList<WavePulse> pulses = new ArrayList<>();
            pulses.addAll(pulsesMap.get(spell));
            for (WavePulse pulse : pulses) {
                if (pulse.isFinished()) {
                    pulsesMap.get(spell).remove(pulse);
                }
            }
            if (pulsesMap.get(spell).isEmpty()) {
                pulsesMap.remove(spell);
            }
        }

    }

    public double getWavelength() {
        //v = f length
        return this.velocity / this.frequency;
    }

    @Override
    public void run(SpellInstance spell, Target target, Target caster) {
        //List<WavePulse> pulses = new ArrayList<>();
        List<WavePulse> pulses = pulsesMap.getOrDefault(this, new ArrayList<>());
        pulsesMap.put(spell, pulses);
        boolean inverted = false;
        for (int i = 0; i < this.pulses; i++) {
            pulses.add(new WavePulse(target, target.getEyeLocation().getDirection(), inverted, spell));
            if (doTrophs) {
                inverted = !inverted;
            }
            try {
                Thread.sleep(Math.round(1000 / frequency));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        while (pulsesMap.containsKey(spell)) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            updatePulsesMap();
        }
        //System.out.println("Wave finished casting!");
    }

    public enum MediumMode {
        WHITELIST, BLACKLIST, VIRTUAL_WAVE
    }

    private class WavePulse {
        public final long CAST_TIME;
        private final Target CASTER;
        private final boolean INVERTED;
        private final SpellInstance SPELL;
        public Location origin;
        private List<Coordnate> done = new ArrayList<>();

        private WavePulse(Target waveCaster, Vector direction, boolean inverted, SpellInstance spell) {
            this.origin = waveCaster.getLocation().clone().setDirection(direction);
            this.CAST_TIME = System.currentTimeMillis();
            this.CASTER = waveCaster;
            this.INVERTED = inverted;
            this.SPELL = spell;
            SpellThread thread = new SpellThread() {
                @Override
                public void cast() {
                    WavePulse.this.itterate();
                }
            };
            spell.getThreads().add(thread);
            thread.runTaskAsynchronously(UltraLib.getInstance());
        }

        public double currentDistance() {
            return ((System.currentTimeMillis() - CAST_TIME) / 1000.0) * Wave.this.velocity;
        }

        private void itterate() {
            //done.clear();
            //System.out.println("Itterating");
            while (!this.isFinished()) {
                double distance = this.currentDistance();
                if (distance > 2) {
                    double arcLength = (2 * Wave.this.width * Math.PI * distance) / 360;
                    double playerAngle = (origin.getYaw()) + 180;
                    double theta = playerAngle - 90;
                    double x = Math.cos(Math.toRadians(theta)) * distance;
                    double z = Math.sin(Math.toRadians(theta)) * distance;
                    Location loc = origin.clone().add(x, 0, z);
                    if (!hasDone(loc.getX(), loc.getZ())) {
                        updateLocation(loc);
                        //System.out.println(loc.getY());
                        origin.setY(loc.getY());
                    }
                    if (Wave.this.mediumMode != MediumMode.VIRTUAL_WAVE) {
                        for (double angle = -Wave.this.width; angle <= Wave.this.width; angle += Wave.this.width / arcLength) {
                            theta = angle - 90 + playerAngle;
                            x = Math.cos(Math.toRadians(theta)) * distance;
                            z = Math.sin(Math.toRadians(theta)) * distance;
                            iterateBlocks(origin.getX() + x, origin.getZ() + z);
                        }
                    }
                    for (double angle = -Wave.this.width; angle <= Wave.this.width; angle += Wave.this.width / (arcLength * 0.01 * intensity)) {
                        theta = angle - 90 + playerAngle;
                        x = Math.cos(Math.toRadians(theta)) * distance;
                        z = Math.sin(Math.toRadians(theta)) * distance;
                        iterateActions(origin.getX() + x, origin.getZ() + z);
                    }

                }
                try {
                    Thread.sleep(Math.round(1000 / update));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private SpellInstance getSpell() {
            return this.SPELL;
        }

        private boolean hasDone(double x, double z) {
            for (Coordnate c : done) {
                if (c.x == (int) x && c.z == (int) z) {
                    return true;
                }
            }
            return false;
        }

        public boolean isValid(double x, double z) {
            Location loc = new Location(origin.getWorld(), x, origin.getY(), z);
            updateLocation(loc);
            loc.add(0, -1, 0);
            Material block = loc.getBlock().getType();
            boolean contains = mediums.contains(block);
            switch (mediumMode) {
                case VIRTUAL_WAVE:
                    return true;
                case WHITELIST:
                    return contains;
                case BLACKLIST:
                    return !contains;
            }
            return false;
        }

        public void iterateBlocks(double x, double z) {
            if (!hasDone(x, z) && isValid(x, z)) {
                done.add(new Coordnate((int) x, (int) z));
                Location location = new Location(origin.getWorld(), x, origin.getY(), z);
                updateLocation(location);
                for (int i = 0; i <= Wave.this.amplitude; i++) {
                    int y = !INVERTED ? i : -i;
                    double time = ((amplitude - i + 1) / amplitude) * duration;
                    Material block = blockOverride;
                    if (blockOverride == Material.AIR) {
                        block = location.clone().subtract(0, 1, 0).getBlock().getType();
                    }
                    Location loc = location.clone();
                    loc.setY(loc.getY() + y);
                    if (INVERTED && Wave.this.amplitude != i) {
                        block = Material.AIR;
                    }
                    if (loc.getBlock().getType() != block) {
                        ChangeBlock.changeBlocks(getSpell(), loc, time, block, (byte) 0);
                    }
                }
            }
        }

        private void updateLocation(Location location) {
            for (double i = 0; i < amplitude + 5; i++) {
                for (int sign : new int[]{-1, 1}) {
                    double y = sign * i;
                    Location loc = location.clone();
                    loc.add(0, y, 0);
                    if (loc.getBlock().getType() != Material.AIR) {
                        loc.add(0, 1, 0);
                        if (loc.getBlock().getType() == Material.AIR) {
                            location.setY(loc.getY());
                            break;
                        }
                    }
                }
            }
        }

        private void iterateActions(double x, double z) {
            //System.out.println("(" + x + ", " + z + ")");
            new BukkitRunnable() {
                public void run() {
                    if (isValid(x, z)) {
                        Location location = new Location(origin.getWorld(), x, origin.getY(), z);
                        updateLocation(location);
                        try {
                            playEffects(new Target(location));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        SpellInstance spell = getSpell();
                        try {
                            Target[] targets;
                            if (protectSpellCaster) {
                                targets = Target.getTargetsInBox(Wave.this.getTargets(), location.clone().add(0, -Wave.this.amplitude, 0), 2, Wave.this.amplitude * 2, 2, getSpell().CASTER);
                            } else {
                                targets = Target.getTargetsInBox(Wave.this.getTargets(), location.clone().add(0, -Wave.this.amplitude, 0), 2, Wave.this.amplitude * 2, 2);
                            }
                            for (Target target : targets) {
                                Wave.this.runActions(spell, target, CASTER);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.runTask(UltraLib.getInstance());
        }

        public boolean isFinished() {
            return this.currentDistance() > Wave.this.range;
        }

        public class Coordnate {
            int x;
            int z;

            public Coordnate(int x, int z) {
                this.x = x;
                this.z = z;
            }
        }
    }
}
