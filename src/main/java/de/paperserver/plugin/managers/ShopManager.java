package de.paperserver.plugin.managers;

import de.paperserver.plugin.PaperPluginSuite;
import org.bukkit.entity.Player;
import java.util.*;

public class ShopManager {

    private final PaperPluginSuite plugin;
    private final Map<Integer, ShopData> shops = new HashMap<>();
    private int shopIdCounter = 0;

    public class ShopData {
        public int id;
        public String name;
        public UUID owner;
        public String type; // "ADMIN" oder "PLAYER"
        public Map<String, Double> items = new HashMap<>();

        public ShopData(int id, String name, UUID owner) {
            this.id = id;
            this.name = name;
            this.owner = owner;
            this.type = "PLAYER";
        }
    }

    public ShopManager(PaperPluginSuite plugin) {
        this.plugin = plugin;
        loadShops();
    }

    public boolean createShop(Player creator, String shopName) {
        if (!creator.hasPermission("plugin.shop.create")) {
            creator.sendMessage("§c✗ Du darfst keine Shops erstellen!");
            return false;
        }

        int maxShopsPerPlayer = plugin.getConfig().getInt("shop.player-shops.max-per-player", 5);
        long playerShops = shops.values().stream()
                .filter(s -> s.owner.equals(creator.getUniqueId()))
                .count();

        if (playerShops >= maxShopsPerPlayer) {
            creator.sendMessage("§c✗ Du hast bereits zu viele Shops!");
            return false;
        }

        ShopData shop = new ShopData(shopIdCounter++, shopName, creator.getUniqueId());
        shops.put(shop.id, shop);
        creator.sendMessage("§a✓ Shop '" + shopName + "' erstellt! ID: " + shop.id);

        return true;
    }

    public boolean addItem(Player owner, int shopId, String itemName, double price) {
        ShopData shop = shops.get(shopId);
        if (shop == null) {
            owner.sendMessage("§c✗ Shop nicht gefunden!");
            return false;
        }

        if (!shop.owner.equals(owner.getUniqueId()) && !owner.hasPermission("plugin.shop.admin")) {
            owner.sendMessage("§c✗ Das ist nicht dein Shop!");
            return false;
        }

        double minPrice = plugin.getConfig().getDouble("shop.player-shops.min-price", 0.1);
        double maxPrice = plugin.getConfig().getDouble("shop.player-shops.max-price", 10000000.0);

        if (price < minPrice || price > maxPrice) {
            owner.sendMessage("§c✗ Preis muss zwischen " + minPrice + " und " + maxPrice + " liegen!");
            return false;
        }

        shop.items.put(itemName, price);
        owner.sendMessage("§a✓ Item '" + itemName + "' zu Shop hinzugefügt!");

        return true;
    }

    public ShopData getShop(int id) {
        return shops.get(id);
    }

    public List<ShopData> getAllShops() {
        return new ArrayList<>(shops.values());
    }

    public void saveShops() {
        // Placeholder für Datenspeicherung
    }

    public void loadShops() {
        // Placeholder für Datenladen
    }
}
