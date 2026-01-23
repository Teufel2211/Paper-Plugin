package de.paperserver.plugin.commands;

import de.paperserver.plugin.managers.TPAManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPAHereCommand implements CommandExecutor {

    private final TPAManager tpaManager;

    public TPAHereCommand(TPAManager tpaManager) {
        this.tpaManager = tpaManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§c✗ Usage: /tpahere <player>");
            return true;
        }

        Player target = player.getServer().getPlayer(args[0]);
        if (target == null) {
            player.sendMessage("§c✗ Player not found!");
            return true;
        }

        tpaManager.sendTPAHere(player, target);
        return true;
    }
}
