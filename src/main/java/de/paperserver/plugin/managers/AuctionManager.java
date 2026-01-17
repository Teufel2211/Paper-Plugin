package de.paperserver.plugin.managers;

import de.paperserver.plugin.PaperPluginSuite;
import org.bukkit.entity.Player;
import java.util.*;

public class AuctionManager {

    private final PaperPluginSuite plugin;
    private final Map<Integer, AuctionData> auctions = new HashMap<>();
    private int auctionIdCounter = 0;

    public class AuctionData {
        public int id;
        public UUID seller;
        public String itemName;
        public double startingPrice;
        public double highestBid;
        public UUID highestBidder;
        public long endTime;
        public List<BidData> bids = new ArrayList<>();

        public AuctionData(int id, UUID seller, String itemName, double price) {
            this.id = id;
            this.seller = seller;
            this.itemName = itemName;
            this.startingPrice = price;
            this.highestBid = price;
            this.endTime = System.currentTimeMillis() + (24 * 60 * 60 * 1000); // 24h
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
