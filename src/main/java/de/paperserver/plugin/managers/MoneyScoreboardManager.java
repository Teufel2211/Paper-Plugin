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

    public MoneyScoreboardManager(PaperPluginSuite plugin) {
        this.plugin = plugin;
    }

    /**
     * Create or update scoreboard for a player
     */
    public void setupScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        
        // Remove old objective if exists
        Objective oldObjective = scoreboard.getObjective(OBJECTIVE_NAME);
        if (oldObjective != null) {
            oldObjective.unregister();
        }

        // Create new objective
        Objective objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, "dummy", DISPLAY_NAME);
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        updateMoneyDisplay(player);
    }

    /**
     * Update the money display for a player
     */
    public void updateMoneyDisplay(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);

        if (objective == null) {
            setupScoreboard(player);
            objective = scoreboard.getObjective(OBJECTIVE_NAME);
        }

        if (plugin.getEconomy() == null) {
            return;
        }

        double balance = plugin.getEconomy().getBalance(player);
        long balanceLong = Math.round(balance);

        // Clear old entries
        objective.getScores().forEach(s -> s.getObjective().getScoreboard().resetScores(s.getEntry()));

        // Add player name and balance as separate lines
        objective.getScore("§7" + player.getName()).setScore(10);
        objective.getScore(formatBalance(balance)).setScore(9);
    }

    /**
     * Format balance string for display
     */
    private String formatBalance(double balance) {
        return "§6Geld: §e" + String.format("%.2f", balance);
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
