package co.amscraft.ultralib.ftpserver;

import co.amscraft.ultralib.commands.Component;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.Editor;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.network.Packet;
import co.amscraft.ultralib.player.UltraPlayer;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class FTPCommand extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"ftp"};
    }

    @Override
    public String getHelp() {
        return "The primary FTP command";
    }

    @Override
    public void execute(CommandSender sender, String[] args, int i, String permission) {
        EditorSettings s = EditorSettings.getSettings(sender);
        if (sender.equals(Bukkit.getConsoleSender())) {
            super.execute(sender, args, i, permission);
        } else {
            sender.sendMessage(s.getError() + "You do not have permission to use this command!");
        }
    }

    @Override
    public void run(CommandSender sender, String[] args, int i) {
        EditorSettings s = EditorSettings.getSettings(sender);
        if (sender.equals(Bukkit.getConsoleSender())) {
            super.run(sender, args, i);
        } else {
            sender.sendMessage(s.getError() + "You do not have permission to use this command!");
        }
    }

    @Override
    public Component[] getComponents() {
        return new Component[]{new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"Add"};
            }

            @Override
            public String getHelp() {
                return "{username} {password}";
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                String username = args[i];
                if (username != null) {
                    if (args.length > i + 1 && args[i + 1] != null) {
                        String password = args[i + 1];
                        UserManager manager = FTPServer.serverFactory.getUserManager();

                        BaseUser user = new BaseUser();
                        user.setName(username);
                        user.setPassword(password);
                        List<Authority> authorities = new ArrayList<>();
                        authorities.add(new CustomPermission());
                        user.setAuthorities(authorities);
                        user.setHomeDirectory(FTPServer.ROOT_FOLDER);
                        try {
                            manager.save(user);
                            sender.sendMessage(s.getSuccess() + "Successfully added FTP user!");
                        } catch (Exception e) {
                            sender.sendMessage(s.getError() + "An error has occured!");
                            e.printStackTrace();
                        }
                    } else {
                        sender.sendMessage(s.getError() + "You must enter a password");
                    }
                } else {
                    sender.sendMessage(s.getError() + "You must enter a username");
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"List"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                try {
                    List<String> list = new ArrayList<>();
                    for (String st : FTPServer.serverFactory.getUserManager().getAllUserNames()) {
                        list.add(st);
                    }
                    sender.sendMessage(s.getValue() + list);
                } catch (Exception e) {
                    sender.sendMessage(s.getError() + "Command failed");
                }
            }
        }, new Component() {
            @Override
            public String[] getAliases() {
                return new String[]{"Delete"};
            }

            @Override
            public void run(CommandSender sender, String[] args, int i) {
                EditorSettings s = EditorSettings.getSettings(sender);
                try {
                    String user = args[i];
                    UserManager manager = FTPServer.serverFactory.getUserManager();
                    if (manager.doesExist(user)) {
                        manager.delete(user);
                        sender.sendMessage(s.getSuccess() + "Deleted user " + user);
                    } else {
                        sender.sendMessage(s.getError() + "User " + user + " does not exist!");
                    }
                } catch (Exception e) {
                    sender.sendMessage(s.getError() + "Command failed");
                }
            }
        }
        };
    }
}
