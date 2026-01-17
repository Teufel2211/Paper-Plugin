package de.paperserver.plugin.listeners;

import de.paperserver.plugin.managers.TPAManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class TPAListener implements Listener {

    private final TPAManager tpaManager;

    public TPAListener(TPAManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Ausstehende TPA-Anfragen bereinigen
    }
}
