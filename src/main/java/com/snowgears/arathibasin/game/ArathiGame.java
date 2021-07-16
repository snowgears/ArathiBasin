package com.snowgears.arathibasin.game;

import com.snowgears.arathibasin.ArathiBasin;
import com.snowgears.arathibasin.score.PlayerScore;
import com.snowgears.arathibasin.score.ScoreManager;
import com.snowgears.arathibasin.util.TitleMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

/**
 * This class stores information about the current game.
 */
public class ArathiGame {

    private boolean inProgress;
    private boolean isEnding;
    private TeamManager teamManager;
    private ScoreManager scoreManager;

    private ArathiStartTimer startTimer;

    public ArathiGame(){
        teamManager = new TeamManager();
        scoreManager = new ScoreManager(ArathiBasin.getPlugin());
    }

    public boolean startGame(){
        if(inProgress)
            return false;
        if(ArathiBasin.getPlugin().isDebug()) {
            System.out.println("[Arathi] Starting game and resetting structures.");
        }
        ArathiBasin.getPlugin().getStructureManager().resetStructures("world_arathi");
        inProgress = true;
        isEnding = false;

        for (Player player : Bukkit.getWorld("world_arathi").getPlayers()) {
            if(player != null)
                TitleMessage.clearTitle(player);
        }

        startTimer = new ArathiStartTimer(ArathiBasin.getPlugin());
        startTimer.runTaskTimer(ArathiBasin.getPlugin(), 0, 20); //run timer every second

        ArathiBasin.getPlugin().getArathiGame().getTeamManager().moveQueue();

        return true;
    }

    public boolean endGame(boolean forceEnd){
        if(!inProgress)
            return false;
        isEnding = true;
        ArathiBasin.getPlugin().getStructureManager().stopStructureTasks();
        scoreManager.stopScoreTask();
        if(startTimer != null)
            startTimer.cancel();

        printFinalScores();
        saveFinalScoresToFile();

        //TODO remove this after event specific plugin
//        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "ctf reset");
//        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "nte reload");

        Bukkit.getScheduler().scheduleSyncDelayedTask(ArathiBasin.getPlugin(), new Runnable() {
            @Override
            public void run() {
                String subMessage2 = ChatColor.LIGHT_PURPLE + "To download the minigame ->"+ChatColor.AQUA+" smarturl.it/arathi";
                for (Player player : Bukkit.getWorld("world_arathi").getPlayers()) {
                    if(player != null)
                        TitleMessage.sendTitle(player, 20, 250, 20, "", subMessage2);
                }
            }
        }, 100);

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
        for(Player player : Bukkit.getWorld("world_arathi").getPlayers()){
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

    private void saveFinalScoresToFile(){
        try {

            File fileDirectory = new File(ArathiBasin.getPlugin().getDataFolder(), "Data");
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
