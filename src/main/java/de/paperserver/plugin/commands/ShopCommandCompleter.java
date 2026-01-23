package de.paperserver.plugin.commands;

import de.paperserver.plugin.managers.ShopManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ShopCommandCompleter implements TabCompleter {

    private final ShopManager shopManager;

    public ShopCommandCompleter(ShopManager shopManager) {
        this.shopManager = shopManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            // First argument: subcommands
            suggestions.add("create");
            suggestions.add("add");
            suggestions.add("remove");
            return suggestions;
        }

        if (args.length == 2) {
            String subcommand = args[0].toLowerCase();
            if (subcommand.equals("add") || subcommand.equals("remove")) {
                suggestions.add("1");
                suggestions.add("2");
                suggestions.add("3");
            }
            return suggestions;
        }

        if (args.length == 3) {
            String subcommand = args[0].toLowerCase();
            if (subcommand.equals("add")) {
                suggestions.add("100");
                suggestions.add("500");
                suggestions.add("1000");
            } else if (subcommand.equals("remove")) {
                suggestions.add("all");
            }
            return suggestions;
        }

        return suggestions;
    }
}
