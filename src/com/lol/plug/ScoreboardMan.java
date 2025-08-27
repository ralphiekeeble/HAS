package com.lol.plug;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardMan {
    public static boolean gameInProgress = false;
    private static Scoreboard current_board = Bukkit.getScoreboardManager().getNewScoreboard();

    public Scoreboard getScoreboard() {
        return current_board;
    }

    private static void updateScoreboard() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(current_board);
        }
    }

    private static void resetScoreboards() {
        for (Objective o : current_board.getObjectives()) {
            o.unregister();
        }
    }

    public static void idleScoreboard(int plrcount) {
        resetScoreboards();

        Objective obj = current_board.registerNewObjective("Idle Scoreboard", "V1", ChatColor.YELLOW + "Idling...");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        Score score = obj.getScore("Players online: " + ChatColor.RED + plrcount );
        score.setScore(1);

        Bukkit.getServer().getConsoleSender().sendMessage("players online " + Bukkit.getServer().getOnlinePlayers().toArray().length);

        updateScoreboard();
    }

    public static Scoreboard personalScoreboard(String Team) {
        resetScoreboards();

        String OutTeam = "";

        if (Team == "SEEKER") {
            OutTeam = ChatColor.RED + "" + ChatColor.BOLD + Team;
        } else if (Team == "HIDER") {
            OutTeam = ChatColor.BLUE + "" + ChatColor.BOLD + Team;
        } else if (Team == "SPECTATOR") {
            OutTeam = ChatColor.GRAY + "" + ChatColor.BOLD + Team;
        }

        Scoreboard score = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective obj = score.registerNewObjective("IGS", "V1", OutTeam);
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

/*        Score score1 = obj.getScore("ur mum");
        score1.setScore(1);*/

        return score;
    }
}
