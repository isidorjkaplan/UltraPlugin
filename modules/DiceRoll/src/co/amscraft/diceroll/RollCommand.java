package co.amscraft.diceroll;

import co.amscraft.ultrachat.ChatData;
import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import co.amscraft.ultralib.player.PlayerUtility;
import co.amscraft.ultralib.player.UltraPlayer;
import org.bukkit.command.CommandSender;

import java.text.DecimalFormat;

public class RollCommand extends UltraCommand {
    @Override
    public String[] getAliases() {
        return new String[]{"Roll", "Random"};
    }

    @Override
    public String getHelp() {
        return "Roll a random number between 1 and {X}";
    }

    /*public static void main(String[] args) {
        int[] rolls  = new int[5+1];
        for (int i = 0; i < 100000; i++) {
            rolls[(int) (Math.random() * 5 + 1)]++;
        }
        for (int i = 0; i < 6; i++) {
            System.out.println(i + ": " + rolls[i]);
        }
    }*/

    @Override
    public void run(CommandSender sender, String[] args, int i) {
        EditorSettings s = EditorSettings.getSettings(sender);
        PlayerUtility utils = UltraPlayer.getPlayer(sender).getData(PlayerUtility.class);
        double cooldown = utils.getCooldown("DiceRoll");
        if (cooldown == 0) {
            int limit = Integer.parseInt(args[i]);
            if (limit > 0) {
                int random = (int) (Math.random() * limit + 1);
                //Plugin ultraChat = UltraLib.getInstance().getServer().getPluginManager().getPlugin("UltraChat");

                UltraPlayer.getPlayer(sender).getData(ChatData.class).getChannel().broadcast("§6§lAMS Demigod §8>> §6Player §c" + sender.getName() + " §6has rolled §c" + random + "§6 out of §c" + limit);
                utils.setCooldown("DiceRoll", 5);
            } else {
                sender.sendMessage(s.getError() + "You must enter a positive integer");
            }

        } else {
            sender.sendMessage(s.getHelp() + "You must wait " + new DecimalFormat("0.0").format(cooldown) + " seconds before you can roll again!");
        }
    }

}
