package de.paperserver.plugin.commands;

import de.paperserver.plugin.managers.HomeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {

    private final HomeManager homeManager;

    public HomeCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§6=== Your Homes ===");
            for (String home : homeManager.getHomes(player)) {
                player.sendMessage("§7- " + home);
            }
        } else {
            homeManager.teleportToHome(player, args[0]);
        }

        return true;
    }
}
