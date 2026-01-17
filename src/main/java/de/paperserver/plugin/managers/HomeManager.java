package de.paperserver.plugin.managers;

import de.paperserver.plugin.PaperPluginSuite;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.*;

public class HomeManager {

    private final PaperPluginSuite plugin;
    private final Map<UUID, Map<String, Location>> playerHomes = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public HomeManager(PaperPluginSuite plugin) {
        this.plugin = plugin;
        loadHomes();
    }

    public boolean setHome(Player player, String homeName) {
        if (!player.hasPermission("plugin.home.set")) {
            player.sendMessage("§c✗ Du darfst keine Homes setzen!");
            return false;
        }

        Map<String, Location> homes = playerHomes.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
        int maxHomes = getMaxHomes(player);

        if (homes.size() >= maxHomes && !homes.containsKey(homeName)) {
            player.sendMessage("§c✗ Du hast dein Home-Limit erreicht! Max: " + maxHomes);
            return false;
        }

        homes.put(homeName, player.getLocation());
        player.sendMessage("§a✓ Home '" + homeName + "' gesetzt!");

        return true;
    }

    public boolean teleportToHome(Player player, String homeName) {
        if (!player.hasPermission("plugin.home.use")) {
            player.sendMessage("§c✗ Du darfst diesen Befehl nicht nutzen!");
            return false;
        }

        // Cooldown prüfen
        if (!cooldowns.containsKey(player.getUniqueId()) || 
            System.currentTimeMillis() > cooldowns.get(player.getUniqueId())) {
            
            Map<String, Location> homes = playerHomes.get(player.getUniqueId());
            if (homes == null || !homes.containsKey(homeName)) {
                player.sendMessage("§c✗ Home '" + homeName + "' nicht gefunden!");
                return false;
            }

            Location home = homes.get(homeName);
            
            // Warmup
            if (plugin.getConfig().getBoolean("homes.warmup.enabled", true)) {
                int warmupSeconds = plugin.getConfig().getInt("homes.warmup.seconds", 2);
                player.sendMessage("§6⏳ Teleportation in " + warmupSeconds + " Sekunden...");
                
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline()) {
                        player.teleport(home);
                        player.sendMessage("§a✓ Du wurdest zu Home '" + homeName + "' teleportiert!");
                    }
                }, warmupSeconds * 20L);
            } else {
                player.teleport(home);
                player.sendMessage("§a✓ Du wurdest zu Home '" + homeName + "' teleportiert!");
            }

            setCooldown(player);
            return true;
        }

        long remainingSeconds = (cooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
        player.sendMessage("§c✗ Cooldown! " + remainingSeconds + "s verbleibend.");
        return false;
    }

    public boolean deleteHome(Player player, String homeName) {
        if (!player.hasPermission("plugin.home.del")) {
            player.sendMessage("§c✗ Du darfst Homes nicht löschen!");
            return false;
        }

        Map<String, Location> homes = playerHomes.get(player.getUniqueId());
        if (homes == null || !homes.containsKey(homeName)) {
            player.sendMessage("§c✗ Home '" + homeName + "' nicht gefunden!");
            return false;
        }

        homes.remove(homeName);
        player.sendMessage("§a✓ Home '" + homeName + "' gelöscht!");

        return true;
    }

    public List<String> getHomes(Player player) {
        Map<String, Location> homes = playerHomes.get(player.getUniqueId());
        return homes != null ? new ArrayList<>(homes.keySet()) : new ArrayList<>();
    }

    private int getMaxHomes(Player player) {
        // Basierend auf Permissions
        for (int i = 50; i >= 1; i--) {
            if (player.hasPermission("plugin.home.limit." + i)) {
                return i;
            }
        }
        return plugin.getConfig().getInt("homes.home-limits.default", 1);
    }

    private void setCooldown(Player player) {
        long cooldownSeconds = plugin.getConfig().getLong("homes.cooldown.base-seconds", 20);
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (cooldownSeconds * 1000));
    }

    public void saveHomes() {
        // Placeholder für Datenspeicherung
    }

    public void loadHomes() {
        // Placeholder für Datenladen
    }
}
