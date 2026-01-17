package de.paperserver.plugin.listeners;

import de.paperserver.plugin.managers.HomeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class HomeListener implements Listener {

    private final HomeManager homeManager;

    public HomeListener(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Kann erweitert werden für Home-Funktionen
    }
}
