package de.paperserver.plugin.managers;

import de.paperserver.plugin.PaperPluginSuite;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
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
        ensureDataFolder();
        loadAuctions();
        // start expiration checker every minute
        Bukkit.getScheduler().runTaskTimer(plugin, this::checkExpiredAuctions, 20L * 60L, 20L * 60L);
    }

    private File auctionsFile;
    private YamlConfiguration auctionsConfig;

    private void ensureDataFolder() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();
        auctionsFile = new File(dataFolder, "auctions.yml");
        if (!auctionsFile.exists()) {
            try {
                auctionsFile.createNewFile();
            } catch (Exception ignore) {}
        }
        auctionsConfig = YamlConfiguration.loadConfiguration(auctionsFile);
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

        double listingFee = plugin.getConfig().getDouble("auction.fees.listing-fee", 50.0);
        if (plugin.getEconomy() != null && !plugin.getEconomy().has(seller, listingFee)) {
            seller.sendMessage("§c✗ Du hast nicht genug Geld für die Auktionsgebühr!");
            return false;
        }

        AuctionData auction = new AuctionData(auctionIdCounter++, seller.getUniqueId(), item, null, startingPrice, System.currentTimeMillis() + durationMs);
        auctions.put(auction.id, auction);

        if (plugin.getEconomy() != null) {
            plugin.getEconomy().withdrawPlayer(seller, listingFee);
        }

        seller.sendMessage("§a✓ Auktion erstellt! ID: " + auction.id);
        return true;
    }

    public List<AuctionData> getAllAuctions() {
        return new ArrayList<>(auctions.values());
    }

    public boolean buyNow(Player buyer, int auctionId) {
        AuctionData auction = auctions.get(auctionId);
        if (auction == null) {
            buyer.sendMessage("§c✗ Auktion nicht gefunden!");
            return false;
        }

        if (buyer.getUniqueId().equals(auction.seller)) {
            buyer.sendMessage("§c✗ Du kannst deine eigene Auktion nicht kaufen!");
            return false;
        }

        // Buy at starting price (direct purchase)
        double buyPrice = auction.startingPrice;
        if (plugin.getEconomy() != null) {
            if (!plugin.getEconomy().has(buyer, buyPrice)) {
                buyer.sendMessage("§c✗ Du hast nicht genug Geld! Benötigt: " + buyPrice);
                return false;
            }

            plugin.getEconomy().withdrawPlayer(buyer, buyPrice);
            plugin.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(auction.seller), buyPrice);
        }

        // Give item to buyer
        if (auction.item != null) {
            buyer.getInventory().addItem(auction.item.clone());
        }

        // Remove auction
        auctions.remove(auctionId);
        buyer.sendMessage("§a✓ Auktion gekauft für §6" + buyPrice);
        
        org.bukkit.entity.Player seller = Bukkit.getPlayer(auction.seller);
        if (seller != null && seller.isOnline()) {
            seller.sendMessage("§a✓ Deine Auktion wurde für §6" + buyPrice + "§a verkauft!");
        }

        return true;
    }

    public void saveAuctions() {
        if (auctionsConfig == null || auctionsFile == null) return;
        auctionsConfig.set("auctions", null);
        for (Map.Entry<Integer, AuctionData> e : auctions.entrySet()) {
            String path = "auctions." + e.getKey();
            AuctionData a = e.getValue();
            auctionsConfig.set(path + ".id", a.id);
            auctionsConfig.set(path + ".seller", a.seller.toString());
            auctionsConfig.set(path + ".item", a.item);
            auctionsConfig.set(path + ".itemName", a.itemName);
            auctionsConfig.set(path + ".startingPrice", a.startingPrice);
            auctionsConfig.set(path + ".highestBid", a.highestBid);
            auctionsConfig.set(path + ".highestBidder", a.highestBidder != null ? a.highestBidder.toString() : null);
            auctionsConfig.set(path + ".endTime", a.endTime);
        }
        try {
            auctionsConfig.save(auctionsFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadAuctions() {
        if (auctionsConfig == null || auctionsFile == null) return;
        auctions.clear();
        auctionIdCounter = 0;
        if (auctionsConfig.getConfigurationSection("auctions") == null) return;
        for (String key : auctionsConfig.getConfigurationSection("auctions").getKeys(false)) {
            String path = "auctions." + key;
            int id = auctionsConfig.getInt(path + ".id", -1);
            if (id < 0) continue;
            UUID seller = UUID.fromString(auctionsConfig.getString(path + ".seller"));
            ItemStack item = auctionsConfig.getItemStack(path + ".item");
            String itemName = auctionsConfig.getString(path + ".itemName");
            double startingPrice = auctionsConfig.getDouble(path + ".startingPrice", 0.0);
            double highestBid = auctionsConfig.getDouble(path + ".highestBid", startingPrice);
            String hb = auctionsConfig.getString(path + ".highestBidder", null);
            UUID highestBidder = hb != null ? UUID.fromString(hb) : null;
            long endTime = auctionsConfig.getLong(path + ".endTime", System.currentTimeMillis() + (24 * 60 * 60 * 1000));

            AuctionData a = new AuctionData(id, seller, item, itemName, startingPrice, endTime);
            a.highestBid = highestBid;
            a.highestBidder = highestBidder;
            auctions.put(a.id, a);
            auctionIdCounter = Math.max(auctionIdCounter, id + 1);
        }
    }

    private void checkExpiredAuctions() {
        long now = System.currentTimeMillis();
        List<Integer> toRemove = new ArrayList<>();
        for (AuctionData a : new ArrayList<>(auctions.values())) {
            if (a.endTime <= now) {
                // finalize
                if (a.highestBidder != null) {
                    // pay seller
                    try {
                        org.bukkit.OfflinePlayer seller = plugin.getServer().getOfflinePlayer(a.seller);
                        if (plugin.getEconomy() != null) {
                            plugin.getEconomy().depositPlayer(seller.getName(), a.highestBid);
                        }
                        // deliver item to winner if online
                        org.bukkit.entity.Player winner = plugin.getServer().getPlayer(a.highestBidder);
                        if (winner != null && winner.isOnline()) {
                            java.util.HashMap<Integer, ItemStack> leftover = winner.getInventory().addItem(a.item.clone());
                            if (!leftover.isEmpty()) winner.getWorld().dropItemNaturally(winner.getLocation(), leftover.values().iterator().next());
                            winner.sendMessage("§a✓ Du hast eine Auktion gewonnen (ID: " + a.id + ")!");
                        } else {
                            // store item as pending claim
                            String path = "pending." + a.highestBidder + "." + a.id;
                            auctionsConfig.set(path + ".item", a.item);
                            auctionsConfig.set(path + ".info", "Won auction " + a.id + " but was offline; claim from server admin or via /claim");
                            try { auctionsConfig.save(auctionsFile); } catch (Exception ignore) {}
                        }
                        // notify seller if online
                        if (seller.isOnline()) seller.getPlayer().sendMessage("§a✓ Deine Auktion #" + a.id + " wurde verkauft für " + a.highestBid);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    // no bids: return item to seller if online, else store as pending
                    org.bukkit.OfflinePlayer seller = plugin.getServer().getOfflinePlayer(a.seller);
                    if (seller.isOnline()) {
                        org.bukkit.entity.Player s = seller.getPlayer();
                        java.util.HashMap<Integer, ItemStack> leftover = s.getInventory().addItem(a.item.clone());
                        if (!leftover.isEmpty()) s.getWorld().dropItemNaturally(s.getLocation(), leftover.values().iterator().next());
                        s.sendMessage("§a✓ Deine Auktion #" + a.id + " ist beendet: kein Gebot. Item retour.");
                    } else {
                        String path = "pending." + a.seller + "." + a.id;
                        auctionsConfig.set(path + ".item", a.item);
                        auctionsConfig.set(path + ".info", "Auction ended without bids; item returned to seller");
                        try { auctionsConfig.save(auctionsFile); } catch (Exception ignore) {}
                    }
                }

                toRemove.add(a.id);
            }
        }
        for (Integer id : toRemove) auctions.remove(id);
        if (!toRemove.isEmpty()) saveAuctions();
    }
}
