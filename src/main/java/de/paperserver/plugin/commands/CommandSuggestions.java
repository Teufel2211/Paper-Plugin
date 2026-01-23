package de.paperserver.plugin.commands;

import de.paperserver.plugin.PaperPluginSuite;
import de.paperserver.plugin.managers.AuctionManager;
import de.paperserver.plugin.managers.ShopManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommandSuggestions implements TabCompleter {

    private final PaperPluginSuite plugin;

    public CommandSuggestions(PaperPluginSuite plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        String name = cmd.getName().toLowerCase();
        List<String> res = new ArrayList<>();

        switch (name) {
            case "rtp":
                if (args.length == 1) {
                    return Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList());
                }
                break;
            case "auction":
                if (args.length == 1) return List.of("create", "list", "buy");
                if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
                    return List.of("100", "500", "1000", "5000");
                }
                if (args.length == 2 && args[0].equalsIgnoreCase("buy")) {
                    AuctionManager am = plugin.getAuctionManager();
                    if (am != null) return am.getAllAuctions().stream().map(a -> String.valueOf(a.id)).collect(Collectors.toList());
                }
                break;
            case "shop":
                if (args.length == 1) return List.of("create", "list", "add", "open");
                if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("open"))) {
                    ShopManager sm = plugin.getShopManager();
                    if (sm != null) return sm.getAllShops().stream().map(s -> String.valueOf(s.id)).collect(Collectors.toList());
                }
                break;
            case "spawn":
                if (args.length == 1) return List.of("set");
                break;
            case "tpa":
            case "tpahere":
            case "tpaccept":
            case "tpdeny":
                if (args.length == 1) {
                    return Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).collect(Collectors.toList());
                }
                break;
            case "crate":
                if (args.length == 1) return List.of("open", "give", "list");
                if (args.length == 2 && args[0].equalsIgnoreCase("open")) return plugin.getCrateManager().getAllCrateTypes().stream().map(c -> c.name).collect(Collectors.toList());
                if (args.length == 2 && args[0].equalsIgnoreCase("give")) return Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).collect(Collectors.toList());
                if (args.length == 3 && args[0].equalsIgnoreCase("give")) return plugin.getCrateManager().getAllCrateTypes().stream().map(c -> c.name).collect(Collectors.toList());
                if (args.length == 4 && args[0].equalsIgnoreCase("give")) return List.of("1", "5", "10");
                break;
            case "ranks":
            case "rank":
            case "permission":
                if (args.length == 1) return List.of("list", "give", "remove", "create");
                if (args.length == 2 && (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("remove"))) {
                    return Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).collect(Collectors.toList());
                }
                if (args.length == 3 && (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("remove"))) {
                    return List.of("plugin.spawn.set", "plugin.home.set", "plugin.auction.create", "plugin.shop.create", "plugin.crate.use", "plugin.crate.give", "plugin.rtp.use", "plugin.tpa.use");
                }
                break;
            case "homes":
                if (sender instanceof Player) {
                    return plugin.getHomeManager().getHomes((Player) sender);
                }
                break;
        }

        return res;
    }
}
