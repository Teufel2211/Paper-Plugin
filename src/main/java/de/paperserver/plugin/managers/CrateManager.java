package de.paperserver.plugin.managers;

import de.paperserver.plugin.PaperPluginSuite;
import org.bukkit.entity.Player;
import java.io.File;
import java.util.*;

public class CrateManager {

    private final PaperPluginSuite plugin;
    private final Map<String, CrateType> crateTypes = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public class CrateType {
        public String name;
        public String displayName;
        public Map<String, CrateItem> items = new HashMap<>();
        public int totalWeight = 0;

        public CrateType(String name, String displayName) {
            this.name = name;
            this.displayName = displayName;
        }
    }

    public static class CrateItem {
        public String itemName;
        public int amount;
        public int weight;

        public CrateItem(String itemName, int amount, int weight) {
            this.itemName = itemName;
            this.amount = amount;
            this.weight = weight;
        }
    }

    public CrateManager(PaperPluginSuite plugin) {
        this.plugin = plugin;
        ensureDataFolder();
        // Reload config to get latest changes
        reloadCratesConfig();
        loadCrates();
    }

    private File cratesFile;
    private org.bukkit.configuration.file.YamlConfiguration cratesConfig;

    private void ensureDataFolder() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();
        cratesFile = new File(dataFolder, "crates.yml");
        
