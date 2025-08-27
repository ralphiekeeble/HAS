// stop player punching bug


package com.lol.plug.has;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import com.lol.plug.commands.NewGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.lol.plug.ScoreboardMan;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import com.lol.plug.events.Events;

public class Game {
    private static final int seekers = 1;
    private static final int hidingTime = 45;
    private static final int gameLength = 300; // in seconds, original 240
    private BukkitTask timer;
    private HashMap<Player, Boolean> playing = new HashMap<Player, Boolean>();
    public int phase = 0;

    private void updateScoreboards(int timeLeft) {
        int hidersLeft = Collections.frequency(playing.values(), false);
        int seekersLeft = Collections.frequency(playing.values(), true);
        for (Player p : playing.keySet()) {
            Scoreboard score;

            if (playing.get(p)) {
                score = ScoreboardMan.personalScoreboard("SEEKER");
            } else {
                score = ScoreboardMan.personalScoreboard("HIDER");
            }

            Objective obj = score.getObjective("IGS");
            Score score1 = obj.getScore(ChatColor.RED + "Seekers left: " + ChatColor.YELLOW + seekersLeft);
            Score score2 = obj.getScore(ChatColor.BLUE + "Hiders left: " + ChatColor.YELLOW + hidersLeft);
            Score score3 = obj.getScore(" ");

            Score score4;

            if (phase == 1) {
                score4 = obj.getScore("Seeker will be released in: " + ChatColor.YELLOW + timeLeft);
            } else {
                score4 = obj.getScore("Time left: " + ChatColor.YELLOW + timeLeft);
            }

            score1.setScore(4);
            score2.setScore(3);
            score3.setScore(2);
            score4.setScore(1);

            p.setScoreboard(score);
        }
    }

    public ArrayList<Player> getHiders() {
        ArrayList<Player> hiders = new ArrayList<Player>();

        for (Player p : playing.keySet()) {
            if (!playing.get(p)) {
                hiders.add(p);
            }
        }

        return hiders;
    }

    public ArrayList<Player> getSeekers() {
        ArrayList<Player> seekers = new ArrayList<Player>();

        for (Player p : playing.keySet()) {
            if (playing.get(p)) {
                seekers.add(p);
            }
        }

        return seekers;
    }

    public ArrayList<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<Player>();

        for (Player p : playing.keySet()) {
            players.add(p);
        }

