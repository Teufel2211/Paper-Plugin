package de.paperserver.plugin.managers;

import de.paperserver.plugin.PaperPluginSuite;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.*;

public class AuctionManager {

    private final PaperPluginSuite plugin;
    private final Map<Integer, AuctionData> auctions = new HashMap<>();
    private int auctionIdCounter = 0;

    public class AuctionData {
        public int id;
        public UUID seller;
        public String itemName;
        public ItemStack item;
        public double startingPrice;
        public double highestBid;
        public UUID highestBidder;
        public long endTime;
        public List<BidData> bids = new ArrayList<>();

        public AuctionData(int id, UUID seller, String itemName, double price) {
            this(id, seller, null, itemName, price, System.currentTimeMillis() + (24 * 60 * 60 * 1000));
        }

        public AuctionData(int id, UUID seller, ItemStack item, String itemName, double price, long endTime) {
            this.id = id;
            this.seller = seller;
            this.item = item;
            this.itemName = itemName != null ? itemName : (item != null ? item.getType().toString() : "Unknown");
            this.startingPrice = price;
            this.highestBid = price;
            this.endTime = endTime;
        }
    }

    public static class BidData {
        public UUID bidder;
        public double amount;
        public long timestamp;

        public BidData(UUID bidder, double amount) {
            this.bidder = bidder;
            this.amount = amount;
            this.timestamp = System.currentTimeMillis();
        }
    }

    public AuctionManager(PaperPluginSuite plugin) {
        this.plugin = plugin;
        loadAuctions();
    }

    // Pending creation flow: player places an ItemStack into a temporary slot and then types starting price in chat
    private final Map<UUID, ItemStack> pendingCreations = new HashMap<>();

    public void startPendingCreation(Player player, ItemStack item) {
        pendingCreations.put(player.getUniqueId(), item);
    }

    public boolean finalizePendingCreation(Player player, double startingPrice) {
        ItemStack item = pendingCreations.remove(player.getUniqueId());
        if (item == null) return false;
        return createAuction(player, item, startingPrice, plugin.getConfig().getLong("auction.default-duration-ms", 24 * 60 * 60 * 1000));
    }

    public boolean createAuction(Player seller, String itemName, double startingPrice) {
        if (!seller.hasPermission("plugin.auction.create")) {
            seller.sendMessage("§c✗ Du darfst keine Auktionen erstellen!");
            return false;
        }

        double listingFee = plugin.getConfig().getDouble("auction.fees.listing-fee", 50.0);
        if (plugin.getEconomy() != null && !plugin.getEconomy().has(seller, listingFee)) {
            seller.sendMessage("§c✗ Du hast nicht genug Geld für die Auktionsgebühr!");
            return false;
        }

        AuctionData auction = new AuctionData(auctionIdCounter++, seller.getUniqueId(), itemName, startingPrice);
        auctions.put(auction.id, auction);

        if (plugin.getEconomy() != null) {
            plugin.getEconomy().withdrawPlayer(seller, listingFee);
        }

        seller.sendMessage("§a✓ Auktion erstellt! ID: " + auction.id);
        return true;
    }

    public boolean createAuction(Player seller, ItemStack item, double startingPrice, long durationMs) {
        if (!seller.hasPermission("plugin.auction.create")) {
            seller.sendMessage("§c✗ Du darfst keine Auktionen erstellen!");
            return false;
        }

        AuctionData auction = new AuctionData(auctionIdCounter++, seller.getUniqueId(), item, null, startingPrice, System.currentTimeMillis() + durationMs);
        auctions.put(auction.id, auction);

        seller.sendMessage("§a✓ Auktion erstellt! ID: " + auction.id);
        return true;
    }

    public boolean placeBid(Player bidder, int auctionId, double amount) {
        if (!bidder.hasPermission("plugin.auction.bid")) {
            bidder.sendMessage("§c✗ Du darfst nicht bieten!");
            return false;
        }

        AuctionData auction = auctions.get(auctionId);
        if (auction == null) {
            bidder.sendMessage("§c✗ Auktion nicht gefunden!");
            return false;
        }

        if (amount <= auction.highestBid) {
            bidder.sendMessage("§c✗ Gebot muss höher als " + auction.highestBid + " sein!");
            return false;
        }

        if (plugin.getEconomy() != null && !plugin.getEconomy().has(bidder, amount)) {
            bidder.sendMessage("§c✗ Du hast nicht genug Geld!");
            return false;
        }

        auction.highestBid = amount;
        auction.highestBidder = bidder.getUniqueId();
        auction.bids.add(new BidData(bidder.getUniqueId(), amount));

        bidder.sendMessage("§a✓ Gebot platziert: " + amount);
        return true;
    }

    public AuctionData getAuction(int id) {
        return auctions.get(id);
    }

    public List<AuctionData> getAllAuctions() {
        return new ArrayList<>(auctions.values());
    }

    public void saveAuctions() {
        // Placeholder für Datenspeicherung
    }

    public void loadAuctions() {
        // Placeholder für Datenladen
    }
}
