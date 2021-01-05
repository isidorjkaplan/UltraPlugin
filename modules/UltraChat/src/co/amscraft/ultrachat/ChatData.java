package co.amscraft.ultrachat;

import co.amscraft.ultralib.editor.FieldDescription;
import co.amscraft.ultralib.player.PlayerData;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatData extends PlayerData {
    private final static char[] FORMAT_ENDINGS = new char[]{'!', '.', '?', ':'};
    public boolean spy = false;
    public boolean format = true;
    public String color = "";
    @FieldDescription(save = false)
    private int channel = Channel.getGeneral().getId();

    public static List<UltraPlayer> getSpies() {
        List<UltraPlayer> list = new ArrayList<>();
        for (UltraPlayer player : UltraPlayer.getPlayers()) {
            if (player.getData(ChatData.class).isSpying()) {
                list.add(player);
            }
        }
        return list;
    }

    private static String getWord(String string, int index) {
        String word = "";
        for (int i1 = 0; i1 < string.length(); i1++) {
            if (string.charAt(i1) == ' ') {
                if (i1 < index) {
                    word = "";
                } else {
                    break;
                }
            } else {
                word += string.charAt(i1);
            }
        }
        return word;
    }

    private static boolean shouldFormat(String word) {
        return !word.startsWith("http://") && !word.substring(0, word.length() - 1).contains(".");
    }

    private static String punctuate(String string) {
        if (string.isEmpty() || string.length() < 10) {
            return string;
        }
        char last = '.';
        char[] array = string.toCharArray();
        String word;
        for (int i = 0; i < string.length(); i++) {
            if (array[i] != ' ') {
                word = getWord(string, i);
                if (!shouldFormat(word)) {
                    last = ' ';
                }
                if (last == '.') {
                    array[i] = Character.toUpperCase(array[i]);
                }
                last = array[i];
            }
        }
        string = String.valueOf(array);
        Arrays.sort(FORMAT_ENDINGS);
        if (shouldFormat(getWord(string, string.length() - 1)) && Arrays.binarySearch(FORMAT_ENDINGS, string.charAt(string.length() - 1)) < 0) {
            string += ".";
        }
        return string;

    }

    public Channel getChannel() {
        Channel channel = Channel.getChannel(this.channel);
        if (channel == null) {
            channel = Channel.getGeneral();
            this.channel = channel.getId();
        }
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel.getId();
    }

    public String getColor() {

        return ChatColor.translateAlternateColorCodes('&', this.color);
    }

    public boolean isSpying() {
        return this.spy;
    }

    public String format(String string) {
        if (this.format) {
            return punctuate(string);
        }
        return string;
    }


}
