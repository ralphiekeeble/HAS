package com.lol.plug.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.lol.plug.ScoreboardMan;

public class Cancel implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!ScoreboardMan.gameInProgress) {
            sender.sendMessage(ChatColor.RED + "No game in progress!");
            return true;
        }

        NewGame.currentGame.cancelGame();

        return true;
    }
}
