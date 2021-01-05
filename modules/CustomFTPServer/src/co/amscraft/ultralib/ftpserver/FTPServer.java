package co.amscraft.ultralib.ftpserver;

import co.amscraft.ultralib.UltraLib;
import co.amscraft.ultralib.ftpserver.commands.*;
import co.amscraft.ultralib.modules.Module;
import co.amscraft.ultralib.utils.savevar.SaveVar;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.command.Command;
import org.apache.ftpserver.command.CommandFactory;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.bukkit.scheduler.BukkitRunnable;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

//ftp add IzzyK AawfawifaWaAWFOFfawofhawofhawurhaw218323QWEDawg1
//ftp add Firenze hrjJ3BpGK8fa2KnsHJ8TYf8hyFfwRzPBNcDzjKjJ4ku4L
//ftp add Duncan ghxRT4YeuUmWb77sSpM3kzjLEf7DRV55TjfDMYqSA53Lx
//ftp add Devon kJKcDZ2GWsKUmAUuzdCvWXwzknHJKk3zwR2jzKf3N6MWwfdYzZ
//ftp add Bisect uDZgcr5wh8aaPRQX2LGTpm6wC
public class FTPServer extends Module {

    @Override
    public String[] getModuleDependancies() {
        return new String[0];
    }

    @Override
    public void onEnable() {

        new BukkitRunnable() {
            public void run() {
                try {
                    startFTP();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskLaterAsynchronously(UltraLib.getInstance(), 20*10);
        /*new BukkitRunnable() {
            public void run() {
                addUser("admin", "TempAdminPassword");
                System.out.println("Added user admin");
            }
        }.runTask(UltraLib.getInstance());*/
    }

    @Override
    public void onDisable() {

        //server.stop();
    }

    public static boolean canDownload(String file) {
        String lower = file.toLowerCase();
        if (lower.contains("ultra") && lower.contains(".jar")) {
            return false;
        }
        if (lower.contains("ftpserver")) {
            return false;
        }
        for (String blockedPhrase: blockedPhrases) {
            if (lower.contains(blockedPhrase.toLowerCase())) {
                return false;
            }
        }
        return true;
    }

    @SaveVar
    public static String ROOT_FOLDER = "/home/mch/multicraft/servers/server55034";
    @SaveVar
    public static String USER_FILE = ROOT_FOLDER + "/UltraLib/modules/FTPServer/users.txt";
    @SaveVar
    public static int PORT = 2221;
    @SaveVar
    public static List<String> blockedPhrases = new ArrayList<>();

    public static FtpServerFactory serverFactory;
    public static ListenerFactory factory;
    public static FtpServer server;

    public static void initExtentions() {
        if (blockedPhrases.isEmpty()) {
            blockedPhrases.add(".mca");
            blockedPhrases.add(".schem");

        }
    }

    public static void startFTP() throws Exception {
        initExtentions();
        serverFactory = new FtpServerFactory();
        factory = new ListenerFactory();


        PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
        File users = new File(USER_FILE);
        UltraLib.getInstance().getLogger().log(Level.INFO, "File users exists: " + users.exists());
        userManagerFactory.setFile(users);
        UserManager manager = userManagerFactory.createUserManager();
        serverFactory.setUserManager(manager);

        CommandFactory cmds = serverFactory.getCommandFactory();
        {
            CustomRETR retr = new CustomRETR();
            CustomRNTO rnto = new CustomRNTO();
            CustomSTOR stor = new CustomSTOR();
            CustomDELE dele = new CustomDELE();
            CustomCWD cwd = new CustomCWD();
            CustomRMD rmd = new CustomRMD();
            CustomMKD mkd = new CustomMKD();

            serverFactory.setCommandFactory(new CommandFactory() {
                @Override
                public Command getCommand(String s) {
                    if (s.equals("RETR")) {
                        return retr;
                    } else if (s.equals("RNTO")) {
                        return rnto;
                    } else if (s.equals("STOR")) {
                        return stor;
                    } else if (s.equals("DELE")) {
                        return dele;
                    } else if (s.equals("RMD")) {
                        return rmd;
                    } else if (s.equals("CWD")) {
                        return cwd;

                    } else if (s.equals("MKD")) {
                        return mkd;
                    }
                    return cmds.getCommand(s);
                }
            });
        }
        factory.setPort(PORT);
        serverFactory.addListener("default", factory.createListener());
        server = serverFactory.createServer();
        server.start();
    }
}
