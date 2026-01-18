package de.paperserver.plugin.managers;

import de.paperserver.plugin.PaperPluginSuite;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
        public Map<Integer, ShopItem> items = new HashMap<>();
        public int itemIdCounter = 0;

        public ShopData(int id, String name, UUID owner) {
            this.id = id;
            this.name = name;
            this.owner = owner;
            this.type = "PLAYER";
        }
    }

    public static class ShopItem {
        public int id;
        public ItemStack item;
        public double price;

        public ShopItem(int id, ItemStack item, double price) {
            this.id = id;
            this.item = item;
            this.price = price;
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

        // Create a placeholder item from the name
        ItemStack is = new ItemStack(org.bukkit.Material.PAPER);
        ItemMeta m = is.getItemMeta();
        if (m != null) {
            m.setDisplayName(itemName);
            is.setItemMeta(m);
        }

        int id = shop.itemIdCounter++;
        shop.items.put(id, new ShopItem(id, is, price));
        owner.sendMessage("§a✓ Item '" + itemName + "' zu Shop hinzugefügt!");

        return true;
    }

    public boolean addItem(Player owner, int shopId, ItemStack item, double price) {
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

        int id = shop.itemIdCounter++;
        shop.items.put(id, new ShopItem(id, item, price));
        owner.sendMessage("§a✓ Item zu Shop hinzugefügt!");

        return true;
    }

    public boolean buyItem(Player buyer, int shopId, int itemId) {
        ShopData shop = shops.get(shopId);
        if (shop == null) {
            buyer.sendMessage("§c✗ Shop nicht gefunden!");
            return false;
        }

        ShopItem si = shop.items.get(itemId);
        if (si == null) {
            buyer.sendMessage("§c✗ Item nicht gefunden!");
            return false;
        }

        if (plugin.getEconomy() == null) {
            buyer.sendMessage("§c✗ Economy nicht verfügbar! Kaufe nicht möglich.");
            return false;
        }

        if (!plugin.getEconomy().has(buyer, si.price)) {
            buyer.sendMessage("§c✗ Du hast nicht genug Geld!");
            return false;
        }

        plugin.getEconomy().withdrawPlayer(buyer, si.price);
        // Pay seller if exists
        if (shop.owner != null) {
            org.bukkit.OfflinePlayer off = plugin.getServer().getOfflinePlayer(shop.owner);
            if (off.isOnline()) {
                plugin.getEconomy().depositPlayer(off.getPlayer(), si.price);
            } else {
                // offline deposit if supported
                plugin.getEconomy().depositPlayer(off.getName(), si.price);
            }
        }

        // give item
        org.bukkit.inventory.ItemStack toGive = si.item.clone();
        org.bukkit.entity.Player p = buyer;
        java.util.HashMap<Integer, org.bukkit.inventory.ItemStack> leftover = p.getInventory().addItem(toGive);
        if (!leftover.isEmpty()) {
            // drop remaining
            leftover.values().forEach(it -> p.getWorld().dropItemNaturally(p.getLocation(), it));
        }

        buyer.sendMessage("§a✓ Du hast das Item gekauft für " + si.price);
        return true;
    }

    public Map<Integer, ShopItem> getShopItems(int shopId) {
        ShopData shop = shops.get(shopId);
        return shop != null ? new HashMap<>(shop.items) : new HashMap<>();
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
