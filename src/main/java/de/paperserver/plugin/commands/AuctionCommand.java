package de.paperserver.plugin.commands;

import de.paperserver.plugin.managers.AuctionManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AuctionCommand implements CommandExecutor {

    private final AuctionManager auctionManager;

    public AuctionCommand(AuctionManager auctionManager) {
        this.auctionManager = auctionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cDieser Befehl kann nur von Spielern genutzt werden!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // open GUI
            player.openInventory(de.paperserver.plugin.gui.AuctionGUI.createMainInventory(auctionManager));
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "create":
                if (args.length < 3) {
                    player.sendMessage("§c✗ Verwendung: /auction create <item> <preis>");
                    return true;
                }
                try {
                    double price = Double.parseDouble(args[2]);
                    auctionManager.createAuction(player, args[1], price);
                } catch (NumberFormatException e) {
                    player.sendMessage("§c✗ Ungültiger Preis!");
                }
                break;

            case "bid":
                if (args.length < 3) {
                    player.sendMessage("§c✗ Verwendung: /auction bid <auktion_id> <betrag>");
                    return true;
                }
                try {
                    int auctionId = Integer.parseInt(args[1]);
                    double amount = Double.parseDouble(args[2]);
                    auctionManager.placeBid(player, auctionId, amount);
                } catch (NumberFormatException e) {
                    player.sendMessage("§c✗ Ungültige ID oder Betrag!");
                }
                break;

            case "list":
                player.sendMessage("§6=== Aktive Auktionen ===");
                for (AuctionManager.AuctionData auction : auctionManager.getAllAuctions()) {
                    player.sendMessage(String.format("§7%d: %s - Höchstes Gebot: %f",
                            auction.id, auction.itemName, auction.highestBid));
                }
                break;

            default:
                player.sendMessage("§c✗ Unbekannter Subbefehl!");
                break;
        }

        return true;
    }
}
