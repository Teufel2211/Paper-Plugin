package de.paperserver.plugin.commands;

import de.paperserver.plugin.managers.RTPManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RTPCommand implements CommandExecutor {

    private final RTPManager rtpManager;

    public RTPCommand(RTPManager rtpManager) {
        this.rtpManager = rtpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cDieser Befehl kann nur von Spielern genutzt werden!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            rtpManager.teleportRandomly(player, null);
        } else if (args.length == 1) {
            rtpManager.teleportRandomly(player, args[0]);
        } else {
            player.sendMessage("§c✗ Verwendung: /rtp [welt]");
        }

        return true;
    }
}
