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
        p.sendMessage("§aWelcome! Available commands:");
        p.sendMessage("§7/rtp [world] - Random teleport");
        p.sendMessage("§7/auction - Open Auction House (/auction create <price> with item in hand)");
        p.sendMessage("§7/shop - Open Shop list (/shop add <shopId> <price> to add item in hand)");
        p.sendMessage("§7/spawn - Teleport to spawn (/spawn set to set spawn)");
        p.sendMessage("§7/homes, /sethome, /delhome - Home management");
        p.sendMessage("§7/tpa, /tpahere, /tpaccept, /tpdeny - Teleport requests");
        p.sendMessage("§7/crate - Crate commands (/crate open <cratename>, /crate list)");
        p.sendMessage("§7/sell <price> - Sell item in your hand");
        p.sendMessage("§7/ranks - Rank and permission management (/ranks list <player>)");
        
        // Setup money scoreboard
        if (plugin.getMoneyScoreboardManager() != null) {
            plugin.getMoneyScoreboardManager().setupScoreboard(p);
        }
    }
}
