package co.amscraft.ultrachat;

import co.amscraft.ultralib.editor.EditorCheck;
import co.amscraft.ultralib.editor.EditorData;
import co.amscraft.ultralib.modules.Module;
import org.bukkit.command.CommandSender;

public class UltraChat extends Module {
    @Override
    public String[] getModuleDependancies() {
        return new String[0];
    }

    @Override
    public void onEnable() {
        try {
            EditorData.registerParse(Channel.class, Channel.class.getMethod("getChannel", String.class));
            EditorCheck.register(Channel.class.getField("format"), new EditorCheck("The format must include {PLAYER} or {DISPLAYNAME} and {MESSAGE}") {
                @Override
                public boolean check(Object object, CommandSender sender) {
                    //"&7(&a{CHANNEL}&7) &f{PLAYER}&f: {MESSAGE}";
                    // {CHANNEL}, {USERNAME}, {PLAYER}, {MESSAGE}
                    if (object == null) {
                        return false;
                    }
                    String s = object.toString();
                    return (s.contains("{PLAYER") || s.contains("{USERNAME}")) && s.contains("{MESSAGE}");
                }
            });
            EditorCheck.register(Channel.class.getField("name"), new EditorCheck("Your channel name must be unique") {
                @Override
                public boolean check(Object object, CommandSender sender) {
                    return Channel.getChannel(object + "") == null;
                }
            });
            EditorCheck.register(Channel.class.getField("general"), new EditorCheck("To change this channel from general you must select a new general channel") {
                @Override
                public boolean check(Object object, CommandSender sender) {
                    if (object.toString().equalsIgnoreCase("true")) {
                        for (Channel channel : Channel.getChannels()) {
                            if (channel.general) {
                                channel.general = false;
                                channel.save();
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });
        } catch (NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

    }
}
