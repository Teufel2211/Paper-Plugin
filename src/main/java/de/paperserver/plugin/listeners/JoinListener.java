package de.paperserver.plugin.listeners;

import de.paperserver.plugin.PaperPluginSuite;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final PaperPluginSuite plugin;

    public JoinListener(PaperPluginSuite plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        var p = e.getPlayer();
        p.sendMessage("§aWillkommen! Verfügbare Befehle:");
        p.sendMessage("/rtp [welt] - Random teleport");
        p.sendMessage("/auction - Öffnet Auktionshaus (oder /auction create <preis> mit Item in der Hand)");
        p.sendMessage("/shop - Öffnet Shop-Liste (/shop add <shopId> <preis> um Item in Hand hinzuzufügen)");
        p.sendMessage("/spawn - Teleport zum Spawn (/spawn set um zu setzen)");
        p.sendMessage("/homes, /sethome, /delhome - Home Verwaltung");
        p.sendMessage("/tpa /tpahere /tpaccept /tpdeny - Teleport-Anfragen");
        p.sendMessage("/crate - Crate Befehle");
    }
}
