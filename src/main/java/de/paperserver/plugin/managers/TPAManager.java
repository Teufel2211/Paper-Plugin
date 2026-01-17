package de.paperserver.plugin.managers;

import de.paperserver.plugin.PaperPluginSuite;
import org.bukkit.entity.Player;
import java.util.*;

public class TPAManager {

    private final PaperPluginSuite plugin;
    private final Map<UUID, TPARequest> pendingRequests = new HashMap<>();
    private final Map<UUID, Set<UUID>> blockedPlayers = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public class TPARequest {
        public UUID sender;
        public UUID receiver;
        public boolean isTpaHere;
        public long timestamp;
        public long expiryTime;

        public TPARequest(UUID sender, UUID receiver, boolean isTpaHere) {
            this.sender = sender;
            this.receiver = receiver;
            this.isTpaHere = isTpaHere;
            this.timestamp = System.currentTimeMillis();
            this.expiryTime = timestamp + (30 * 1000); // 30 Sekunden Timeout
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    public TPAManager(PaperPluginSuite plugin) {
        this.plugin = plugin;
    }

    public boolean sendTPA(Player sender, Player receiver) {
        if (!sender.hasPermission("plugin.tpa.send")) {
            sender.sendMessage("§c✗ Du darfst diesen Befehl nicht nutzen!");
            return false;
        }

        if (isBlocked(receiver.getUniqueId(), sender.getUniqueId())) {
            sender.sendMessage("§c✗ " + receiver.getName() + " hat deine Anfragen blockiert!");
            return false;
        }

        if (cooldowns.containsKey(sender.getUniqueId())) {
            long remaining = (cooldowns.get(sender.getUniqueId()) - System.currentTimeMillis()) / 1000;
            if (remaining > 0) {
                sender.sendMessage("§c✗ Cooldown! " + remaining + "s verbleibend.");
                return false;
            }
        }

        TPARequest request = new TPARequest(sender.getUniqueId(), receiver.getUniqueId(), false);
        pendingRequests.put(sender.getUniqueId(), request);

        sender.sendMessage("§a✓ TPA-Anfrage an " + receiver.getName() + " gesendet!");
        receiver.sendMessage("§6» " + sender.getName() + " möchte sich zu dir teleportieren!");
        receiver.sendMessage("§7Schreibe /tpaccept " + sender.getName() + " oder /tpdeny " + sender.getName());

        setCooldown(sender);
        return true;
    }

    public boolean sendTPAHere(Player sender, Player receiver) {
        if (!sender.hasPermission("plugin.tpa.here")) {
            sender.sendMessage("§c✗ Du darfst diesen Befehl nicht nutzen!");
            return false;
        }

        if (isBlocked(receiver.getUniqueId(), sender.getUniqueId())) {
            sender.sendMessage("§c✗ " + receiver.getName() + " hat deine Anfragen blockiert!");
            return false;
        }

        TPARequest request = new TPARequest(sender.getUniqueId(), receiver.getUniqueId(), true);
        pendingRequests.put(sender.getUniqueId(), request);

        sender.sendMessage("§a✓ TPA-Anfrage an " + receiver.getName() + " gesendet!");
        receiver.sendMessage("§6» " + sender.getName() + " möchte, dass du dich zu ihm teleportierst!");
        receiver.sendMessage("§7Schreibe /tpaccept " + sender.getName() + " oder /tpdeny " + sender.getName());

        return true;
    }

    public boolean acceptTPA(Player receiver, UUID senderId) {
        if (!receiver.hasPermission("plugin.tpa.accept")) {
            receiver.sendMessage("§c✗ Du darfst diesen Befehl nicht nutzen!");
            return false;
        }

        TPARequest request = pendingRequests.get(senderId);
        if (request == null || !request.receiver.equals(receiver.getUniqueId())) {
            receiver.sendMessage("§c✗ Keine Anfrage gefunden!");
            return false;
        }

        if (request.isExpired()) {
            pendingRequests.remove(senderId);
            receiver.sendMessage("§c✗ Anfrage ist abgelaufen!");
            return false;
        }

        Player sender = plugin.getServer().getPlayer(senderId);
        if (sender == null) {
            receiver.sendMessage("§c✗ Spieler ist nicht online!");
            return false;
        }

        if (request.isTpaHere) {
            sender.teleport(receiver);
            sender.sendMessage("§a✓ Anfrage akzeptiert! Du wurdest teleportiert!");
        } else {
            receiver.teleport(sender);
            receiver.sendMessage("§a✓ Anfrage akzeptiert! Du wurdest teleportiert!");
        }

        pendingRequests.remove(senderId);
        return true;
    }

    public boolean denyTPA(Player receiver, UUID senderId) {
        if (!receiver.hasPermission("plugin.tpa.deny")) {
            receiver.sendMessage("§c✗ Du darfst diesen Befehl nicht nutzen!");
            return false;
        }

        if (pendingRequests.remove(senderId) != null) {
            receiver.sendMessage("§a✓ Anfrage abgelehnt!");
            Player sender = plugin.getServer().getPlayer(senderId);
            if (sender != null) {
                sender.sendMessage("§c✗ " + receiver.getName() + " hat deine Anfrage abgelehnt!");
            }
            return true;
        }

        receiver.sendMessage("§c✗ Keine Anfrage gefunden!");
        return false;
    }

    public void toggleBlock(Player player, UUID toBlock) {
        Set<UUID> blocked = blockedPlayers.computeIfAbsent(player.getUniqueId(), k -> new HashSet<>());
        if (blocked.contains(toBlock)) {
            blocked.remove(toBlock);
            player.sendMessage("§a✓ Spieler entsperrt!");
        } else {
            blocked.add(toBlock);
            player.sendMessage("§a✓ Spieler blockiert!");
        }
    }

    private boolean isBlocked(UUID blocker, UUID player) {
        Set<UUID> blocked = blockedPlayers.get(blocker);
        return blocked != null && blocked.contains(player);
    }

    private void setCooldown(Player player) {
        long cooldownSeconds = plugin.getConfig().getLong("tpa.cooldown.send-cooldown-seconds", 10);
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + (cooldownSeconds * 1000));
    }
}
