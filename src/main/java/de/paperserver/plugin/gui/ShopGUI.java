package de.paperserver.plugin.gui;

import de.paperserver.plugin.managers.ShopManager;
import de.paperserver.plugin.managers.ShopManager.ShopItem;
import de.paperserver.plugin.managers.ShopManager.ShopData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ShopGUI {

    public static Inventory createShopListInventory(de.paperserver.plugin.managers.ShopManager shopManager) {
        Inventory inv = Bukkit.createInventory((InventoryHolder) null, 54, ChatColor.DARK_AQUA + "Shops");

        int slot = 0;
        for (ShopData s : shopManager.getAllShops()) {
            ItemStack item = new ItemStack(org.bukkit.Material.CHEST);
            org.bukkit.inventory.meta.ItemMeta m = item.getItemMeta();
            if (m != null) {
                m.setDisplayName(s.name + " (#" + s.id + ")");
                item.setItemMeta(m);
            }
            inv.setItem(slot++, item);
            if (slot >= 54) break;
        }

        return inv;
    }

    public static Inventory createShopInventory(de.paperserver.plugin.managers.ShopManager shopManager, int shopId) {
        Inventory inv = Bukkit.createInventory((InventoryHolder) null, 54, ChatColor.GOLD + "Shop #" + shopId);

        Map<Integer, ShopItem> items = shopManager.getShopItems(shopId);
        int slot = 0;
        for (ShopItem si : items.values()) {
            ItemStack display = si.item.clone();
            org.bukkit.inventory.meta.ItemMeta m = display.getItemMeta();
            if (m != null) {
                java.util.List<String> lore = new java.util.ArrayList<>();
                lore.add("Preis: " + si.price);
                m.setLore(lore);
                display.setItemMeta(m);
            }
            inv.setItem(slot++, display);
            if (slot >= 54) break;
        }

        return inv;
    }
}
