package de.paperserver.plugin.commands;

import de.paperserver.plugin.managers.SpawnManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private final SpawnManager spawnManager;

    public SpawnCommand(SpawnManager spawnManager) {
        this.spawnManager = spawnManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cDieser Befehl kann nur von Spielern genutzt werden!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            spawnManager.teleportToSpawn(player);
        } else if (args[0].equalsIgnoreCase("set")) {
            if (!player.hasPermission("plugin.spawn.set")) {
                player.sendMessage("§c✗ Du darfst keinen Spawn setzen!");
                return true;
            }
            spawnManager.setSpawn(player, player.getLocation());
        } else {
            player.sendMessage("§c✗ Verwendung: /spawn [set]");
        }

        return true;
    }
}
