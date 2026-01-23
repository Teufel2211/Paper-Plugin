package de.paperserver.plugin.commands;

import de.paperserver.plugin.managers.HomeManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HomeCommandCompleter implements TabCompleter {

    private final HomeManager homeManager;

    public HomeCommandCompleter(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (!(sender instanceof Player)) {
            return suggestions;
        }

        Player player = (Player) sender;

        if (args.length == 1) {
            // Show home names for this player
            return homeManager.getHomes(player);
        }

        return suggestions;
    }
}
