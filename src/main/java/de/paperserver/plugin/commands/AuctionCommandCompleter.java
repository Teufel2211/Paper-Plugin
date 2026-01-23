package de.paperserver.plugin.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AuctionCommandCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            // First argument: subcommands
            suggestions.add("create");
            suggestions.add("list");
            suggestions.add("buy");
            return suggestions;
        }

        if (args.length == 2) {
            String subcommand = args[0].toLowerCase();
            if (subcommand.equals("create")) {
                suggestions.add("100");
                suggestions.add("500");
                suggestions.add("1000");
            }
            return suggestions;
        }

        return suggestions;
    }
}
