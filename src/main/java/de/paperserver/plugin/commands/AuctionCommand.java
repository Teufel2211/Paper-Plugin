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
                // Usage: /auction create <preis> (halte ein Item in der Hand)
                if (args.length < 2) {
                    player.sendMessage("§c✗ Verwendung: /auction create <preis> (halte ein Item in der Hand)");
                    return true;
                }
                try {
                    double price = Double.parseDouble(args[1]);
                    org.bukkit.inventory.ItemStack inHand = player.getInventory().getItemInMainHand();
                    if (inHand == null || inHand.getType().isAir()) {
                        player.sendMessage("§c✗ Halte ein Item in der Hand, das du versteigern möchtest.");
                        return true;
                    }
                    double listingFee = player.getServer().getPluginManager().getPlugin("PaperPluginSuite").getConfig().getDouble("auction.fees.listing-fee", 50.0);
                    boolean ok = auctionManager.createAuction(player, inHand.clone(), price, player.getServer().getPluginManager().getPlugin("PaperPluginSuite").getConfig().getLong("auction.default-duration-ms", 24 * 60 * 60 * 1000));
                    if (ok) {
                        player.sendMessage("§a✓ Auktion erstellt!");
                        player.sendMessage("§6Gebühr: §c-" + listingFee);
                        // consume one item
                        int amount = inHand.getAmount();
                        if (amount <= 1) player.getInventory().setItemInMainHand(null);
                        else inHand.setAmount(amount - 1);
                    }
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

            case "buy":
                if (args.length < 2) {
                    player.sendMessage("§c✗ Verwendung: /auction buy <auktion_id>");
                    return true;
                }
                try {
                    int auctionId = Integer.parseInt(args[1]);
                    auctionManager.buyNow(player, auctionId);
                } catch (NumberFormatException e) {
                    player.sendMessage("§c✗ Ungültige Auktions-ID!");
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
