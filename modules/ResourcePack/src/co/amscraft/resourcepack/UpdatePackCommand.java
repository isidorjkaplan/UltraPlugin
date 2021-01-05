package co.amscraft.resourcepack;

import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;

import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

public class UpdatePackCommand extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"resourcepack", "rpack"};
    }

    @Override
    public String getHelp() {
        return "{URL}";
    }

    @Override
    public void run(CommandSender sender, String[] args, int i) {
        EditorSettings s = EditorSettings.getSettings(sender);
        String URL = args[i];
        if (URL != null) {
            File file = new File("server.properties");
            try {
                List<String> lines = Files.readAllLines(file.toPath());
                for (int j = 0; j < lines.size(); j++) {
                    String line = lines.get(j);
                    if (line.startsWith("resource-pack=")) {
                        lines.set(j, "resource-pack=" + URL);
                        Files.write(file.toPath(), lines, Charset.defaultCharset());
                        sender.sendMessage(s.getSuccess() + "Set resourcepack link to: " + URL + " from " + line.replace("resource-pack=", "") + "!");
                        sender.sendMessage(s.getError() + "WARNING: You must restart the server for the change to take effect and this plugin does not check to ensure you submitted a valid link.");
                        break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                sender.sendMessage(s.getError() + "An error has occured!!");
            }
        } else {
            sender.sendMessage(s.getError() + "You must enter a download link!");
        }
    }

}
