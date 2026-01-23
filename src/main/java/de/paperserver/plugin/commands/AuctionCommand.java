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
            sender.sendMessage("§cThis command can only be used by players!");
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
                // Usage: /auction create <price> (hold an item in your hand)
                if (args.length < 2) {
                    player.sendMessage("§c✗ Usage: /auction create <price> (hold an item in your hand)");
                    return true;
                }
                try {
                    double price = Double.parseDouble(args[1]);
                    org.bukkit.inventory.ItemStack inHand = player.getInventory().getItemInMainHand();
                    if (inHand == null || inHand.getType().isAir()) {
                        player.sendMessage("§c✗ Hold an item in your hand that you want to auction.");
                        return true;
                    }
                    double listingFee = player.getServer().getPluginManager().getPlugin("PaperPluginSuite").getConfig().getDouble("auction.fees.listing-fee", 50.0);
                    boolean ok = auctionManager.createAuction(player, inHand.clone(), price, player.getServer().getPluginManager().getPlugin("PaperPluginSuite").getConfig().getLong("auction.default-duration-ms", 24 * 60 * 60 * 1000));
                    if (ok) {
                        player.sendMessage("§a✓ Auction created!");
                        player.sendMessage("§6Fee: §c-" + listingFee);
                        // consume one item
                        int amount = inHand.getAmount();
                        if (amount <= 1) player.getInventory().setItemInMainHand(null);
                        else inHand.setAmount(amount - 1);
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage("§c✗ Invalid price!");
                }
                break;

            case "buy":
                if (args.length < 2) {
                    player.sendMessage("§c✗ Usage: /auction buy <auction_id>");
                    return true;
                }
                try {
                    int auctionId = Integer.parseInt(args[1]);
                    auctionManager.buyNow(player, auctionId);
                } catch (NumberFormatException e) {
                    player.sendMessage("§c✗ Invalid auction ID!");
                }
                break;

            case "list":
                player.sendMessage("§6=== Active Auctions ===");
                for (AuctionManager.AuctionData auction : auctionManager.getAllAuctions()) {
                    player.sendMessage(String.format("§7%d: %s - Price: %f",
                            auction.id, auction.itemName, auction.highestBid));
                }
                break;

            default:
                player.sendMessage("§c✗ Unknown subcommand!");
                break;
        }

        return true;
    }
}
