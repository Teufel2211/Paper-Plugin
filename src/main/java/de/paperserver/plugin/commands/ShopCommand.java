package de.paperserver.plugin.commands;

import de.paperserver.plugin.managers.ShopManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShopCommand implements CommandExecutor {

    private final ShopManager shopManager;

    public ShopCommand(ShopManager shopManager) {
        this.shopManager = shopManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // open shop list GUI
            player.openInventory(de.paperserver.plugin.gui.ShopGUI.createShopListInventory(shopManager));
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage("§c✗ Usage: /shop create <name>");
                    return true;
                }
                shopManager.createShop(player, args[1]);
                break;

            case "add":
                // /shop add <shopId> <price> (uses item in hand)
                if (args.length < 3) {
                    player.sendMessage("§c✗ Usage: /shop add <shopId> <price> (hold an item in your hand)");
                    return true;
                }
                try {
                    int shopId = Integer.parseInt(args[1]);
                    double price = Double.parseDouble(args[2]);
                    org.bukkit.inventory.ItemStack inHand = player.getInventory().getItemInMainHand();
                    if (inHand == null || inHand.getType().isAir()) {
                        player.sendMessage("§c✗ Hold an item in your hand that you want to add.");
                        return true;
                    }
                    boolean ok = shopManager.addItem(player, shopId, inHand.clone(), price);
                    if (ok) {
                        // consume one item from hand
                        int amount = inHand.getAmount();
                        if (amount <= 1) player.getInventory().setItemInMainHand(null);
                        else inHand.setAmount(amount - 1);
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage("§c✗ Invalid ID or price!");
                }
                break;

            case "list":
                player.sendMessage("§6=== Shops ===");
                for (ShopManager.ShopData shop : shopManager.getAllShops()) {
                    player.sendMessage(String.format("§7%d: %s (%s)", shop.id, shop.name, shop.type));
                }
                break;

            default:
                player.sendMessage("§c✗ Unknown subcommand!");
                break;
        }

        return true;
    }
}
