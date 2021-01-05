import java.io.File;

public class ProjectSizeCalculator {
    public static void main(String[] args) {
        File folder = new File("C:\\Users\\izzy6\\Dropbox\\Workspace\\UltraLib");
        System.out.println("Your project contains " + countClasses(folder) + " classes");
    }

    public static int countClasses(File folder) {
        int count = 0;
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                count += countClasses(file);
            } else if (file.getName().endsWith(".java")) {
                count++;
            }
        }
        return count;
    }

}