package de.paperserver.plugin.listeners;

import de.paperserver.plugin.managers.CrateManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class CrateListener implements Listener {

    private final CrateManager crateManager;

    public CrateListener(CrateManager crateManager) {
        this.crateManager = crateManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Crate-Interaktion handhaben
    }
}
