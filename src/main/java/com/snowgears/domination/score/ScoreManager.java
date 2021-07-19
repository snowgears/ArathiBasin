package com.snowgears.domination.score;

import com.snowgears.domination.Domination;
import com.snowgears.domination.game.BattleTeam;
import com.snowgears.domination.structure.Base;
import com.snowgears.domination.structure.Structure;
import com.snowgears.domination.util.TitleMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class ScoreManager {

    private Domination plugin;

    private int redScore;
    private int blueScore;
    private int redBases;
    private int blueBases;

    private boolean redWarning;
    private boolean blueWarning;
    private boolean timeWarning;

    private ScoreTick redTick;
    private ScoreTick blueTick;

    private HashMap<UUID, PlayerScore> playerScores;
    private List<PlayerScore> orderedPlayerScores;

    private int scoreTaskID;
    private long startTimeMillis;

    public ScoreManager(Domination instance){
        plugin = instance;
        playerScores = new HashMap<>();
        orderedPlayerScores = new ArrayList<>();
        redTick = new ScoreTick(DyeColor.RED);
        blueTick = new ScoreTick(DyeColor.BLUE);
    }

    public void startScoreTask(){
        scoreTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Domination.getPlugin(), new Runnable() {
            @Override
            public void run() {
                scoreTask();
            }
        }, 20L, 20L);
        this.startTimeMillis = System.currentTimeMillis();
    }

    public void stopScoreTask(){
        Bukkit.getScheduler().cancelTask(scoreTaskID);
    }

    private void scoreTask(){
        int redBases = 0;
        int blueBases = 0;

        for(Structure s : Domination.getPlugin().getStructureManager().getStructures().values()){
            if(s instanceof Base){
                if(s.getColor() == DyeColor.RED){
                    redBases++;
                }
                else if(s.getColor() == DyeColor.BLUE){
                    blueBases++;
                }
            }
        }

        this.redBases = redBases;
        this.blueBases = blueBases;

        int redpoints = redTick.getPoints(redBases);
        this.redScore += redpoints;
        int bluepoints = blueTick.getPoints(blueBases);
        this.blueScore += bluepoints;

        Collections.sort(orderedPlayerScores, new PlayerScoreComparator());

        if (!redWarning && redScore >= plugin.getScoreWarning()) {
            redWarning = true;
            String message = ChatColor.RED + ""+ plugin.getScoreWarning() + " - " +plugin.getRedTeamName();
            String warning = ChatColor.GRAY + "Score Warning";
            for (Player player : Bukkit.getWorld(plugin.getWorldName()).getPlayers()) {
                TitleMessage.sendTitle(player, 20, 40, 20, message, warning);
            }
        }
        if (!blueWarning && blueScore >= plugin.getScoreWarning()) {
            blueWarning = true;
            String message = ChatColor.BLUE + ""+ plugin.getScoreWarning() + " - " +plugin.getBlueTeamName();
            String warning = ChatColor.GRAY + "Score Warning";
            for (Player player : Bukkit.getWorld(plugin.getWorldName()).getPlayers()) {
                TitleMessage.sendTitle(player, 20, 40, 20, message, warning);
            }
        }

        boolean over = false;
        if(this.redScore >= plugin.getScoreWin()) {
            this.redScore = plugin.getScoreWin();
            over = true;
            String message = ChatColor.RED + plugin.getRedTeamName() + " Wins!";
            String finalScore = ChatColor.RED + ""+ this.redScore + "  " + ChatColor.BLUE + this.blueScore;
            for (Player player : Bukkit.getWorld(plugin.getWorldName()).getPlayers()) {
                TitleMessage.sendTitle(player, 20, 200, 20, message, finalScore);
            }
        }
        if(this.blueScore >= plugin.getScoreWin()) {
            this.blueScore = plugin.getScoreWin();
            over = true;
            String message = ChatColor.BLUE + plugin.getBlueTeamName() + " Wins!";
            String finalScore = ChatColor.BLUE + ""+ this.blueScore + "  " + ChatColor.RED + this.redScore;
            for (Player player : Bukkit.getWorld(plugin.getWorldName()).getPlayers()) {
                TitleMessage.sendTitle(player, 20, 200, 20, message, finalScore);
            }
        }

        if(redpoints > 0 || bluepoints > 0) {
            for (PlayerScore score : playerScores.values()) {
                score.update();
            }
        }

        //if the game has a max time limit
        if(plugin.getGameMaxTime() > 0){
            long elapsedTimeMillis = System.currentTimeMillis() - startTimeMillis;
            long elapsedMinutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTimeMillis);
            int remainingMinutes = plugin.getGameMaxTime() - (int)elapsedMinutes;

            if(!timeWarning && elapsedMinutes >= plugin.getGameTimeWarning()){
                timeWarning = true;
                String message = ChatColor.GRAY + ""+ remainingMinutes + " minutes remaining";
                String warning = ChatColor.GRAY + "Time Warning";
                for (Player player : Bukkit.getWorld(plugin.getWorldName()).getPlayers()) {
                    TitleMessage.sendTitle(player, 20, 40, 20, message, warning);
                }
            }

            if(elapsedMinutes >= plugin.getGameMaxTime()){
                //time limit has been reached, next point wins

                if(this.blueScore > this.redScore) {
                    over = true;
                    String message = ChatColor.BLUE + plugin.getBlueTeamName() + " Wins!";
                    String finalScore = ChatColor.BLUE + "" + this.blueScore + "  " + ChatColor.RED + this.redScore;
                    for (Player player : Bukkit.getWorld(plugin.getWorldName()).getPlayers()) {
                        TitleMessage.sendTitle(player, 20, 200, 20, message, finalScore);
                    }
                }
                else if(this.redScore > this.blueScore){
                    over = true;
                    String message = ChatColor.RED + plugin.getRedTeamName() + " Wins!";
                    String finalScore = ChatColor.RED + ""+ this.redScore + "  " + ChatColor.BLUE + this.blueScore;
                    for (Player player : Bukkit.getWorld(plugin.getWorldName()).getPlayers()) {
                        TitleMessage.sendTitle(player, 20, 200, 20, message, finalScore);
                    }
                }
                //if scores are tied, keep going. Next point wins from here
            }
        }


        if(over){
            Domination.getPlugin().getDominationGame().endGame(false);
        }
    }

    public PlayerScore addPlayerScore(Player player){
        if(playerScores.containsKey(player.getUniqueId()))
            return playerScores.get(player.getUniqueId());
        PlayerScore score = new PlayerScore(player);
        this.savePlayerScore(score);

        BattleTeam team = plugin.getDominationGame().getTeamManager().getCurrentTeam(player);
        if(team != null) {
            for (PlayerScore s : this.playerScores.values()) {
                s.addPlayerToTeam(player, team.getColor());
                if(!orderedPlayerScores.contains(score))
                    orderedPlayerScores.add(score);
            }
        }

        return score;
    }

    public boolean removePlayerScore(Player player){
        if(playerScores.containsKey(player.getUniqueId())) {
            playerScores.remove(player.getUniqueId());
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

            BattleTeam team = plugin.getDominationGame().getTeamManager().getCurrentTeam(player);
            if(team != null) {
                PlayerScore score = getPlayerScore(player);
                if(score == null)
                    return false;

                score.removePlayerFromTeam(player, team.getColor());
                if (orderedPlayerScores.contains(score))
                    orderedPlayerScores.remove(score);

            }
            return true;
        }
        return false;
    }

    public int getCurrentRanking(Player player){
        PlayerScore score = getPlayerScore(player);
        if(score == null)
            return -1;
        try {
            return orderedPlayerScores.indexOf(score)+1;
        } catch(IndexOutOfBoundsException e){
            return -1;
        }
    }

    public List<PlayerScore> getOrderedPlayerScores(){
        return this.orderedPlayerScores;
    }

    //note that rankIndex is +1 more than index. Getting rank 1 will search at index 0
    public PlayerScore getScoreAtRank(int rankIndex){
        try {
            return orderedPlayerScores.get(rankIndex - 1);
        } catch (IndexOutOfBoundsException e){
            return null;
        }
    }

    public PlayerScore getPlayerScore(Player player){
        if(playerScores.containsKey(player.getUniqueId()))
            return playerScores.get(player.getUniqueId());
        return null;
    }

    public void savePlayerScore(PlayerScore score){
        playerScores.put(score.getPlayerUUID(), score);
        if(!orderedPlayerScores.contains(score))
            orderedPlayerScores.add(score);
        score.update();
    }

    public void reset(){
        redScore = 0;
        blueScore = 0;
        redBases = 0;
        blueBases = 0;
        redWarning = false;
        blueWarning = false;
        playerScores.clear();
        orderedPlayerScores.clear();
        this.startTimeMillis = 0;
    }

//    public List<PlayerScore> getTopScores(){
//        List<PlayerScore> scores = new ArrayList<PlayerScore>(playerScores.values());
//        Iterator<PlayerScore> itr = scores.iterator();
//        PlayerScore score;
//        while (itr.hasNext()) {
//            score = itr.next();
//            if(score.isSpectator()) //only include playerscores who are actually on a team
//                itr.remove();
//        }
//        Collections.sort(scores, new PlayerScoreComparator());
//        return scores;
//    }

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

    public int getRedBases(){
        return redBases;
    }

    public int getBlueBases(){
        return blueBases;
    }
}
