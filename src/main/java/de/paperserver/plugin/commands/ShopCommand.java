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
