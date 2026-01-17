package de.paperserver.plugin.managers;

import de.paperserver.plugin.PaperPluginSuite;
import org.bukkit.entity.Player;
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
        loadCrates();
    }

    public boolean openCrate(Player player, String crateName) {
        if (!player.hasPermission("plugin.crate.use")) {
            player.sendMessage("§c✗ Du darfst diesen Befehl nicht nutzen!");
            return false;
        }

        if (cooldowns.containsKey(player.getUniqueId())) {
            long remaining = (cooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
            if (remaining > 0) {
                player.sendMessage("§c✗ Cooldown! " + remaining + "s verbleibend.");
                return false;
            }
        }

        CrateType crateType = crateTypes.get(crateName);
        if (crateType == null) {
            player.sendMessage("§c✗ Crate-Typ '" + crateName + "' nicht gefunden!");
            return false;
        }

        CrateItem reward = rollReward(crateType);
        if (reward == null) {
            player.sendMessage("§c✗ Fehler beim Rollen der Belohnung!");
            return false;
        }

        player.sendMessage("§a✓ Du hast " + reward.amount + "x " + reward.itemName + " gewonnen!");
        setCooldown(player);

        return true;
    }

    public boolean giveCrate(Player admin, Player target, String crateName, int amount) {
        if (!admin.hasPermission("plugin.crate.give")) {
            admin.sendMessage("§c✗ Du darfst keine Crates geben!");
            return false;
        }

        if (!crateTypes.containsKey(crateName)) {
            admin.sendMessage("§c✗ Crate-Typ '" + crateName + "' nicht gefunden!");
            return false;
        }

        admin.sendMessage("§a✓ " + amount + "x " + crateName + " an " + target.getName() + " gegeben!");
        target.sendMessage("§a✓ Du hast " + amount + "x " + crateName + " erhalten!");

        return true;
    }

    public CrateType getCrateType(String name) {
        return crateTypes.get(name);
    }

    public List<CrateType> getAllCrateTypes() {
        return new ArrayList<>(crateTypes.values());
    }

    private CrateItem rollReward(CrateType crateType) {
        int random = new Random().nextInt(crateType.totalWeight);
        int current = 0;

        for (CrateItem item : crateType.items.values()) {
            current += item.weight;
            if (random < current) {
                return item;
            }
        }

        return null;
    }

    private void setCooldown(Player player) {
        long cooldownSeconds = plugin.getConfig().getLong("crates.cooldowns.base-seconds", 30);
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (cooldownSeconds * 1000));
    }

    public void saveCrates() {
        // Placeholder für Datenspeicherung
    }

    public void loadCrates() {
        // Placeholder für Datenladen
    }
}
