package com.snowgears.domination.game;


import com.snowgears.domination.Domination;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DominationStartTimer extends BukkitRunnable{

    private Domination plugin;
    private int timeTilGatesOpen;

    public DominationStartTimer(Domination instance){
        plugin = instance;
        timeTilGatesOpen = plugin.getStartWait();
        if(Domination.getPlugin().isDebug()) {
            System.out.println("[Domination] Initializing new game start timer.");
        }
    }
    @Override
    public void run(){
        if(timeTilGatesOpen < 0){
            plugin.getStructureManager().startStructureTasks();
            plugin.getDominationGame().getScoreManager().startScoreTask();
            this.cancel();
            return;
        }
        else{
            for(Player player : plugin.getDominationGame().getTeamManager().getAllPlayers()){
                if(player != null){
                    player.setLevel(timeTilGatesOpen);

                    if(timeTilGatesOpen == 0){
                        //TitleMessage.sendTitle(player, 20, 60, 20, ChatColor.GREEN+"GO!", ChatColor.GRAY+"Control bases to win!");
                        player.sendTitle(ChatColor.GREEN+"GO!", ChatColor.GRAY+"Control bases to win!", 20, 60, 20);
                    }
                    else if(timeTilGatesOpen < 11){
                        //TitleMessage.sendTitle(player, 5, 10, 5, ChatColor.GOLD+""+timeTilGatesOpen, null);
                        player.sendTitle(ChatColor.GOLD+""+timeTilGatesOpen, null, 5, 10, 5);
                    }
                }
            }
            timeTilGatesOpen--;
        }
    }
}
