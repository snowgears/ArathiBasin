package com.snowgears.domination.game;

import com.snowgears.domination.Domination;
import com.snowgears.domination.events.GameEndEvent;
import com.snowgears.domination.events.GameStartEvent;
import com.snowgears.domination.score.PlayerScore;
import com.snowgears.domination.score.ScoreManager;
import com.snowgears.domination.util.FileUtils;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class stores information about the current game.
 */
public class DominationGame {

    private boolean inProgress;
    private boolean isEnding;
    private TeamManager teamManager;
    private ScoreManager scoreManager;

    private DominationStartTimer startTimer;

    public DominationGame(){
        teamManager = new TeamManager();
        scoreManager = new ScoreManager(Domination.getPlugin());
    }

    public boolean startGame(){
        if(inProgress)
            return false;
        if(Domination.getPlugin().isDebug()) {
            System.out.println("[Domination] Starting game and resetting structures.");
        }
        Domination.getPlugin().getStructureManager().resetStructures(Domination.getPlugin().getWorldName());
        inProgress = true;
        isEnding = false;

        if(Domination.getPlugin().getRollbackWorld()){
            try {
                FileUtils.createWorldBackup();
            } catch (IOException e) { e.printStackTrace(); }
        }

        for (Player player : Bukkit.getWorld(Domination.getPlugin().getWorldName()).getPlayers()) {
            if(player != null)
                player.sendTitle("", "", 0,0,0);
        }

        startTimer = new DominationStartTimer(Domination.getPlugin());
        startTimer.runTaskTimer(Domination.getPlugin(), 0, 20); //run timer every second

        Domination.getPlugin().getDominationGame().getTeamManager().moveQueue();

        GameStartEvent gameStartEvent = new GameStartEvent();
        Bukkit.getPluginManager().callEvent(gameStartEvent);

        return true;
    }

    public boolean endGame(boolean forceEnd){
        if(!inProgress)
            return false;
        isEnding = true;
        Domination.getPlugin().getStructureManager().stopStructureTasks();
        scoreManager.stopScoreTask();
        if(startTimer != null)
            startTimer.cancel();

        GameEndEvent gameEndEvent = new GameEndEvent(forceEnd);
        Bukkit.getPluginManager().callEvent(gameEndEvent);

        printFinalScores();
        saveFinalScoresToFile();

        ArrayList<UUID> winners = new ArrayList<>();
        ArrayList<UUID> losers = new ArrayList<>();
        if(scoreManager.getBlueScore() > scoreManager.getRedScore()){
            for(Player player : teamManager.getTeam(DyeColor.BLUE).getPlayers()){
                winners.add(player.getUniqueId());
            }
            for(Player player : teamManager.getTeam(DyeColor.RED).getPlayers()){
                losers.add(player.getUniqueId());
            }
        }
        else{
            for(Player player : teamManager.getTeam(DyeColor.RED).getPlayers()){
                winners.add(player.getUniqueId());
            }
            for(Player player : teamManager.getTeam(DyeColor.BLUE).getPlayers()){
                losers.add(player.getUniqueId());
            }
        }

        //TODO remove this after event specific plugin
//        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "ctf reset");
//        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "nte reload");

        int delayTicks = (Domination.getPlugin().getEndWait() * 20);
        if(forceEnd)
            delayTicks = 10;

        Bukkit.getScheduler().scheduleSyncDelayedTask(Domination.getPlugin(), new Runnable() {
            @Override
            public void run() {
                Domination.getPlugin().getStructureManager().resetStructures(Domination.getPlugin().getWorldName());
                inProgress = false;
                isEnding = false;
                scoreManager.reset();
                teamManager.clear();

                for(UUID winner : winners){
                    Player player = Bukkit.getPlayer(winner);
                    if(player != null){
                        Domination.getPlugin().runWinnerCommands(player);
                    }
                }
                for(UUID loser : losers){
                    Player player = Bukkit.getPlayer(loser);
                    if(player != null){
                        Domination.getPlugin().runLoserCommands(player);
                    }
                }

                if(Domination.getPlugin().getRollbackWorld()){
                    try {
                        FileUtils.restoreWorldFromBackup();
                    } catch (IOException e) { e.printStackTrace(); }
                }
            }
        }, delayTicks);

        return true;
    }

