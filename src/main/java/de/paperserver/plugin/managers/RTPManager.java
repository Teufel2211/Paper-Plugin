package de.paperserver.plugin.managers;

import de.paperserver.plugin.PaperPluginSuite;
import de.paperserver.plugin.utils.Logger;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class RTPManager {

    private final PaperPluginSuite plugin;
    private final Map<UUID, Long> cooldowns = new HashMap<>();
    private final Map<UUID, BukkitTask> warmupTasks = new HashMap<>();
    private final Map<UUID, Long> hourlyAttempts = new HashMap<>();

    public RTPManager(PaperPluginSuite plugin) {
        this.plugin = plugin;
    }

    public boolean teleportRandomly(Player player, String worldName) {
        // Validierung
        if (!canTeleport(player)) {
            return false;
        }

        World world = Bukkit.getWorld(worldName != null ? worldName : player.getWorld().getName());
        if (world == null) {
            player.sendMessage("§c✗ Welt nicht gefunden!");
            return false;
        }

        // Blacklist-Prüfung
        if (isWorldBlacklisted(world.getName())) {
            player.sendMessage("§c✗ Teleportation in dieser Welt nicht erlaubt!");
            return false;
        }

        // Antrag auf Koordinaten
        Location randomLocation = findSafeLocation(world);
        if (randomLocation == null) {
            player.sendMessage("§c✗ Keine sichere Location gefunden! Versuche später erneut.");
            return false;
        }

        // Kosten abziehen
        if (!deductCost(player)) {
            player.sendMessage("§c✗ Du hast nicht genug Geld! Benötigt: " + getConfigDouble("cost.amount"));
            return false;
        }

        // Warmup
        if (getConfigBoolean("warmup.enabled")) {
            startWarmup(player, randomLocation);
        } else {
            executeTP(player, randomLocation);
        }

        return true;
    }

    private Location findSafeLocation(World world) {
        ConfigurationSection cfg = plugin.getConfig().getConfigurationSection("rtp");
        if (cfg == null) return null;

        int minDist = Math.max(0, cfg.getInt("teleport.min-distance", 100));
        int maxDist = Math.max(minDist + 1, cfg.getInt("teleport.max-distance", 10000));
        int minHeight = cfg.getInt("teleport.min-height", 60);
        int maxHeight = Math.max(minHeight + 1, cfg.getInt("teleport.max-height", 320));

        Random random = ThreadLocalRandom.current();
        int attempts = cfg.getInt("teleport.search-attempts", 50);

        for (int i = 0; i < attempts; i++) {
            int dx = random.nextInt(maxDist - minDist + 1) + minDist;
            if (random.nextBoolean()) dx = -dx;

            int dz = random.nextInt(maxDist - minDist + 1) + minDist;
            if (random.nextBoolean()) dz = -dz;

            int x = dx;
            int z = dz;

            int highestY = world.getHighestBlockYAt(x, z);
            int y = highestY;
            if (y < minHeight) y = minHeight;
            if (y > maxHeight) y = maxHeight;

            Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);

            if (isSafeLocation(loc)) return loc;
        }

        return null;
    }

    private boolean isSafeLocation(Location loc) {
        ConfigurationSection cfg = plugin.getConfig().getConfigurationSection("rtp");
        if (cfg == null) return false;
        Block block = loc.getBlock();
        Block below = loc.clone().add(0, -1, 0).getBlock();
        Block above = loc.clone().add(0, 1, 0).getBlock();

        // Lava / Flüssigkeit Prüfung (inkl. Wasser)
        if (cfg.getBoolean("safety.check-lava", true) || cfg.getBoolean("safety.check-water", true)) {
            if (block.isLiquid() || below.isLiquid()) return false;
        }

        // Block über Kopf muss nicht solid sein
        if (cfg.getBoolean("safety.check-block-above", true)) {
            if (above.getType().isSolid()) return false;
        }

        // Fester Boden: below sollte solid sein
        if (cfg.getBoolean("safety.check-ground", true)) {
            if (!below.getType().isSolid()) return false;
        }

        // Avoid spawning on lava, cactus, fire, magma
        Material belowType = below.getType();
        if (belowType == Material.CACTUS || belowType == Material.FIRE || belowType == Material.MAGMA_BLOCK) return false;

        return true;
    }

    private void startWarmup(Player player, Location target) {
        int warmupSeconds = plugin.getConfig().getInt("rtp.warmup.seconds", 3);
        
        player.sendMessage("§6⏳ Teleportation in " + warmupSeconds + " Sekunden...");

        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                executeTP(player, target);
                warmupTasks.remove(player.getUniqueId());
            }
        }, warmupSeconds * 20L);

        warmupTasks.put(player.getUniqueId(), task);
    }

    private void executeTP(Player player, Location location) {
        player.teleport(location);
        player.sendMessage("§a✓ Du wurdest teleportiert!");
        setCooldown(player);
    }

    private boolean canTeleport(Player player) {
        if (!player.hasPermission("plugin.rtp.use")) {
            player.sendMessage("§c✗ Du darfst diesen Befehl nicht nutzen!");
            return false;
        }

        // Cooldown prüfen
        if (!player.hasPermission("plugin.rtp.bypasscooldown")) {
            if (cooldowns.containsKey(player.getUniqueId())) {
                long remainingSeconds = (cooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
                if (remainingSeconds > 0) {
                    player.sendMessage("§c✗ Cooldown! " + remainingSeconds + "s verbleibend.");
                    return false;
                }
            }
        }

        // Hourly Limit prüfen
        UUID uuid = player.getUniqueId();
        long now = System.currentTimeMillis();
        long hourAgo = now - (60 * 60 * 1000);

        hourlyAttempts.entrySet().removeIf(e -> e.getValue() < hourAgo);

        if (hourlyAttempts.size() >= plugin.getConfig().getInt("rtp.antiabuse.max-per-hour", 10)) {
            player.sendMessage("§c✗ Du hast dein stündliches Limit erreicht!");
            return false;
        }

        hourlyAttempts.put(uuid, now);
        return true;
    }

    private boolean deductCost(Player player) {
        if (!plugin.getConfig().getBoolean("rtp.cost.enabled", true)) {
            return true;
        }

        if (player.hasPermission("plugin.rtp.bypasscost")) {
            return true;
        }

        double cost = plugin.getConfig().getDouble("rtp.cost.amount", 100.0);
        if (plugin.getEconomy() == null) {
            return true;
        }

        if (plugin.getEconomy().has(player, cost)) {
            plugin.getEconomy().withdrawPlayer(player, cost);
            return true;
        }

        return false;
    }

    private void setCooldown(Player player) {
        if (player.hasPermission("plugin.rtp.bypasscooldown")) {
            return;
        }

        long cooldownSeconds = plugin.getConfig().getLong("rtp.cooldown.base-seconds", 30);
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (cooldownSeconds * 1000));
    }

    private boolean isWorldBlacklisted(String worldName) {
        return plugin.getConfig().getStringList("rtp.blacklist-worlds").contains(worldName);
    }

    private boolean getConfigBoolean(String path) {
        return plugin.getConfig().getBoolean(path, false);
    }

    private double getConfigDouble(String path) {
        return plugin.getConfig().getDouble(path, 0.0);
    }

    public void cancelWarmup(Player player) {
        BukkitTask task = warmupTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
            player.sendMessage("§c✗ Teleportation abgebrochen!");
        }
    }
}
