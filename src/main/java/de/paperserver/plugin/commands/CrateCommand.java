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
            sender.sendMessage("§c✗ Verwendung: /crate <open|give|list> [args]");
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "open":
                if (!(sender instanceof Player)) {
                    sender.sendMessage("§cDieser Befehl kann nur von Spielern genutzt werden!");
                    return true;
                }
                Player player = (Player) sender;
                if (args.length < 2) {
                    player.sendMessage("§c✗ Verwendung: /crate open <crate_name>");
                    return true;
                }
                crateManager.openCrate(player, args[1]);
                break;

            case "give":
                if (!sender.hasPermission("plugin.crate.give")) {
                    sender.sendMessage("§c✗ Du darfst keine Crates geben!");
                    return true;
                }
                if (args.length < 4) {
                    sender.sendMessage("§c✗ Verwendung: /crate give <spieler> <crate> <anzahl>");
                    return true;
                }
                Player target = sender.getServer().getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage("§c✗ Spieler nicht gefunden!");
                    return true;
                }
                try {
                    int amount = Integer.parseInt(args[3]);
                    crateManager.giveCrate((Player) sender, target, args[2], amount);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§c✗ Ungültige Anzahl!");
                }
                break;

            case "list":
                System.out.println("§a[CRATE COMMAND DEBUG] /crate list called");
                System.out.println("§a[CRATE COMMAND DEBUG] Total crates available: " + crateManager.getAllCrateTypes().size());
                sender.sendMessage("§6=== Available Crates ===");
                if (crateManager.getAllCrateTypes().isEmpty()) {
                    sender.sendMessage("§c✗ No crates loaded!");
                    System.out.println("§c[CRATE COMMAND DEBUG] No crates found!");
                } else {
                    for (CrateManager.CrateType type : crateManager.getAllCrateTypes()) {
                        System.out.println("§a[CRATE COMMAND DEBUG] Listing crate: " + type.name + " -> " + type.displayName);
                        sender.sendMessage("§7- " + type.displayName);
                    }
                }
                break;

            default:
                sender.sendMessage("§c✗ Unbekannter Subbefehl!");
                break;
        }

        return true;
    }
}
