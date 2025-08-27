package com.lol.plug.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import com.lol.plug.has.Game;

public class NewGame implements CommandExecutor {
    public static Game currentGame;
    public static Player lastSeeker;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (Bukkit.getOnlinePlayers().size() <= 1)  {
            sender.sendMessage(ChatColor.RED + "Cannot start game; not enough players!");
            return true;
        }

        currentGame = new Game();

        return true;
    }
}
