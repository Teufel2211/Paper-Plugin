package de.paperserver.plugin.commands;

import de.paperserver.plugin.managers.CrateManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CrateCommandCompleter implements TabCompleter {

    private final CrateManager crateManager;

    public CrateCommandCompleter(CrateManager crateManager) {
        this.crateManager = crateManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            // First argument: subcommands
            suggestions.add("open");
            suggestions.add("give");
            suggestions.add("list");
            return suggestions;
        }

        if (args.length == 2) {
            String subcommand = args[0].toLowerCase();
            if (subcommand.equals("open")) {
                // Second argument: crate names
                for (CrateManager.CrateType type : crateManager.getAllCrateTypes()) {
                    suggestions.add(type.name);
                }
            } else if (subcommand.equals("give")) {
                // Second argument: player names
                for (Player player : Bukkit.getOnlinePlayers()) {
                    suggestions.add(player.getName());
                }
            }
            return suggestions;
        }

        if (args.length == 3) {
            String subcommand = args[0].toLowerCase();
            if (subcommand.equals("give")) {
                // Third argument: crate names
                for (CrateManager.CrateType type : crateManager.getAllCrateTypes()) {
                    suggestions.add(type.name);
                }
            }
            return suggestions;
        }

        if (args.length == 4) {
            String subcommand = args[0].toLowerCase();
            if (subcommand.equals("give")) {
                // Fourth argument: amount suggestions
                suggestions.add("1");
                suggestions.add("5");
                suggestions.add("10");
            }
            return suggestions;
        }

        return suggestions;
    }
}
