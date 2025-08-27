package com.lol.plug.events;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import com.lol.plug.ScoreboardMan;
import com.lol.plug.commands.NewGame;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class Events implements Listener {
    public static int playerCount = 0;
    public static ScoreboardMan manager = new ScoreboardMan();
    private static HashMap<Player, Boolean> boss = new HashMap<Player, Boolean>();
    private static BossBar found = Bukkit.createBossBar("ye", BarColor.RED, BarStyle.SOLID);

    public static BossBar getFound() {
        return found;
    }

    @EventHandler
    public static void playerMoveEvent(PlayerMoveEvent event) {
        if (ScoreboardMan.gameInProgress && NewGame.currentGame.getSeekers().contains(event.getPlayer()) && NewGame.currentGame.phase == 1) {
            event.setCancelled(true);
        } else if (ScoreboardMan.gameInProgress && NewGame.currentGame.getSeekers().contains(event.getPlayer()) && NewGame.currentGame.phase != 1) {
            for (Player p : NewGame.currentGame.getHiders()) {
                if (p.getLocation().distance(event.getPlayer().getLocation()) <= 40 && !boss.containsKey(event.getPlayer())) {
                    boss.put(event.getPlayer(), true);
                    found.addPlayer(event.getPlayer());
                    found.setTitle("You are near a player!");
                    found.setProgress(1);
                } else if (p.getLocation().distance(event.getPlayer().getLocation()) > 40 && boss.containsKey(event.getPlayer()) && boss.get(event.getPlayer()) == true) {
                    boss.remove(event.getPlayer());
                    found.removePlayer(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public static void playerHit(EntityDamageEvent event) {
        if ( event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public static void playerHit(EntityDamageByEntityEvent event) {
        if (!ScoreboardMan.gameInProgress) {
            event.setCancelled(true);
        } else {
            event.setDamage(0.0d);
        }

        if ( !(event.getDamager() instanceof Player) ) {
            event.setCancelled(true);
        }

        if (ScoreboardMan.gameInProgress &&  NewGame.currentGame != null && event.getDamager() instanceof Player && NewGame.currentGame.getSeekers().contains(((Player) event.getDamager()).getPlayer()) && !NewGame.currentGame.getSeekers().contains( (Player) event.getEntity() ) && NewGame.currentGame.phase != 1 && ScoreboardMan.gameInProgress) {
            //Bukkit.getServer().getConsoleSender().sendMessage("yo");
            Player victim = (Player) event.getEntity();

            Bukkit.getWorld("hasyan").spawnParticle(Particle.FIREWORKS_SPARK, victim.getLocation(), 1024);
            Bukkit.getWorld("hasyan").playEffect(victim.getLocation(), Effect.CLICK1, 0); // change sound to note tick
            victim.sendMessage("You are now a " + ChatColor.RED + "" + ChatColor.BOLD + "SEEKER" + ChatColor.RESET + "!");

            victim.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1));

            for (Player p : NewGame.currentGame.getPlayers()) {
                if (p != victim) {
                    p.sendMessage(ChatColor.RED + victim.getName() + ChatColor.RESET + " is now a " + ChatColor.RED + "" + ChatColor.BOLD + "SEEKER" + ChatColor.RESET + "!");
                }
            }

            victim.addPotionEffect( new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1) );
            NewGame.currentGame.addSeeker(victim);

            if ( NewGame.currentGame.getPlayers().size() == NewGame.currentGame.getSeekers().size()) {
                NewGame.currentGame.endGame( false, ChatColor.RED + "" + ChatColor.BOLD + "SEEKERS");
            }
        }
    }

    @EventHandler
    public static void playerLeave(PlayerQuitEvent player) {
        playerCount--;
        if (!ScoreboardMan.gameInProgress) {
            manager.idleScoreboard(playerCount);
        }
    }

    @EventHandler
    public static void playerJoin(PlayerJoinEvent player) {
        playerCount++;
        Player p = player.getPlayer();

        //player.getPlayer().teleport( new Location( Bukkit.getServer().getWorld("flatroom"), -43.153d, 51.0d, 168.451d ) );
        p.teleport( new Location( Bukkit.getServer().getWorld("hasyan"), 165.576d, 201.0d, 193.441d ) );
        p.setGameMode(GameMode.ADVENTURE);
        if (!ScoreboardMan.gameInProgress) {
            manager.idleScoreboard(playerCount);
        }
        if (p.hasPotionEffect(PotionEffectType.BLINDNESS)) {
            p.removePotionEffect(PotionEffectType.BLINDNESS);
        }
        p.setWalkSpeed(0.2f);
        p.getInventory().clear();
    }

    @EventHandler
    public static void inventoryChange(InventoryOpenEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public static void blockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }
}
