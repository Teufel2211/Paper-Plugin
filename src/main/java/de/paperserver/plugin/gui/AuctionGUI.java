package de.paperserver.plugin.gui;

import de.paperserver.plugin.managers.AuctionManager;
import de.paperserver.plugin.managers.AuctionManager.AuctionData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class AuctionGUI {

    public static class MainHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public static class CreateHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    public static Inventory createMainInventory(AuctionManager auctionManager) {
        Inventory inv = Bukkit.createInventory(new MainHolder(), 54, ChatColor.DARK_GREEN + "Auction House");

        int slot = 0;
        for (AuctionData a : auctionManager.getAllAuctions()) {
            ItemStack display;
            if (a.item != null) {
                display = a.item.clone();
            } else {
                display = new ItemStack(Material.PAPER);
                ItemMeta m = display.getItemMeta();
                if (m != null) m.setDisplayName(a.itemName);
                display.setItemMeta(m);
            }

            ItemMeta meta = display.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("ID: " + a.id);
            lore.add("Seller: " + Bukkit.getOfflinePlayer(a.seller).getName());
            lore.add(String.format("Start: %.2f", a.startingPrice));
            lore.add(String.format("Highest: %.2f", a.highestBid));
            long remaining = Math.max(0, a.endTime - System.currentTimeMillis());
            lore.add("Time left: " + (remaining / 1000 / 60) + "m");

            if (meta != null) {
                meta.setLore(lore);
                display.setItemMeta(meta);
            }

            inv.setItem(slot++, display);
            if (slot >= 54) break;
        }

        // (Create button removed - use /auction create or /auction creategui)

        return inv;
    }

    public static Inventory createCreateInventory(Player player) {
        Inventory inv = Bukkit.createInventory(new CreateHolder(), 9, ChatColor.GOLD + "Create Auction");

        ItemStack placeholder = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta pm = placeholder.getItemMeta();
        if (pm != null) pm.setDisplayName(" ");
        placeholder.setItemMeta(pm);

        for (int i = 0; i < 9; i++) inv.setItem(i, placeholder);

        inv.setItem(2, makeButton(Material.RED_CONCRETE, ChatColor.RED + "Cancel"));
        inv.setItem(4, null); // Item slot
        inv.setItem(6, makeButton(Material.LIME_CONCRETE, ChatColor.GREEN + "Confirm (set price in chat)"));

        return inv;
    }

    private static ItemStack makeButton(Material mat, String name) {
        ItemStack it = new ItemStack(mat);
        ItemMeta m = it.getItemMeta();
        if (m != null) m.setDisplayName(name);
        it.setItemMeta(m);
        return it;
    }
}
