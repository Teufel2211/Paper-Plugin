package de.paperserver.plugin.listeners;

import de.paperserver.plugin.managers.RTPManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class RTPListener implements Listener {

    private final RTPManager rtpManager;

    public RTPListener(RTPManager rtpManager) {
        this.rtpManager = rtpManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Warmup abbrechen bei Bewegung
        rtpManager.cancelWarmup(event.getPlayer());
    }
}
