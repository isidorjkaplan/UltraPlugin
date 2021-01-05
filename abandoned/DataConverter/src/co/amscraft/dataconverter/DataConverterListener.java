package co.amscraft.dataconverter;

import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultramagic.MagicData;
import co.amscraft.ultramagic.Spell;
import co.amscraft.ultrastats.PlayerStat;
import co.amscraft.ultrastats.Stat;
import co.amscraft.ultrastats.StatsData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Izzy on 2017-11-22.
 */
public class DataConverterListener implements Listener {
    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent evt) {
        UltraPlayer player = UltraPlayer.getPlayer(evt.getPlayer());
        try {
            if (player.getConfig().get("data.MagicData") == null) {
                File file = new File("plugins/UltraPlugin/players/" + evt.getPlayer().getUniqueId().toString() + ".yml");
                if (file.exists()) {
                    FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                    FileConfiguration spellsFile = YamlConfiguration.loadConfiguration(new File("plugins/UltraPlugin/spells.yml"));
                    List<Integer> list = data.getIntegerList("Spells");
                    List<Spell> spells = new ArrayList<>();
                    for (Integer i : list) {
                        try {
                            spells.add(Spell.getSpell(spellsFile.getString("spells." + i + ".parameters.name")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    for (Spell spell : spells) {
                        try {
                            player.getData(MagicData.class).addSpell(Spell.getSpell(spell.name));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (player.getConfig().get("data.StatsData") == null) {
            File file = new File("plugins/UltraPlugin/players/" + evt.getPlayer().getUniqueId().toString() + ".yml");
            if (file.exists()) {
                FileConfiguration data = YamlConfiguration.loadConfiguration(file);
                FileConfiguration statsFile = YamlConfiguration.loadConfiguration(new File("plugins/UltraPlugin/magic-level-paths.yml"));
                for (Map<?, ?> map : data.getMapList("Stats")) {
                    System.out.println(map);
                    try {
                        int level = Integer.parseInt(map.get("level") + "");
                        float exp = Float.parseFloat(map.get("exp") + "");
                        //magic-path-id: 13
                        String name = statsFile.getString("paths." + map.get("magic-path-id") + ".name");
                        name = name.replace("Child", "").replace(" ", "");
                        Stat stat = Stat.getObject(Stat.class, "name", name);
                        PlayerStat playerStat = new PlayerStat();
                        playerStat.level = level;
                        playerStat.exp = exp;
                        playerStat.stat = stat;
                        player.getData(StatsData.class).addStat(playerStat);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