        // If file doesn't exist, create it with default content
        if (!cratesFile.exists()) {
            try {
                // Get the default config from resources
                java.io.InputStream in = plugin.getResource("config-crates.yml");
                if (in != null) {
                    java.io.FileOutputStream out = new java.io.FileOutputStream(cratesFile);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.close();
                    in.close();
                    System.out.println("§a[CRATE DEBUG] Created crates.yml from resources");
                } else {
                    System.out.println("§c[CRATE DEBUG] config-crates.yml not found in resources!");
                    cratesFile.createNewFile();
                }
            } catch (Exception e) {
                System.out.println("§c[CRATE DEBUG] Error creating crates.yml: " + e.getMessage());
                try { cratesFile.createNewFile(); } catch (Exception ignore) {}
            }
        }
        reloadCratesConfig();
    }

    private void reloadCratesConfig() {
        cratesConfig = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(cratesFile);
        System.out.println("§a[CRATE DEBUG] reloadCratesConfig called, file exists: " + cratesFile.exists() + ", size: " + cratesFile.length());
    }

    public boolean openCrate(Player player, String crateName) {
        if (!player.hasPermission("plugin.crate.use")) {
            player.sendMessage("§c✗ You don't have permission to use this command!");
            return false;
        }

        if (cooldowns.containsKey(player.getUniqueId())) {
            long remaining = (cooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
            if (remaining > 0) {
                player.sendMessage("§c✗ Cooldown! " + remaining + "s remaining.");
                return false;
            }
        }

        CrateType crateType = crateTypes.get(crateName);
        if (crateType == null) {
            player.sendMessage("§c✗ Crate type '" + crateName + "' not found!");
            return false;
        }

        CrateItem reward = rollReward(crateType);
        if (reward == null) {
            player.sendMessage("§c✗ Error rolling reward!");
            return false;
        }

        // Create and give the item to the player
        org.bukkit.Material material = org.bukkit.Material.getMaterial(reward.itemName);
        if (material != null) {
            org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(material, reward.amount);
            player.getInventory().addItem(item);
            player.sendMessage("§a✓ You received " + reward.amount + "x " + reward.itemName + " from " + crateType.displayName + "!");
        } else {
            player.sendMessage("§c✗ Item '" + reward.itemName + "' does not exist!");
            return false;
        }

        setCooldown(player);
        return true;
    }

    public boolean giveCrate(Player admin, Player target, String crateName, int amount) {
        if (!admin.hasPermission("plugin.crate.give")) {
            admin.sendMessage("§c✗ You don't have permission to give crates!");
            return false;
        }

        if (!crateTypes.containsKey(crateName)) {
            admin.sendMessage("§c✗ Crate type '" + crateName + "' not found!");
            return false;
        }

        admin.sendMessage("§a✓ " + amount + "x " + crateName + " given to " + target.getName() + "!");
        target.sendMessage("§a✓ You received " + amount + "x " + crateName + "!");

        return true;
    }

    public CrateType getCrateType(String name) {
        return crateTypes.get(name);
    }

    public List<CrateType> getAllCrateTypes() {
        return new ArrayList<>(crateTypes.values());
    }

    private CrateItem rollReward(CrateType crateType) {
        if (crateType.totalWeight <= 0) {
            System.out.println("§c[CRATE DEBUG] Crate " + crateType.name + " has invalid totalWeight: " + crateType.totalWeight);
            System.out.println("§c[CRATE DEBUG] Items in crate: " + crateType.items.size());
            for (CrateItem item : crateType.items.values()) {
                System.out.println("§c[CRATE DEBUG]   - " + item.itemName + " (weight: " + item.weight + ")");
            }
            return null;
        }
        
        int random = new Random().nextInt(crateType.totalWeight);
        int current = 0;

        for (CrateItem item : crateType.items.values()) {
            current += item.weight;
            if (random < current) {
                return item;
            }
        }

        // Fallback: return first item if nothing matched
        if (!crateType.items.isEmpty()) {
            return crateType.items.values().iterator().next();
        }
        return null;
    }

    private void setCooldown(Player player) {
        long cooldownSeconds = plugin.getConfig().getLong("crates.cooldowns.base-seconds", 30);
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (cooldownSeconds * 1000));
    }

    public void saveCrates() {
        if (cratesConfig == null || cratesFile == null) return;
        cratesConfig.set("crates", null);
        for (CrateType ct : crateTypes.values()) {
            String path = "crates." + ct.name;
            cratesConfig.set(path + ".displayName", ct.displayName);
            cratesConfig.set(path + ".totalWeight", ct.totalWeight);
            for (Map.Entry<String, CrateItem> e : ct.items.entrySet()) {
                String p2 = path + ".items." + e.getKey();
                cratesConfig.set(p2 + ".itemName", e.getValue().itemName);
                cratesConfig.set(p2 + ".amount", e.getValue().amount);
                cratesConfig.set(p2 + ".weight", e.getValue().weight);
            }
        }
        try { cratesConfig.save(cratesFile); } catch (Exception ex) { ex.printStackTrace(); }
    }

    public void loadCrates() {
        if (cratesConfig == null || cratesFile == null) {
            System.out.println("§c[CRATE DEBUG] cratesConfig or cratesFile is null!");
            return;
        }
        crateTypes.clear();
        
        System.out.println("§a[CRATE DEBUG] Loading crates from: " + cratesFile.getAbsolutePath());
        
        if (cratesConfig.getConfigurationSection("crates") == null) {
            System.out.println("§c[CRATE DEBUG] No 'crates' section found in config!");
            return;
        }
        
        System.out.println("§a[CRATE DEBUG] Found crates section with keys: " + cratesConfig.getConfigurationSection("crates").getKeys(false));
        
        // Skip non-crate keys like "enabled", "cooldowns", "keys", "limits", etc.
        for (String name : cratesConfig.getConfigurationSection("crates").getKeys(false)) {
            // Skip configuration keys that aren't crate definitions
            if (name.equals("enabled") || name.equals("cooldowns") || name.equals("keys") || 
                name.equals("limits") || name.equals("animation") || name.equals("broadcast") || 
                name.equals("antiabuse") || name.equals("database")) {
                System.out.println("§a[CRATE DEBUG] Skipping config key: " + name);
                continue;
            }
            
            String path = "crates." + name;
            
            // Check if this is actually a crate type (has displayName)
            if (!cratesConfig.contains(path + ".displayName")) {
                System.out.println("§c[CRATE DEBUG] Skipping " + name + " - no displayName found");
                continue;
            }
            
            String disp = cratesConfig.getString(path + ".displayName", name);
            System.out.println("§a[CRATE DEBUG] Loading crate: " + name + " -> " + disp);
            
            CrateType ct = new CrateType(name, disp);
            ct.totalWeight = 0; // Recalculate weight from items
            
            if (cratesConfig.getConfigurationSection(path + ".items") != null) {
                for (String ik : cratesConfig.getConfigurationSection(path + ".items").getKeys(false)) {
                    String p2 = path + ".items." + ik;
                    String itemName = cratesConfig.getString(p2 + ".itemName", "");
                    int amount = cratesConfig.getInt(p2 + ".amount", 1);
                    int weight = cratesConfig.getInt(p2 + ".weight", 1);
                    CrateItem ci = new CrateItem(itemName, amount, weight);
                    ct.items.put(ik, ci);
                    ct.totalWeight += weight;
                    System.out.println("§a[CRATE DEBUG]   - Item: " + itemName + " (x" + amount + ", weight " + weight + ")");
                }
            }
            
            System.out.println("§a[CRATE DEBUG] Final totalWeight for " + name + ": " + ct.totalWeight);
            crateTypes.put(name, ct);
        }
        
        System.out.println("§a[CRATE DEBUG] Total crates loaded: " + crateTypes.size());
    }
}
