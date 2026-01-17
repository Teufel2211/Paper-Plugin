package de.paperserver.plugin.commands;

import de.paperserver.plugin.managers.TPAManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPADenyCommand implements CommandExecutor {

    private final TPAManager tpaManager;

    public TPADenyCommand(TPAManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cDieser Befehl kann nur von Spielern genutzt werden!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§c✗ Verwendung: /tpdeny <spieler>");
            return true;
        }

        Player sender_player = player.getServer().getPlayer(args[0]);
        if (sender_player == null) {
            player.sendMessage("§c✗ Spieler nicht gefunden!");
            return true;
        }

        tpaManager.denyTPA(player, sender_player.getUniqueId());
        return true;
    }
}