        return players;
    }

    public void setTeams(ArrayList<? extends Player> active ) {
        Random generator = new Random();
        generator.setSeed(System.currentTimeMillis());

        /*int newSeeker = (int) (( generator.nextInt(Bukkit.getOnlinePlayers().toArray().length)  * Math.random()) - 1);*/

        //

        ArrayList<Integer> abc = new ArrayList<>();

        for (int i = 0; i < seekers; i++) {
            int num = generator.nextInt(Bukkit.getOnlinePlayers().toArray().length);

            if (abc.contains(num)) {
                i--;
                continue;
            } else {
                abc.add(num);
            }
        }


        //int newSeeker = (int) (generator.nextInt(Bukkit.getOnlinePlayers().toArray().length) );

        for (int i = 0; i < active.size(); i++) {
            if (abc.contains(i)) {
/*                if (active.get(i).equals(NewGame.lastSeeker)) {
                    setTeams(active);
                    return;
                }*/
                playing.put(active.get(i), true);
            } else {
                playing.put(active.get(i), false);
            }
        }
    }

    public void addSeeker(Player p) {
        playing.replace(p, true);
        Bukkit.getServer().getConsoleSender().sendMessage(playing.toString());
    }

    public void endGame(boolean didWin, String winningTeam) {
        playing.forEach((player, aBoolean) -> {
            if (aBoolean != didWin) {
                player.sendMessage("Congratulations! " + winningTeam + ChatColor.RESET + " win!");
            } else {
                player.sendMessage("Game over! " + winningTeam + ChatColor.RESET + " win!");
            }

            player.playEffect(player.getLocation(), Effect.ENDERDRAGON_GROWL, 0);

            // create a ender dragon growl effect
        });

        cancelGame();
    }

    public void cancelGame() {
        try {
            timer.cancel();
        }
        catch(Exception e) {
            Bukkit.getServer().getConsoleSender().sendMessage(e.toString());
        }

        getPlayers().forEach(player -> {
           if (player.hasPotionEffect(PotionEffectType.BLINDNESS)) {
               player.removePotionEffect(PotionEffectType.BLINDNESS);
           }

           if (player.hasPotionEffect(PotionEffectType.GLOWING)) {
               player.removePotionEffect(PotionEffectType.GLOWING);
           }
           Events.getFound().removeAll();

           if (player.hasPotionEffect(PotionEffectType.SPEED)) {
               player.removePotionEffect(PotionEffectType.SPEED);
           }
           player.setWalkSpeed(0.2f);
           player.getPlayer().teleport( new Location( Bukkit.getServer().getWorld("hasyan"), 165.576d, 201.0d, 193.441d ) );
        });

        ScoreboardMan.gameInProgress = false;
        Events.manager.idleScoreboard(Events.playerCount);
    }

    public Game() {

        ArrayList<Player> active = new ArrayList<Player>();

        for (Player p : Bukkit.getOnlinePlayers()) {
            active.add(p);
        }

        setTeams(active);

        timer = new BukkitRunnable() {
            int counter = 1; //6
            public void run() {
                counter--;
                if (phase == 0) {
                    if (counter != 0) {
                        Bukkit.getServer().getConsoleSender().sendMessage("countdown timer: " + counter);
                        Bukkit.getServer().getOnlinePlayers().forEach(p -> {
                            p.sendMessage(ChatColor.YELLOW + "The game starts in " + ChatColor.RED + counter + ChatColor.RESET + "...");
                        });
                    } else {
                        ScoreboardMan.gameInProgress = true;
                        Bukkit.getServer().getConsoleSender().sendMessage("countdown finished");
                        //Bukkit.getScheduler().cancelTask(timer.getTaskId());
                        counter = hidingTime;
                        phase++;
                        getPlayers().forEach(player -> {
                            player.sendMessage(ChatColor.YELLOW + "=========================================");
                            player.sendMessage(ChatColor.YELLOW + "             Hide and Seek");

                            if (getSeekers().contains(player)) {
                                NewGame.lastSeeker = player;
                                player.setWalkSpeed(0.0f);
                                player.addPotionEffect( new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 1) );
                                player.sendMessage(ChatColor.YELLOW + "  You are a " + ChatColor.RED + "" + ChatColor.BOLD + "SEEKER" + ChatColor.RESET+ "" + ChatColor.YELLOW + ". You will be");
                                player.sendMessage(ChatColor.YELLOW + "  released in 30 seconds. Find all the");
                                player.sendMessage(ChatColor.YELLOW + "  hiders before the time runs out.");
                                player.addPotionEffect( new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 4) );
                                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1));
                            } else {
                                player.sendMessage(ChatColor.YELLOW + "  You are a " + ChatColor.BLUE + "" + ChatColor.BOLD + "HIDER. " + ChatColor.RESET + ChatColor.YELLOW + "Explore the map and");
                                player.sendMessage(ChatColor.YELLOW + "  find somewhere to hide to avoid the");
                                player.sendMessage(ChatColor.YELLOW + "  seekers.");
                            }
                            player.sendMessage(ChatColor.YELLOW + "=========================================");

                            player.teleport(new Location(Bukkit.getWorld("hasyan"), 165.419d, 25.0d, 41.609d));

                        });
                    }
                } else if (phase == 1) {
                   if (counter != 0) {
                       updateScoreboards(counter);
                   } else {
                       counter = gameLength;
                       phase++;
                       getSeekers().get(0).removePotionEffect( PotionEffectType.BLINDNESS );
                       getSeekers().get(0).setWalkSpeed(0.2f);
                   }
                } else {
                    if (counter != 0) {
                        updateScoreboards(counter);
                    } else {
                        //Bukkit.getScheduler().cancelTask(timer.getTaskId());
                        endGame(true, ChatColor.BLUE + "" + ChatColor.BOLD + "HIDERS");
                    }
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("plug"), 0L,20L);
    }
}
