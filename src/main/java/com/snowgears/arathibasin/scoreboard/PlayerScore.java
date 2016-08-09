package com.snowgears.arathibasin.scoreboard;

import com.snowgears.arathibasin.ArathiBasin;
import com.snowgears.arathibasin.BattleTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.UUID;

/**
 * Basic object used to store different scores and statistics of a single {@link Player}.
 *
 * <P>Various values to indicate current state of scoring.
 * <P>Each PlayerScore object contains a scoreboard for the associated Player.
 */

public class PlayerScore {

    private UUID playerUUID;
    private int kills;
    private int deaths;
    private int captures;
    private int breaks;
    private int points;

    private Scoreboard scoreboard;
    private Team scoreboardTeam;

    public PlayerScore(Player player){
        this.playerUUID = player.getUniqueId();

        BattleTeam team = ArathiBasin.getPlugin().getTeamManager().getCurrentTeam(player);
        setupScoreboard(team);
    }

    public UUID getPlayerUUID(){
        return playerUUID;
    }

    public int getKills(){
        return kills;
    }

    public void setKills(int kills){
        this.kills = kills;
    }

    public int getDeaths(){
        return deaths;
    }

    public void setDeaths(int deaths){
        this.deaths = deaths;
    }

    public int getCaptures(){
        return captures;
    }

    public void setCaptures(int captures){
        this.captures = captures;
    }

    public int getBreaks(){
        return breaks;
    }

    public void setBreaks(int breaks){
        this.breaks = breaks;
    }

    public int getPoints(){
        return points;
    }

    public void setPoints(int points){
        this.points = points;
    }

    public void update(){

        Player player = Bukkit.getPlayer(playerUUID);
        if(player == null)
            return;

        Objective objective = scoreboard.getObjective(DisplaySlot.SIDEBAR);

        Score redScore = objective.getScore(ChatColor.RED +""+  ChatColor.BOLD + ArathiBasin.getPlugin().getRedTeamName());
        redScore.setScore(14);

        Score redScoreNum = objective.getScore("    "+ChatColor.RED +""+  ChatColor.BOLD + ArathiBasin.getPlugin().getTeamManager().getRedTeam().getScore());
        redScoreNum.setScore(13);

        Score blueScore = objective.getScore(ChatColor.BLUE +""+ ChatColor.BOLD + ArathiBasin.getPlugin().getBlueTeamName());
        blueScore.setScore(12);

        Score blueScoreNum = objective.getScore("    "+ChatColor.BLUE +""+ ChatColor.BOLD + ArathiBasin.getPlugin().getTeamManager().getBlueTeam().getScore());
        blueScoreNum.setScore(11);

        Score placeHolder = objective.getScore("");
        placeHolder.setScore(10);

        Score stats = objective.getScore(ChatColor.BOLD + "  Your Stats:");
        stats.setScore(9);

        Score points = objective.getScore(ChatColor.GOLD+"Points:");
        points.setScore(8);

        Score pointsNum = objective.getScore("    "+ChatColor.GOLD+ ""+ this.points);
        pointsNum.setScore(7);

        Score captures = objective.getScore(ChatColor.AQUA + "Captures:");
        captures.setScore(6);

        Score capturesNum = objective.getScore("    "+ChatColor.AQUA + ""+ this.captures);
        capturesNum.setScore(5);

        Score breaks = objective.getScore(ChatColor.DARK_RED + "Breaks:");
        breaks.setScore(4);

        Score breaksNum = objective.getScore("    "+ChatColor.DARK_RED + "" +this.breaks);
        breaksNum.setScore(3);

        Score kills = objective.getScore(ChatColor.GREEN+ "K/D");
        kills.setScore(2);

        Score killsNum = objective.getScore("    "+ChatColor.GREEN + "" + this.kills + " / "+ this.deaths);
        killsNum.setScore(1);

        player.setScoreboard(scoreboard);
    }

    private void setupScoreboard(BattleTeam team){
        if(team == null)
            return;

        Player player = Bukkit.getPlayer(playerUUID);
        if(player == null)
            return;

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        scoreboardTeam = scoreboard.registerNewTeam(team.getName());
        scoreboardTeam.setPrefix(ChatColor.valueOf(team.getColor().toString())+"");
        scoreboardTeam.setDisplayName(team.getName());
        scoreboardTeam.setCanSeeFriendlyInvisibles(true);
        scoreboardTeam.setAllowFriendlyFire(false);

        Objective objective = scoreboard.registerNewObjective("main", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.BOLD+"Score");

        update();
    }
}
