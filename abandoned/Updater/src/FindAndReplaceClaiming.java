import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FindAndReplaceClaiming {
    public static void main(String[] args) {
        File folder = new File("/Users/izzy6/OneDrive/Desktop/ChestCommands/menu");
        System.out.println(folder.exists());
        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(".yml")) {
                try {
                    FileUtils.writeStringToFile(file, readFile(file.getPath()).replace("{Player}", "{player}").replace("Amscraft.co", "http://amscraft.co").replace("wandp", "wand"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }
}
