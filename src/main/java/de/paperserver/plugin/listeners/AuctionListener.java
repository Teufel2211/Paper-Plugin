package de.paperserver.plugin.listeners;

import de.paperserver.plugin.PaperPluginSuite;
import de.paperserver.plugin.gui.AuctionGUI;
import de.paperserver.plugin.managers.AuctionManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

public class AuctionListener implements Listener {

    private final PaperPluginSuite plugin;
    private final AuctionManager auctionManager;

    public AuctionListener(PaperPluginSuite plugin) {
        this.plugin = plugin;
        this.auctionManager = plugin.getAuctionManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView() == null || e.getCurrentItem() == null) return;
        String title = e.getView().getTitle();
        Player p = (Player) e.getWhoClicked();

        if (title != null && title.contains("Auction House")) {
            e.setCancelled(true);
            // Clicked an auction slot -> try place quick bid (min increment)
            int slot = e.getRawSlot();
            if (slot >= 0) {
                AuctionManager.AuctionData a = null;
                try {
                    a = auctionManager.getAllAuctions().get(slot);
                } catch (Exception ex) {
                    // ignore
                }
                if (a != null) {
                    // Direct buy at starting price
                    double buyPrice = a.startingPrice;
                    boolean ok = auctionManager.buyNow(p, a.id);
                    if (ok) {
                        // Update scoreboard after purchase
                        if (plugin.getMoneyScoreboardManager() != null) {
                            plugin.getMoneyScoreboardManager().updateMoneyDisplay(p);
                        }
                    }
                }
            }
        }

        // Create Auction GUI and chat-based creation removed: use /auction create while holding an item instead.
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        // noop
    }

    // Chat-based pending creation removed; creation must be done via command (/auction create while holding an item).
}
