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
            else {
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
                        double min = Math.max(a.startingPrice, a.highestBid + 1.0);
                        boolean ok = auctionManager.placeBid(p, a.id, min);
                        if (ok) p.sendMessage(ChatColor.GREEN + "✓ Geboten: " + min);
                        else p.sendMessage(ChatColor.RED + "✗ Gebot fehlgeschlagen. Nutze /auction bid <id> <betrag>");
                    }
                }
            }
        }

        if (title != null && title.contains("Create Auction")) {
            e.setCancelled(true);
            if (e.getRawSlot() == 2) {
                // Cancel
                p.closeInventory();
                p.sendMessage(ChatColor.RED + "✗ Erstellung abgebrochen.");
                return;
            }
            if (e.getRawSlot() == 6) {
                // Confirm: take item from slot 4
                ItemStack item = e.getView().getItem(4);
                if (item == null || item.getType().isAir()) {
                    p.sendMessage(ChatColor.RED + "✗ Lege zuerst ein Item in Slot 5 ein.");
                    return;
                }
                // store pending and ask for starting price in chat
                auctionManager.startPendingCreation(p, item);
                p.closeInventory();
                p.sendMessage(ChatColor.YELLOW + "Bitte gib jetzt im Chat den Startpreis ein (z.B. 100.0)");
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        // noop
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (!auctionManagerHasPending(p)) return;
        e.setCancelled(true);
        String msg = e.getMessage();
        try {
            double price = Double.parseDouble(msg.trim());
            // finalize on main thread
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                boolean ok = auctionManager.finalizePendingCreation(p, price);
                if (ok) p.sendMessage(ChatColor.GREEN + "✓ Auktion erstellt mit Startpreis: " + price);
                else p.sendMessage(ChatColor.RED + "✗ Auktion konnte nicht erstellt werden.");
            });
        } catch (NumberFormatException ex) {
            p.sendMessage(ChatColor.RED + "✗ Ungültiger Preis. Vorgang abgebrochen.");
            auctionManager.startPendingCreation(p, null); // clear
        }
    }

    private boolean auctionManagerHasPending(Player p) {
        try {
            java.lang.reflect.Field f = AuctionManager.class.getDeclaredField("pendingCreations");
            f.setAccessible(true);
            Object map = f.get(auctionManager);
            if (map instanceof java.util.Map) {
                return ((java.util.Map) map).containsKey(p.getUniqueId());
            }
        } catch (Exception ignore) {}
        return false;
    }
}
