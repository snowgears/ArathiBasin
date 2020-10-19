package com.snowgears.arathibasin.game;


import com.snowgears.arathibasin.ArathiBasin;
import com.snowgears.arathibasin.util.TitleMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ArathiStartTimer extends BukkitRunnable{

    private ArathiBasin plugin;
    private int timeTilGatesOpen;

    public ArathiStartTimer(ArathiBasin instance){
        plugin = instance;
        timeTilGatesOpen = plugin.getStartWait();
        if(ArathiBasin.getPlugin().isDebug()) {
            System.out.println("[Arathi] Initializing new game start timer.");
        }
    }
    @Override
    public void run(){
        if(timeTilGatesOpen < 0){
            plugin.getStructureManager().startStructureTasks();
            plugin.getArathiGame().getScoreManager().startScoreTask();
            this.cancel();
            return;
        }
        else{
            for(Player player : plugin.getArathiGame().getTeamManager().getAllPlayers()){
                if(player != null){
                    player.setLevel(timeTilGatesOpen);

                    if(timeTilGatesOpen == 0){
                        TitleMessage.sendTitle(player, 20, 60, 20, ChatColor.GREEN+"GO!", ChatColor.GRAY+"Control bases to win!");
                    }
                    else if(timeTilGatesOpen < 11){
                        TitleMessage.sendTitle(player, 5, 10, 5, ChatColor.GOLD+""+timeTilGatesOpen, null);
                    }
                }
            }
            timeTilGatesOpen--;
        }
    }
}
