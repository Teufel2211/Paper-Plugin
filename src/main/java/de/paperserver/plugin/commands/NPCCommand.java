package de.paperserver.plugin.commands;

import de.paperserver.plugin.managers.NPCManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NPCCommand implements CommandExecutor {

    private final NPCManager npcManager;

    public NPCCommand(NPCManager npcManager) {
        this.npcManager = npcManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cThis command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§c✗ Usage: /npc <create|remove|list> [args]");
            return true;
        }

        String subcommand = args[0].toLowerCase();

        switch (subcommand) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage("§c✗ Usage: /npc create <name>");
                    return true;
                }
                npcManager.createNPC(player, args[1], player.getWorld().getName());
                break;

            case "remove":
                if (args.length < 2) {
                    player.sendMessage("§c✗ Usage: /npc remove <id>");
                    return true;
                }
                try {
                    int id = Integer.parseInt(args[1]);
                    npcManager.removeNPC(player, id);
                } catch (NumberFormatException e) {
                    player.sendMessage("§c✗ Invalid NPC ID!");
                }
                break;

            case "list":
                player.sendMessage("§6=== NPCs ===");
                for (NPCManager.NPCData npc : npcManager.getAllNPCs()) {
                    player.sendMessage(String.format("§7%d: %s (%s)", npc.id, npc.name, npc.world));
                }
                break;

            default:
                player.sendMessage("§c✗ Unknown subcommand!");
                break;
        }

        return true;
    }
}
