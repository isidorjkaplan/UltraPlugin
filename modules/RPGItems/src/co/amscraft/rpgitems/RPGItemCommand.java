package co.amscraft.rpgitems;


import co.amscraft.ultralib.commands.UltraCommand;
import co.amscraft.ultralib.editor.EditorSettings;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RPGItemCommand extends UltraCommand {

    @Override
    public String[] getAliases() {
        return new String[]{"RPGitem","RPGItems","UltraItem"};
    }

    @Override
    public String getHelp() {
        return "<RPGItem> <Number> <Player>";
    }

    @Override
    public void run(CommandSender sender, String[] args, int i) {
        RPGItem item = RPGItem.getItem(args[i]);
        EditorSettings s = EditorSettings.getSettings(sender);
        if (item != null) {
            Player target = null;
            if (sender instanceof Player) {
                target = (Player)sender;
            }
            if (args.length > i+2){
                target=Bukkit.getPlayer(args[i+2]);
            }
            if (target != null) {
                int amount = 1;
                if (args.length > i+1) {
                    try {
                        amount = Integer.parseInt(args[i + 1]);
                    } catch (Exception e) {
                        sender.sendMessage(s.getError() + "You must enter a number greater then 0");
                    }
                }
                if (amount > 0) {
                    ItemStack stack = item.createNewItem().seralize();
                    stack.setAmount(amount);
                    target.getInventory().addItem(stack);
                    sender.sendMessage(s.getSuccess() + "Successfully created new RPGitem");
                } else {
                    sender.sendMessage(s.getError() + "You must enter a valid integer greater then 0");
                }
            } else {
                sender.sendMessage(s.getError() + "Player " + args[i+2] + " is not online!");
            }
        } else {
            sender.sendMessage(s.getError() + "Item " + args[i] + " does not exist!");
        }

    }
}
