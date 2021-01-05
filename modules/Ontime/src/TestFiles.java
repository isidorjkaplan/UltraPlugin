import co.amscraft.ontime.OntimeData;
import co.amscraft.ultralib.player.UltraPlayer;
import co.amscraft.ultralib.utils.ObjectUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class TestFiles {
    public static void main(String[] args) throws IOException {
        File[] files = new File("C:/Users/izzy6/OneDrive/Desktop/players").listFiles();
        if (files != null) {
            long start = System.currentTimeMillis();
            /*Arrays.sort(files, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                }
            });*/
            int count = 0;
            int error = 0;
            Map<UUID, Long> currentValues = new HashMap<>();
            for (int i = 0; i < files.length; i++) {
                try {
                    ConfigurationSection section = YamlConfiguration.loadConfiguration(files[i]).getConfigurationSection("data.OntimeData");
                    if (section != null) {
                        OntimeData data = (OntimeData) ObjectUtils.read(section);
                        currentValues.put(UUID.fromString(files[i].getName().replace(".yml", "")), data.getTotal());
                        count++;
                    } else {
                        error++;
                    }
                } catch(Exception e) {
                    error++;
                }
            }
            System.out.println(count + "/" + files.length);
            System.out.println("Error: " + error);
            System.out.println("MapSize: " + currentValues.size());
            long sum = 0;
            for (Long l: currentValues.values()) {
                sum+=l;
            }
            System.out.println("Total time on AMS: " + ((float)sum/86400) + " days");
            System.out.println("Time: " + (float)(System.currentTimeMillis()-start)/1000);
        } else {
            System.out.println("Is null");
        }
    }
}
