package de.paperserver.plugin.commands;

import de.paperserver.plugin.managers.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RankCommandCompleter implements TabCompleter {

    private final RankManager rankManager;

    public RankCommandCompleter(RankManager rankManager) {
        this.rankManager = rankManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            // First argument: subcommands
            suggestions.add("list");
            suggestions.add("give");
            suggestions.add("remove");
            suggestions.add("create");
            return suggestions;
        }

        if (args.length == 2) {
            String subcommand = args[0].toLowerCase();
            if (subcommand.equals("list") || subcommand.equals("give") || subcommand.equals("remove")) {
                // Second argument: player names
                for (Player player : Bukkit.getOnlinePlayers()) {
                    suggestions.add(player.getName());
                }
            }
            return suggestions;
        }

        if (args.length == 3) {
            String subcommand = args[0].toLowerCase();
            if (subcommand.equals("give") || subcommand.equals("remove")) {
                // Third argument: common permissions
                suggestions.add("plugin.spawn.set");
                suggestions.add("plugin.home.set");
                suggestions.add("plugin.auction.create");
                suggestions.add("plugin.shop.create");
                suggestions.add("plugin.shop.add");
                suggestions.add("plugin.crate.use");
                suggestions.add("plugin.crate.give");
                suggestions.add("plugin.rtp.use");
                suggestions.add("plugin.tpa.use");
            }
            return suggestions;
        }

        return suggestions;
    }
}
