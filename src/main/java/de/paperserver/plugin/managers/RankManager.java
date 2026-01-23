package de.paperserver.plugin.managers;

import de.paperserver.plugin.PaperPluginSuite;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class RankManager {

    private final PaperPluginSuite plugin;

    public RankManager(PaperPluginSuite plugin) {
        this.plugin = plugin;
    }

    /**
     * Get all permissions of a player
     */
    public Set<String> getPlayerPermissions(Player player) {
        Set<String> permissions = new HashSet<>();
        if (plugin.getLuckPerms() == null) {
            return permissions;
        }

        try {
            User user = plugin.getLuckPerms().getUserManager().getUser(player.getUniqueId());
            if (user != null) {
                user.getNodes().forEach(node -> {
                    permissions.add(node.getKey());
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return permissions;
    }

    /**
     * Give permission to a player
     */
    public boolean givePermission(Player admin, Player target, String permission) {
        if (!admin.hasPermission("plugin.ranks.admin")) {
            admin.sendMessage("§c✗ You don't have permission to manage ranks!");
            return false;
        }

        if (plugin.getLuckPerms() == null) {
            admin.sendMessage("§c✗ LuckPerms not available!");
            return false;
        }

        try {
            User user = plugin.getLuckPerms().getUserManager().getUser(target.getUniqueId());
            if (user != null) {
                Node node = Node.builder(permission).build();
                user.data().add(node);
                plugin.getLuckPerms().getUserManager().saveUser(user);
                
                admin.sendMessage("§a✓ Permission '" + permission + "' given to " + target.getName());
                target.sendMessage("§a✓ You received permission: " + permission);
                // Update player's permissions in-game
                plugin.getLuckPerms().getUserManager().loadUser(target.getUniqueId());
                return true;
            }
        } catch (Exception e) {
            admin.sendMessage("§c✗ Error giving permission: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Remove permission from a player
     */
    public boolean removePermission(Player admin, Player target, String permission) {
        if (!admin.hasPermission("plugin.ranks.admin")) {
            admin.sendMessage("§c✗ You don't have permission to manage ranks!");
            return false;
        }

        if (plugin.getLuckPerms() == null) {
            admin.sendMessage("§c✗ LuckPerms not available!");
            return false;
        }

        try {
            User user = plugin.getLuckPerms().getUserManager().getUser(target.getUniqueId());
            if (user != null) {
                user.data().remove(Node.builder(permission).build());
                plugin.getLuckPerms().getUserManager().saveUser(user);
                
                admin.sendMessage("§a✓ Permission '" + permission + "' removed from " + target.getName());
                target.sendMessage("§c✗ Permission removed: " + permission);
                // Update player's permissions in-game
                plugin.getLuckPerms().getUserManager().loadUser(target.getUniqueId());
                return true;
            }
        } catch (Exception e) {
            admin.sendMessage("§c✗ Error removing permission: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Create a new rank group (group = multiple permissions together)
     */
    public boolean createRank(Player admin, String rankName, List<String> permissions) {
        if (!admin.hasPermission("plugin.ranks.admin")) {
            admin.sendMessage("§c✗ You don't have permission to manage ranks!");
            return false;
        }

        if (plugin.getLuckPerms() == null) {
            admin.sendMessage("§c✗ LuckPerms not available!");
            return false;
        }

        try {
            var group = plugin.getLuckPerms().getGroupManager().createAndLoadGroup(rankName);
            for (String perm : permissions) {
                group.data().add(Node.builder(perm).build());
            }
            plugin.getLuckPerms().getGroupManager().saveGroup(group);
            admin.sendMessage("§a✓ Rank '" + rankName + "' created with " + permissions.size() + " permissions!");
            return true;
        } catch (Exception e) {
            admin.sendMessage("§c✗ Error creating rank: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get all permissions in a specific group/rank
     */
    public Set<String> getRankPermissions(String rankName) {
        Set<String> permissions = new HashSet<>();
        if (plugin.getLuckPerms() == null) {
            return permissions;
        }

        try {
            var group = plugin.getLuckPerms().getGroupManager().getGroup(rankName);
            if (group != null) {
                group.getNodes().forEach(node -> {
                    permissions.add(node.getKey());
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return permissions;
    }

    /**
     * Add a player to a rank group
     */
    public boolean addPlayerToRank(Player admin, Player target, String rankName) {
        if (!admin.hasPermission("plugin.ranks.admin")) {
            admin.sendMessage("§c✗ You don't have permission to manage ranks!");
            return false;
        }

        if (plugin.getLuckPerms() == null) {
            admin.sendMessage("§c✗ LuckPerms not available!");
            return false;
        }

        try {
            User user = plugin.getLuckPerms().getUserManager().getUser(target.getUniqueId());
            var group = plugin.getLuckPerms().getGroupManager().getGroup(rankName);
            
            if (user != null && group != null) {
                user.data().add(Node.builder("group." + rankName).build());
                plugin.getLuckPerms().getUserManager().saveUser(user);
                
                admin.sendMessage("§a✓ " + target.getName() + " added to rank '" + rankName + "'");
                target.sendMessage("§a✓ You are now in rank: " + rankName);
                plugin.getLuckPerms().getUserManager().loadUser(target.getUniqueId());
                return true;
            } else {
                admin.sendMessage("§c✗ Rank or player not found!");
            }
        } catch (Exception e) {
            admin.sendMessage("§c✗ Error adding player to rank: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
