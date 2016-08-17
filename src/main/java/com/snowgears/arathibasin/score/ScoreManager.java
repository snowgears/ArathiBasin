package com.snowgears.arathibasin.score;

import com.snowgears.arathibasin.ArathiBasin;
import com.snowgears.arathibasin.game.BattleTeam;
import com.snowgears.arathibasin.structure.Base;
import com.snowgears.arathibasin.structure.Structure;
import com.snowgears.arathibasin.util.TitleMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import java.util.*;

public class ScoreManager {

    private ArathiBasin plugin;

    private int redScore;
    private int blueScore;

    private boolean redWarning;
    private boolean blueWarning;

    private ScoreTick redTick;
    private ScoreTick blueTick;

    private HashMap<UUID, PlayerScore> playerScores;

    private int scoreTaskID;

    public ScoreManager(ArathiBasin instance){
        plugin = instance;
        playerScores = new HashMap<>();
        redTick = new ScoreTick(DyeColor.RED);
        blueTick = new ScoreTick(DyeColor.BLUE);
    }

    public void startScoreTask(){
        scoreTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(ArathiBasin.getPlugin(), new Runnable() {
            @Override
            public void run() {
                scoreTask();
            }
        }, 20L, 20L);
    }

    public void stopScoreTask(){
        Bukkit.getScheduler().cancelTask(scoreTaskID);
    }

    private void scoreTask(){
        int redBases = 0;
        int blueBases = 0;

        for(Structure s : ArathiBasin.getPlugin().getStructureManager().getStructures().values()){
            if(s instanceof Base){
                if(s.getColor() == DyeColor.RED){
                    redBases++;
                }
                else if(s.getColor() == DyeColor.BLUE){
                    blueBases++;
                }
            }
        }

        int redpoints = redTick.getPoints(redBases);
        this.redScore += redpoints;
        int bluepoints = blueTick.getPoints(blueBases);
        this.blueScore += bluepoints;

        if (!redWarning && redScore >= plugin.getScoreWarning()) {
            redWarning = true;
            String message = ChatColor.RED + ""+ plugin.getScoreWarning() + " - " +plugin.getRedTeamName();
            String warning = ChatColor.GRAY + "Score Warning";
            for (Player player : Bukkit.getWorld("world_arathi").getPlayers()) {
                TitleMessage.sendTitle(player, 20, 40, 20, message, warning);
            }
        }
        if (!blueWarning && blueScore >= plugin.getScoreWarning()) {
            blueWarning = true;
            String message = ChatColor.BLUE + ""+ plugin.getScoreWarning() + " - " +plugin.getBlueTeamName();
            String warning = ChatColor.GRAY + "Score Warning";
            for (Player player : Bukkit.getWorld("world_arathi").getPlayers()) {
                TitleMessage.sendTitle(player, 20, 40, 20, message, warning);
            }
        }

        boolean over = false;
        if(this.redScore >= plugin.getScoreWin()) {
            this.redScore = plugin.getScoreWin();
            over = true;
            String message = ChatColor.RED + plugin.getRedTeamName() + " Wins!";
            String finalScore = ChatColor.RED + ""+ this.redScore + "  " + ChatColor.BLUE + this.blueScore;
            for (Player player : Bukkit.getWorld("world_arathi").getPlayers()) {
                TitleMessage.sendTitle(player, 20, 200, 20, message, finalScore);
            }
        }
        if(this.blueScore >= plugin.getScoreWin()) {
            this.blueScore = plugin.getScoreWin();
            over = true;
            String message = ChatColor.BLUE + plugin.getBlueTeamName() + " Wins!";
            String finalScore = ChatColor.BLUE + ""+ this.blueScore + "  " + ChatColor.RED + this.redScore;
            for (Player player : Bukkit.getWorld("world_arathi").getPlayers()) {
                TitleMessage.sendTitle(player, 20, 200, 20, message, finalScore);
            }
        }

        if(redpoints > 0 || bluepoints > 0) {
            for (PlayerScore score : playerScores.values()) {
                score.update();
            }
        }

        if(over){
            ArathiBasin.getPlugin().getArathiGame().endGame(false);
        }
    }

    public PlayerScore addPlayerScore(Player player){
        if(playerScores.containsKey(player.getUniqueId()))
            return playerScores.get(player.getUniqueId());
        PlayerScore score = new PlayerScore(player);
        this.savePlayerScore(score);

        BattleTeam team = plugin.getArathiGame().getTeamManager().getCurrentTeam(player);
        if(team != null) {
            for (PlayerScore s : this.playerScores.values()) {
                s.addPlayerToTeam(player, team.getColor());
            }
        }

        return score;
    }

    public boolean removePlayerScore(Player player){
        if(playerScores.containsKey(player.getUniqueId())) {
            playerScores.remove(player.getUniqueId());
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

            BattleTeam team = plugin.getArathiGame().getTeamManager().getCurrentTeam(player);
            if(team != null) {
                for (PlayerScore s : this.playerScores.values()) {
                    s.removePlayerFromTeam(player, team.getColor());
                }
            }
            return true;
        }
        return false;
    }

    public PlayerScore getPlayerScore(Player player){
        if(playerScores.containsKey(player.getUniqueId()))
            return playerScores.get(player.getUniqueId());
        return null;
    }

    public void savePlayerScore(PlayerScore score){
        playerScores.put(score.getPlayerUUID(), score);
        score.update();
    }

    public void reset(){
        redScore = 0;
        blueScore = 0;
        redWarning = false;
        blueWarning = false;
        playerScores.clear();
    }

    public List<PlayerScore> getTopScores(){
        List<PlayerScore> scores = new ArrayList<PlayerScore>(playerScores.values());
        Collections.sort(scores, new PlayerScoreComparator());
        return scores;
    }

    public int getRedScore(){
        return redScore;
    }

    public void setRedScore(int redScore){
        this.redScore = redScore;
    }

    public int getBlueScore(){
        return blueScore;
    }

    public void setBlueScore(int blueScore){
        this.blueScore = blueScore;
    }
}
