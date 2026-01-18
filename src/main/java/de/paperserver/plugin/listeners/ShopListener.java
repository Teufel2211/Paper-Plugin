package de.paperserver.plugin.listeners;

import de.paperserver.plugin.PaperPluginSuite;
import de.paperserver.plugin.gui.ShopGUI;
import de.paperserver.plugin.managers.ShopManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ShopListener implements Listener {

    private final PaperPluginSuite plugin;
    private final ShopManager shopManager;

    public ShopListener(PaperPluginSuite plugin) {
        this.plugin = plugin;
        this.shopManager = plugin.getShopManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView() == null || e.getCurrentItem() == null) return;
        String title = e.getView().getTitle();
        if (title == null) return;
        Player p = (Player) e.getWhoClicked();

        if (title.contains("Shops")) {
            e.setCancelled(true);
            int slot = e.getRawSlot();
            if (slot < 0) return;
            // Open selected shop
            int idx = slot; // mapping simple: slot -> shop index
            try {
                java.util.List<ShopManager.ShopData> shops = shopManager.getAllShops();
                if (idx >= shops.size()) return;
                ShopManager.ShopData s = shops.get(idx);
                p.closeInventory();
                p.openInventory(ShopGUI.createShopInventory(shopManager, s.id));
            } catch (Exception ex) {
                // ignore
            }
        }

        if (title.contains("Shop #")) {
            e.setCancelled(true);
            // Buy item by slot -> item id approx by slot index
            int slot = e.getRawSlot();
            if (slot < 0) return;
            String[] parts = title.split("#");
            if (parts.length < 2) return;
            try {
                int shopId = Integer.parseInt(parts[1]);
                // Approximate mapping: slot index to item id by ordering
                java.util.List<Integer> ids = new java.util.ArrayList<>(shopManager.getShopItems(shopId).keySet());
                if (slot >= ids.size()) return;
                int itemId = ids.get(slot);
                boolean ok = shopManager.buyItem(p, shopId, itemId);
                if (ok) {
                    p.sendMessage(ChatColor.GREEN + "✓ Kauf erfolgreich.");
                    // Update scoreboard after purchase
                    if (plugin.getMoneyScoreboardManager() != null) {
                        plugin.getMoneyScoreboardManager().updateMoneyDisplay(p);
                    }
                } else {
                    p.sendMessage(ChatColor.RED + "✗ Kauf fehlgeschlagen.");
                }
            } catch (NumberFormatException ignored) {}
        }
    }
}
