package com.snowgears.domination.score;

import com.snowgears.domination.Domination;
import com.snowgears.domination.events.BaseAssaultEvent;
import com.snowgears.domination.events.BaseCaptureEvent;
import com.snowgears.domination.events.BaseDefendEvent;
import com.snowgears.domination.game.BattleTeam;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Listener class for default actions that will be used on their respective scoreboards.
 *
 * <P>All actions are provided to {@link ScoreManager} for handling.
 */

public class PlayerScoreboardListener implements Listener {

    public Domination plugin = Domination.getPlugin();
    //this is to keep track of double shift clicks to toggle scoreboard
    private HashMap<UUID, Integer> timesSneaked = new HashMap<>();

    public PlayerScoreboardListener(Domination instance) {
        plugin = instance;
    }

    @EventHandler
    public void onBaseAssault(BaseAssaultEvent event){
        BattleTeam team = plugin.getDominationGame().getTeamManager().getTeam(event.getTeamColor());
        String message = event.getNotificationColor() + event.getBase().getName() + " assaulted.";
        String subtitle = ChatColor.GRAY+"+1 Assault Point(s)";
        for(Player player : event.getBase().getWorld().getPlayers()) {
            if(containsPlayer(player, event.getPlayers())) {
                //TitleMessage.sendTitle(player, 20, 40, 20, message, subtitle);
                player.sendTitle(message, subtitle, 20, 40, 20);
                //increment assault score of player
                PlayerScore score = plugin.getDominationGame().getScoreManager().getPlayerScore(player);
                if(score == null)
                    score = new PlayerScore(player);
                score.addAssaults(1);
                plugin.getDominationGame().getScoreManager().savePlayerScore(score);
            }
            else{
                //TitleMessage.sendTitle(player, 20, 40, 20, message, null);
                player.sendTitle(message, null, 20,40,20);
            }
        }
    }

    @EventHandler
    public void onBaseCapture(BaseCaptureEvent event){
        BattleTeam team = plugin.getDominationGame().getTeamManager().getTeam(event.getTeamColor());
        String message = event.getNotificationColor() + event.getBase().getName() + " captured.";
        String subtitle = ChatColor.GRAY+"+1 Capture Point(s)";
        for(Player player : event.getBase().getWorld().getPlayers()) {
            if(containsPlayer(player, event.getPlayers())) {
                //TitleMessage.sendTitle(player, 20, 40, 20, message, subtitle);
                player.sendTitle(message, subtitle, 20,40,20);
                //increment capture score of player
                PlayerScore score = plugin.getDominationGame().getScoreManager().getPlayerScore(player);
                if(score == null)
                    score = new PlayerScore(player);
                score.addCaptures(1);
                plugin.getDominationGame().getScoreManager().savePlayerScore(score);
            }
            else{
                //TitleMessage.sendTitle(player, 20, 40, 20, message, null);
                player.sendTitle(message, null, 20,40,20);
            }
        }

    }

    @EventHandler
    public void onBaseDefend(BaseDefendEvent event){
        BattleTeam team = plugin.getDominationGame().getTeamManager().getTeam(event.getTeamColor());
        String message = event.getNotificationColor() + event.getBase().getName() + " defended.";
        String subtitle = ChatColor.GRAY+"+1 Defend Point(s)";
        for(Player player : event.getBase().getWorld().getPlayers()) {
            if(containsPlayer(player, event.getPlayers())) {
                //TitleMessage.sendTitle(player, 20, 40, 20, message, subtitle);
                player.sendTitle(message, subtitle, 20,40,20);
                //increment defend score of player
                PlayerScore score = plugin.getDominationGame().getScoreManager().getPlayerScore(player);
                if(score == null)
                    score = new PlayerScore(player);
                score.addDefends(1);
                plugin.getDominationGame().getScoreManager().savePlayerScore(score);
            }
            else{
                //TitleMessage.sendTitle(player, 20, 40, 20, message, null);
                player.sendTitle(message, null, 20,40,20);
            }
        }
    }

    private boolean containsPlayer(Player player, List<Player> players){
        for(Player p : players){
            if(p.getUniqueId().equals(player.getUniqueId()))
                return true;
        }
        return false;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        PlayerScore score = plugin.getDominationGame().getScoreManager().getPlayerScore(player);
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
                PlayerScore score = plugin.getDominationGame().getScoreManager().getPlayerScore(damager);
                if(score != null){
                    if(player.getHealth() - event.getFinalDamage() <= 0){
                        score.addKills(1);
                    }
                }
            }
            else if(event.getDamager() instanceof Arrow){
                Arrow damager = (Arrow)event.getDamager();
                if(damager.getShooter() instanceof Player) {
                    Player shooter = (Player)damager.getShooter();
                    PlayerScore score = plugin.getDominationGame().getScoreManager().getPlayerScore(shooter);
                    if (score != null) {
                        if (player.getHealth() - event.getFinalDamage() <= 0) {
                            score.addKills(1);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void toggleScoreboard(PlayerToggleSneakEvent event) {
        final Player player = event.getPlayer();
        PlayerScore score = plugin.getDominationGame().getScoreManager().getPlayerScore(player);
        if (score != null) {

            //dont expand scoreboard to show full stats (since there are none) if player is spectating
            if(score.isSpectator())
                return;

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
}
