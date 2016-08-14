package com.snowgears.arathibasin.score;

import com.snowgears.arathibasin.ArathiBasin;
import com.snowgears.arathibasin.game.BattleTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.UUID;

/**
 * Listener class for default actions that will be used on their respective scoreboards.
 *
 * <P>All actions are provided to {@link ScoreManager} for handling.
 */

public class PlayerScoreboardListener implements Listener {

    public ArathiBasin plugin = ArathiBasin.getPlugin();
    //this is to keep track of double shift clicks to toggle scoreboard
    private HashMap<UUID, Integer> timesSneaked = new HashMap<>();

    public PlayerScoreboardListener(ArathiBasin instance) {
        plugin = instance;
    }

    @EventHandler
    public void onCrouch(PlayerToggleSneakEvent event) {
        final Player player = event.getPlayer();
        PlayerScore score = plugin.getArathiGame().getScoreManager().getPlayerScore(player);
        if (score != null) {
            if (event.isSneaking()) {
                int times = 0;
                if (timesSneaked.containsKey(player.getUniqueId())) {
                    times = timesSneaked.get(player.getUniqueId());
                }
                times++;

                if (times == 2) {
                    //toggle show full score
                    score.setShowFullScore(!score.getShowFullScore());
                    if (timesSneaked.containsKey(player.getUniqueId())) {
                        timesSneaked.remove(player.getUniqueId());
                    }
                } else
                    timesSneaked.put(player.getUniqueId(), times);

                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        if (timesSneaked.containsKey(player.getUniqueId())) {
                            timesSneaked.remove(player.getUniqueId());
                        }
                    }
                }, 10L);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        PlayerScore score = plugin.getArathiGame().getScoreManager().getPlayerScore(player);
        if (score != null) {
            score.addDeaths(1);
        }
    }

    @EventHandler
    public void onKill(EntityDamageByEntityEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player)event.getEntity();
            if(event.getDamager() instanceof Player){
                Player damager = (Player)event.getDamager();
                PlayerScore score = plugin.getArathiGame().getScoreManager().getPlayerScore(damager);
                if(score != null){
                    if(player.getHealth() - event.getDamage() <= 0){
                        score.addKills(1);
                    }
                }
            }
        }
    }
}
