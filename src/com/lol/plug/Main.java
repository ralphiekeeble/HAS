package com.lol.plug;

import com.lol.plug.commands.Cancel;
import org.bukkit.*;
import com.lol.plug.events.Events;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import com.lol.plug.commands.NewGame;

public class Main extends JavaPlugin {
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Events(), this);

        World has_map = new WorldCreator("hasyan").createWorld();

        has_map.setDifficulty(Difficulty.PEACEFUL);

        this.getCommand("start").setExecutor(new NewGame());
        this.getCommand("cancel").setExecutor(new Cancel());

        getServer().getConsoleSender().sendMessage("plugin loaded lol");
    }

    public void onDisable() {
        getServer().getConsoleSender().sendMessage("plugin unloaded lol");
    }
}