    public boolean addPlayer(Player player, DyeColor team){

        //only do entry check if its cost is above 0
        if(Domination.getPlugin().getVaultEntryCost() > 0) {
            //first check that the player has enough money
            double balance = Domination.getPlugin().getEconomy().getBalance(player);
            if(balance < Domination.getPlugin().getVaultEntryCost()) {
                player.sendMessage(ChatColor.RED+"You do not have sufficient funds that meet the required entry fee ("+Domination.getPlugin().getVaultEntryCost()+")");
                return false;
            }

            EconomyResponse response = Domination.getPlugin().getEconomy().withdrawPlayer(player, Domination.getPlugin().getVaultEntryCost());
            if(!response.transactionSuccess()){
                player.sendMessage(ChatColor.RED+"Could not withdraw required entry fee ("+Domination.getPlugin().getVaultEntryCost()+")");
                return false;
            }
        }

        return teamManager.addPlayer(player, team);
    }

    public boolean removePlayer(Player player){
        return teamManager.removePlayer(player);
    }

    public boolean addSpectator(Player player){
        return teamManager.addSpectator(player);
    }

    public boolean removeSpectator(Player player){
        return teamManager.removeSpectator(player);
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
        List<PlayerScore> topScores = scoreManager.getOrderedPlayerScores();
        for(Player player : Bukkit.getWorld(Domination.getPlugin().getWorldName()).getPlayers()){
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
                player.sendMessage(ChatColor.GRAY + "To leave the game, type " + ChatColor.AQUA + "/"+Domination.getPlugin().getDominationCommand()+" leave");
            }
        }
    }

    private void saveFinalScoresToFile(){
        try {

            File fileDirectory = new File(Domination.getPlugin().getDataFolder(), "Data");
            if (!fileDirectory.exists())
                fileDirectory.mkdir();

            File currentFile = new File(fileDirectory + "/scores_"+System.currentTimeMillis()+".yml");

            if (!currentFile.exists()) // file doesn't exist
                currentFile.createNewFile();

            YamlConfiguration config = YamlConfiguration.loadConfiguration(currentFile);

            List<PlayerScore> topScores = scoreManager.getOrderedPlayerScores();
            if(topScores == null || topScores.isEmpty())
                return;

            int scoreNumber = 1;
            for (PlayerScore score : topScores) {
                config.set("scores." + scoreNumber + ".name", score.getPlayerName());
                config.set("scores." + scoreNumber + ".points", score.getPoints());
                config.set("scores." + scoreNumber + ".assaults", score.getAssaults());
                config.set("scores." + scoreNumber + ".captures", score.getCaptures());
                config.set("scores." + scoreNumber + ".defends", score.getDefends());
                config.set("scores." + scoreNumber + ".kills", score.getKills());
                config.set("scores." + scoreNumber + ".deaths", score.getDeaths());
                scoreNumber++;
            }
            config.save(currentFile);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void printScore(Player player, int num, PlayerScore score){
        if(player == null)
            return;
        if(score == null)
            return;
        Player scorePlayer = Bukkit.getPlayer(score.getPlayerUUID());
        if(scorePlayer == null)
            return;
        BattleTeam team = teamManager.getCurrentTeam(scorePlayer);
        ChatColor color = ChatColor.WHITE;
        if(team != null)
            color = ChatColor.valueOf(team.getColor().toString());

        player.sendMessage(""+num+"."+color+scorePlayer.getName()+" "
                +ChatColor.GOLD+score.getPoints()    +     "          "
                +ChatColor.RED+score.getAssaults()   +     "            "
                +ChatColor.AQUA+score.getCaptures()  +     "             "
                +ChatColor.LIGHT_PURPLE+score.getDefends()+"         "
                +ChatColor.GREEN+score.getKills()+"/"+score.getDeaths()
        );

    }
}
