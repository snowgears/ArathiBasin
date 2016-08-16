package com.snowgears.arathibasin.game;

import com.snowgears.arathibasin.ArathiBasin;
import com.snowgears.arathibasin.score.PlayerScore;
import com.snowgears.arathibasin.score.ScoreManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * This class stores information about the current game.
 */
public class ArathiGame {

    private boolean inProgress;
    private boolean isEnding;
    private TeamManager teamManager;
    private ScoreManager scoreManager;

    public ArathiGame(){
        teamManager = new TeamManager();
        scoreManager = new ScoreManager(ArathiBasin.getPlugin());
    }

    public boolean startGame(){
        if(inProgress)
            return false;
        inProgress = true;
        isEnding = false;

        ArathiStartTimer timer = new ArathiStartTimer(ArathiBasin.getPlugin());
        timer.runTaskTimer(ArathiBasin.getPlugin(), 0, 20); //run timer every second

        return true;
    }

    public boolean endGame(boolean forceEnd){
        if(!inProgress)
            return false;
        isEnding = true;
        ArathiBasin.getPlugin().getStructureManager().stopStructureTasks();
        scoreManager.stopScoreTask();

        printFinalScores();

        int delayTicks = (ArathiBasin.getPlugin().getEndWait() * 20);
        if(forceEnd)
            delayTicks = 10;

        Bukkit.getScheduler().scheduleSyncDelayedTask(ArathiBasin.getPlugin(), new Runnable() {
            @Override
            public void run() {
                ArathiBasin.getPlugin().getStructureManager().resetStructures("world_arathi");
                inProgress = false;
                isEnding = false;
                scoreManager.reset();
                teamManager.clear();
            }
        }, delayTicks);

        return true;
    }

    public boolean addPlayer(Player player, DyeColor team){
        return teamManager.addPlayer(player, team);
    }

    public boolean removePlayer(Player player){
        return teamManager.removePlayer(player);
    }

    public TeamManager getTeamManager(){
        return teamManager;
    }

    public ScoreManager getScoreManager(){
        return scoreManager;
    }

    public boolean isInProgress(){
        return inProgress;
    }

    public boolean isEnding(){
        return isEnding;
    }

    private void printFinalScores(){
        List<PlayerScore> topScores = scoreManager.getTopScores();
        for(Player player : teamManager.getAllPlayers()){
            int scores = 1;
            if(player != null) {
                player.sendMessage(ChatColor.BOLD+"Top:       "+ChatColor.GOLD+"Points   "+ChatColor.RED+"Assaults   "+ChatColor.AQUA+"Captures   "+ChatColor.LIGHT_PURPLE+"Defends   "+ChatColor.GREEN+"K/D");

                int ownNum = 0;
                for(PlayerScore score : topScores){
                    if(scores < 6){
                        printScore(player, scores, score);
                    }
                    if(player.getUniqueId().equals(score.getPlayerUUID())){
                        ownNum = scores;
                    }
                    scores++;
                }
                //score was not already in the top 5
                if(ownNum > 5){
                    printScore(player, ownNum, scoreManager.getPlayerScore(player));
                }
                player.sendMessage(ChatColor.GRAY + "To leave the game, type " + ChatColor.AQUA + "/arathi leave");
            }
        }
    }

    private void printScore(Player player, int num, PlayerScore score){
        if(player == null)
            return;
        BattleTeam team = teamManager.getCurrentTeam(player);
        ChatColor color = ChatColor.WHITE;
        if(team != null)
            color = ChatColor.valueOf(team.getColor().toString());

        player.sendMessage(""+num+"."+color+player.getName()+" "
                +ChatColor.GOLD+score.getPoints()    +     "          "
                +ChatColor.RED+score.getAssaults()   +     "            "
                +ChatColor.AQUA+score.getCaptures()  +     "             "
                +ChatColor.LIGHT_PURPLE+score.getDefends()+"         "
                +ChatColor.GREEN+score.getKills()+"/"+score.getDeaths()
        );

    }
}
