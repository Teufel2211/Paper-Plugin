package de.paperserver.plugin.commands;

import de.paperserver.plugin.managers.CrateManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CrateCommand implements CommandExecutor {

    private final CrateManager crateManager;

    public CrateCommand(CrateManager crateManager) {
        this.crateManager = crateManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§c✗ Usage: /crate <open|give|list> [args]");
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "open":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cThis command can only be used by players!");
                    return true;
                }
                Player player = (Player) sender;
                if (args.length < 2) {
                    player.sendMessage("§c✗ Usage: /crate open <crate_name>");
                    return true;
                }
                crateManager.openCrate(player, args[1]);
                break;

            case "give":
                if (!sender.hasPermission("plugin.crate.give")) {
                    sender.sendMessage("§c✗ You don't have permission to give crates!");
                    return true;
                }
                if (args.length < 4) {
                    sender.sendMessage("§c✗ Usage: /crate give <player> <crate_name> <amount>");
                    sender.sendMessage("§7Available Crates:");
                    for (CrateManager.CrateType type : crateManager.getAllCrateTypes()) {
                        sender.sendMessage("§7  - " + type.name);
                    }
                    return true;
                }
                Player target = sender.getServer().getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage("§c✗ Player not found!");
                    return true;
                }
                String crateName = args[2];
                try {
                    int amount = Integer.parseInt(args[3]);
                    crateManager.giveCrate((Player) sender, target, crateName, amount);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§c✗ Invalid amount! Must be a number.");
                }
                break;

            case "list":
                sender.sendMessage("§6=== Available Crates ===");
                if (crateManager.getAllCrateTypes().isEmpty()) {
                    sender.sendMessage("§c✗ No crates loaded!");
                } else {
                    for (CrateManager.CrateType type : crateManager.getAllCrateTypes()) {
                        sender.sendMessage("§7- " + type.displayName);
                    }
                }
                break;

            default:
                sender.sendMessage("§c✗ Unknown subcommand!");
                break;
        }

        return true;
    }
}
