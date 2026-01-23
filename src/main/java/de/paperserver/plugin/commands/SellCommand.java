package de.paperserver.plugin.commands;

import de.paperserver.plugin.PaperPluginSuite;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SellCommand implements CommandExecutor {

    private final PaperPluginSuite plugin;

    public SellCommand(PaperPluginSuite plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("plugin.sell.use")) {
            player.sendMessage("§c✗ You don't have permission to use this command!");
            return true;
        }

        ItemStack inHand = player.getInventory().getItemInMainHand();
        if (inHand == null || inHand.getType().isAir()) {
            player.sendMessage("§c✗ Hold an item in your hand that you want to sell.");
            return true;
        }

        // Get sell price from config (e.g., sell.prices.<MATERIAL_NAME> or sell.default-price)
        String materialName = inHand.getType().toString();
        double price = plugin.getConfig().getDouble("sell.prices." + materialName, 
                       plugin.getConfig().getDouble("sell.default-price", 1.0));

        if (price <= 0) {
            player.sendMessage("§c✗ This item cannot be sold.");
            return true;
        }

        double totalPrice = price * inHand.getAmount();

        if (plugin.getEconomy() == null) {
            player.sendMessage("§c✗ Economy not available!");
            return true;
        }

        // Deposit money
        plugin.getEconomy().depositPlayer(player, totalPrice);

        // Remove item
        player.getInventory().setItemInMainHand(null);

        player.sendMessage("§a✓ Sold: " + inHand.getAmount() + "x " + materialName);
        player.sendMessage("§6Profit: §a+" + totalPrice);
        
        // Update scoreboard
        if (plugin.getMoneyScoreboardManager() != null) {
            plugin.getMoneyScoreboardManager().updateMoneyDisplay(player);
        }

        return true;
    }
}
