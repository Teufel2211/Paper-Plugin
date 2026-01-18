package de.paperserver.plugin.managers;

import de.paperserver.plugin.PaperPluginSuite;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class MoneyScoreboardManager {

    private final PaperPluginSuite plugin;
    private static final String OBJECTIVE_NAME = "money";
    private static final String DISPLAY_NAME = "§6§lBalance";
    private static final String MONEY_ENTRY = "§6Geld"; // Fixed entry name

    public MoneyScoreboardManager(PaperPluginSuite plugin) {
        this.plugin = plugin;
    }

    /**
     * Create or update scoreboard for a player (reset and create fresh)
     */
    public void setupScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        
        // Remove old objective if it exists
        Objective oldObjective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (oldObjective != null) {
            oldObjective.unregister();
        }

        // Create fresh objective
        Objective objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, "dummy", DISPLAY_NAME);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        updateMoneyDisplay(player);
    }

    /**
     * Update the money display for a player
     * Uses a FIXED entry name so only ONE line appears and updates its value
     */
    public void updateMoneyDisplay(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);

        if (objective == null) {
            setupScoreboard(player);
            return;
        }

        if (plugin.getEconomy() == null) {
            return;
        }

        double balance = plugin.getEconomy().getBalance(player);

        // Use FIXED entry name - this prevents creating new entries
        // Always update the same "§6Geld" entry with new balance value
        objective.getScore(MONEY_ENTRY).setScore((int) balance);
    }

    /**
     * Remove scoreboard for a player (e.g., on leave)
     */
    public void removeScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (objective != null) {
            objective.unregister();
        }
    }

    /**
     * Update all players' scoreboards (call this after global economy transactions)
     */
    public void updateAllScoreboards() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateMoneyDisplay(player);
        }
    }
}
