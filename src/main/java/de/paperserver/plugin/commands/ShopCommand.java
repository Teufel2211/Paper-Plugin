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
            sender.sendMessage("§cDieser Befehl kann nur von Spielern genutzt werden!");
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
                    player.sendMessage("§c✗ Verwendung: /shop create <name>");
                    return true;
                }
                shopManager.createShop(player, args[1]);
                break;

            case "add":
                // /shop add <shopId> <price> (uses item in hand)
                if (args.length < 3) {
                    player.sendMessage("§c✗ Verwendung: /shop add <shopId> <price> (halte ein Item in der Hand)");
                    return true;
                }
                try {
                    int shopId = Integer.parseInt(args[1]);
                    double price = Double.parseDouble(args[2]);
                    org.bukkit.inventory.ItemStack inHand = player.getInventory().getItemInMainHand();
                    if (inHand == null || inHand.getType().isAir()) {
                        player.sendMessage("§c✗ Halte ein Item in der Hand, das du hinzufügen möchtest.");
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
                    player.sendMessage("§c✗ Ungültige ID oder Preis!");
                }
                break;

            case "list":
                player.sendMessage("§6=== Shops ===");
                for (ShopManager.ShopData shop : shopManager.getAllShops()) {
                    player.sendMessage(String.format("§7%d: %s (%s)", shop.id, shop.name, shop.type));
                }
                break;

            default:
                player.sendMessage("§c✗ Unbekannter Subbefehl!");
                break;
        }

        return true;
    }
}
