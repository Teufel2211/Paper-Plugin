package de.paperserver.plugin.commands;

import de.paperserver.plugin.managers.RankManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand implements CommandExecutor {

    private final RankManager rankManager;

    public RankCommand(RankManager rankManager) {
        this.rankManager = rankManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§c✗ Usage: /ranks <list|give|remove|create> [args]");
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "list":
                handleListPermissions(sender, args);
                break;

            case "give":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cThis command can only be used by players!");
                    return true;
                }
                handleGivePermission((Player) sender, args);
                break;

            case "remove":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cThis command can only be used by players!");
                    return true;
                }
                handleRemovePermission((Player) sender, args);
                break;

            case "create":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cThis command can only be used by players!");
                    return true;
                }
                handleCreateRank((Player) sender, args);
                break;

            default:
                sender.sendMessage("§c✗ Unknown subcommand!");
                break;
        }

        return true;
    }

    private void handleListPermissions(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§c✗ Usage: /ranks list <player_name>");
            return;
        }

        Player target = sender.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§c✗ Player not found!");
            return;
        }

        var permissions = rankManager.getPlayerPermissions(target);
        sender.sendMessage("§6=== Permissions of " + target.getName() + " ===");
        if (permissions.isEmpty()) {
            sender.sendMessage("§7- No permissions");
        } else {
            for (String perm : permissions) {
                sender.sendMessage("§7- " + perm);
            }
        }
    }

    private void handleGivePermission(Player admin, String[] args) {
        if (args.length < 3) {
            admin.sendMessage("§c✗ Usage: /ranks give <player_name> <permission>");
            admin.sendMessage("§7Example: /ranks give Spieler plugin.spawn.set");
            return;
        }

        Player target = admin.getServer().getPlayer(args[1]);
        if (target == null) {
            admin.sendMessage("§c✗ Player not found!");
            return;
        }

        String permission = args[2];
        rankManager.givePermission(admin, target, permission);
    }

    private void handleRemovePermission(Player admin, String[] args) {
        if (args.length < 3) {
            admin.sendMessage("§c✗ Usage: /ranks remove <player_name> <permission>");
            admin.sendMessage("§7Example: /ranks remove Spieler plugin.spawn.set");
            return;
        }

        Player target = admin.getServer().getPlayer(args[1]);
        if (target == null) {
            admin.sendMessage("§c✗ Player not found!");
            return;
        }

        String permission = args[2];
        rankManager.removePermission(admin, target, permission);
    }

    private void handleCreateRank(Player admin, String[] args) {
        if (args.length < 3) {
            admin.sendMessage("§c✗ Usage: /ranks create <rank_name> <permission1>,<permission2>,...");
            admin.sendMessage("§7Example: /ranks create vip plugin.shop.create,plugin.auction.create");
            return;
        }

        String rankName = args[1];
        String[] permissions = args[2].split(",");
        
        rankManager.createRank(admin, rankName, java.util.Arrays.asList(permissions));
    }
}
