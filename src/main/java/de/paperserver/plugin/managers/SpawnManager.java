package de.paperserver.plugin.managers;

import de.paperserver.plugin.PaperPluginSuite;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.*;

public class SpawnManager {

    private final PaperPluginSuite plugin;
    private Location defaultSpawn;
    private final Map<String, Location> worldSpawns = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public SpawnManager(PaperPluginSuite plugin) {
        this.plugin = plugin;
        loadSpawns();
    }

    public boolean setSpawn(Player player, Location location) {
        if (!player.hasPermission("plugin.spawn.set")) {
            player.sendMessage("§c✗ Du darfst keinen Spawn setzen!");
            return false;
        }

        this.defaultSpawn = location;
        player.sendMessage("§a✓ Spawn gesetzt!");
        return true;
    }

    public boolean teleportToSpawn(Player player) {
        if (!player.hasPermission("plugin.spawn.use")) {
            player.sendMessage("§c✗ Du darfst diesen Befehl nicht nutzen!");
            return false;
        }

        // Cooldown prüfen
        if (!player.hasPermission("plugin.spawn.bypasscooldown")) {
            if (cooldowns.containsKey(player.getUniqueId())) {
                long remainingSeconds = (cooldowns.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
                if (remainingSeconds > 0) {
                    player.sendMessage("§c✗ Cooldown! " + remainingSeconds + "s verbleibend.");
                    return false;
                }
            }
        }

        Location spawn = getSpawnLocation(player.getWorld().getName());
        if (spawn == null) {
            player.sendMessage("§c✗ Spawn nicht gesetzt!");
            return false;
        }

        // Warmup
        if (plugin.getConfig().getBoolean("spawn.warmup.enabled", true)) {
            int warmupSeconds = plugin.getConfig().getInt("spawn.warmup.seconds", 3);
            player.sendMessage("§6⏳ Teleportation in " + warmupSeconds + " Sekunden...");
            
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    player.teleport(spawn);
                    player.sendMessage("§a✓ Du wurdest zum Spawn teleportiert!");
                    setCooldown(player);
                }
            }, warmupSeconds * 20L);
        } else {
            player.teleport(spawn);
            player.sendMessage("§a✓ Du wurdest zum Spawn teleportiert!");
            setCooldown(player);
        }

        return true;
    }

    public Location getSpawnLocation(String worldName) {
        Location spawn = worldSpawns.get(worldName);
        if (spawn != null) {
            return spawn;
        }
        return defaultSpawn;
    }

    private void setCooldown(Player player) {
        long cooldownSeconds = plugin.getConfig().getLong("spawn.cooldown.base-seconds", 30);
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (cooldownSeconds * 1000));
    }

    private void loadSpawns() {
        // Placeholder für Datenladen
    }

    public void saveSpawns() {
        // Placeholder für Datenspeicherung
    }
}
