package com.snowgears.domination.structure;

import com.snowgears.domination.Domination;
import com.snowgears.domination.events.BaseAssaultEvent;
import com.snowgears.domination.events.BaseCaptureEvent;
import com.snowgears.domination.events.BaseDefendEvent;
import com.snowgears.domination.game.BattleTeam;
import com.snowgears.domination.util.UtilMethods;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Base extends Structure{

    private DyeColor previousColor;
    private int scanTaskID;
    private int delayedCaptureTaskID;
    private boolean isContested;

    public Base(String name, String world){
        super(name, world);
        previousColor = DyeColor.WHITE;
    }

    public void startScanTask(){
        //initialize scan task that calls scanTask() every 2 seconds
        scanTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Domination.getPlugin(), new Runnable() {
            @Override
            public void run() {
                scanTask();
            }
        }, 40L, Domination.getPlugin().getBaseAssaultInterval());
    }

    public void stopScanTask(){
        Bukkit.getScheduler().cancelTask(scanTaskID);
    }

    //this method scans for all players near the BASE_FLAG and calls a color transition based on the team distribution
    private void scanTask(){
        List<Player> playersNearFlag = this.scan(StructureModule.BASE_FLAG, 5);

        List<Player> redTeam = new ArrayList<>();
        List<Player> blueTeam = new ArrayList<>();
        for(Player player : playersNearFlag){
            BattleTeam team = Domination.getPlugin().getDominationGame().getTeamManager().getCurrentTeam(player);
            if(team != null) {
                if (team.getColor() == DyeColor.BLUE)
                    blueTeam.add(player);
                else
                    redTeam.add(player);
            }
        }

        switch (this.color){
            case WHITE:
                if(redTeam.size() > blueTeam.size()){
                    colorTransition(redTeam, DyeColor.PINK);
                }
                else if (blueTeam.size() > redTeam.size()){
                    colorTransition(blueTeam, DyeColor.LIGHT_BLUE);
                }
                else
                    colorTransition(redTeam, DyeColor.WHITE);
                break;
            case PINK:
                if (blueTeam.size() > redTeam.size()){
                    if(previousColor == DyeColor.BLUE)
                        colorTransition(blueTeam, DyeColor.BLUE);
                    else
                        colorTransition(blueTeam, DyeColor.WHITE);
                }
                else
                    colorTransition(redTeam, DyeColor.PINK);
                break;
            case LIGHT_BLUE:
                if (redTeam.size() > blueTeam.size()){
                    if(previousColor == DyeColor.RED)
                        colorTransition(redTeam, DyeColor.RED);
                    else
                        colorTransition(redTeam, DyeColor.WHITE);
                }
                else
                    colorTransition(blueTeam, DyeColor.LIGHT_BLUE);
                break;
            case RED:
                if (blueTeam.size() > redTeam.size()){
                    colorTransition(blueTeam, DyeColor.LIGHT_BLUE);
                }
                else
                    colorTransition(redTeam, DyeColor.RED);
                break;
            case BLUE:
                if(redTeam.size() > blueTeam.size()){
                    colorTransition(redTeam, DyeColor.PINK);
                }
                else
                    colorTransition(blueTeam, DyeColor.BLUE);
                break;
        }
    }

    private void colorTransition(List<Player> players, DyeColor captureColor){

        //don't scan all floor blocks when base is uncontested
        if(isContested == false && captureColor == this.color)
            return;

        boolean transitionComplete = false;
        ArrayList<Location> floor = this.getLocations(StructureModule.BASE_GLASS_FLOOR);
        if(floor != null){
            ArrayList<Block> floorToChange = new ArrayList<>();
            if(color == DyeColor.WHITE && (captureColor == DyeColor.PINK || captureColor == DyeColor.LIGHT_BLUE)) {
                //ensure that when red team and blue team are competing for a WHITE base, it regresses to WHITE
                for (Location location : floor) {
                    //grab any blocks that are not the capturing color of the base
                    Block block = location.getBlock();
                    DyeColor blockColor = UtilMethods.getColor(block);
                    if (blockColor != captureColor && blockColor != DyeColor.WHITE)
                        floorToChange.add(block);
                }
                if(!floorToChange.isEmpty())
                    captureColor = DyeColor.WHITE;
                else{
                    //continue as normal
                    for (Location location : floor) {
                        //grab any blocks that are not the capturing color of the base
                        Block block = location.getBlock();
                        DyeColor blockColor = UtilMethods.getColor(block);
                        if (blockColor != captureColor)
                            floorToChange.add(block);
                    }
                }
            }
            else{
                for (Location location : floor) {
                    //grab any blocks that are not the capturing color of the base
                    Block block = location.getBlock();
                    DyeColor blockColor = UtilMethods.getColor(block);
                    if (blockColor != captureColor)
                        floorToChange.add(block);
                }
            }

            //floor is totally full with new color
            if(floorToChange.isEmpty()) {
                transitionComplete = true;
                isContested = false;
            }
            else{
                isContested = true;
                ArrayList<Integer> randomIndex = new ArrayList<>();
                while(randomIndex.size() < 5) {
                    int i = ThreadLocalRandom.current().nextInt(0, floorToChange.size());
                    if(!randomIndex.contains(i))
                        randomIndex.add(i);
                }
                for(int i : randomIndex) {
                    setBlock(floorToChange.get(i), false, captureColor);
                }
                if(floorToChange.size() == 5) { //these 5 were just filled in above
                    transitionComplete = true;
                    isContested = false;
                }
            }
        }

        if(transitionComplete){
            if(this.color != captureColor) {
                this.setColor(captureColor, players);
            }
        }
    }

    @Override
    public void setColor(DyeColor color, List<Player> players){
        this.previousColor = this.color;
        this.color = color;

        if(Domination.getPlugin().getDominationGame().isInProgress()) {
            if (this.color != this.previousColor) {
                Bukkit.getScheduler().cancelTask(delayedCaptureTaskID);
            }
        }

        for(Map.Entry<StructureModule, ArrayList<Location>> entry : locations.entrySet()){
            Iterator<Location> iterator = entry.getValue().iterator();
            if(entry.getKey().toString().contains("GLASS")){
                while(iterator.hasNext()){
                    setBlock(iterator.next(), false, color);
                }
            }
            else{
                while(iterator.hasNext()){
                    setBlock(iterator.next(), true, color);
                }
            }
        }
        if(locations.containsKey(StructureModule.BASE_FLAG)){
            for(Location flag : locations.get(StructureModule.BASE_FLAG)){
                flag.getWorld().strikeLightningEffect(flag);
            }
        }

        if(Domination.getPlugin().getDominationGame().isInProgress()) {
            //set a delayed task for the transition of the base from secondary color to primary color
            if (color == DyeColor.PINK || color == DyeColor.LIGHT_BLUE) {
                setupDelayedCaptureTask(players);
                if (color != previousColor) {
                    if (color == DyeColor.PINK) {
                        BaseAssaultEvent event = new BaseAssaultEvent(this, DyeColor.RED, players);
                        Bukkit.getServer().getPluginManager().callEvent(event);
                    } else {
                        BaseAssaultEvent event = new BaseAssaultEvent(this, DyeColor.BLUE, players);
                        Bukkit.getServer().getPluginManager().callEvent(event);
                    }
                }
            }

            if (color == DyeColor.RED && previousColor == DyeColor.LIGHT_BLUE) {
                BaseDefendEvent event = new BaseDefendEvent(this, DyeColor.RED, players);
                Bukkit.getServer().getPluginManager().callEvent(event);
            } else if (color == DyeColor.BLUE && previousColor == DyeColor.PINK) {
                BaseDefendEvent event = new BaseDefendEvent(this, DyeColor.BLUE, players);
                Bukkit.getServer().getPluginManager().callEvent(event);
            } else if (color == DyeColor.RED && previousColor != color) {
                BaseCaptureEvent event = new BaseCaptureEvent(this, DyeColor.RED, players);
                Bukkit.getServer().getPluginManager().callEvent(event);
            } else if (color == DyeColor.BLUE && previousColor != color) {
                BaseCaptureEvent event = new BaseCaptureEvent(this, DyeColor.BLUE, players);
                Bukkit.getServer().getPluginManager().callEvent(event);
            }
        }
    }

    private void setupDelayedCaptureTask(final List<Player> players){
        delayedCaptureTaskID = Bukkit.getScheduler().scheduleSyncDelayedTask(Domination.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if(Domination.getPlugin().getDominationGame().isEnding())
                    return;
                if(color == DyeColor.PINK)
                    setColor(DyeColor.RED, players);
                else if(color == DyeColor.LIGHT_BLUE)
                    setColor(DyeColor.BLUE, players);
            }
        }, (Domination.getPlugin().getBaseCaptureInterval() * 20)); //1 minute
    }

    private void setBlock(Location location, boolean isWool, DyeColor color){
        if(isWool)
            location.getBlock().setType(UtilMethods.getWoolMaterial(color));
        else
            location.getBlock().setType(UtilMethods.getStainedGlassMaterial(color));

//        if(Domination.getPlugin().isDebug()) {
//            System.out.println("[Arathi] Setting block at location ("+location.getBlockX()+", "+location.getBlockY()+", "+location.getBlockZ()+") to color "+color.name());
//        }
        this.playChangeEffect(location, color);
    }

    private void setBlock(Block block, boolean isWool, DyeColor color){
        if(isWool)
            block.setType(UtilMethods.getWoolMaterial(color));
        else
            block.setType(UtilMethods.getStainedGlassMaterial(color));

//        if(Domination.getPlugin().isDebug()) {
//            System.out.println("[Arathi] Setting block at location ("+location.getBlockX()+", "+location.getBlockY()+", "+location.getBlockZ()+") to color "+color.name());
//        }
        this.playChangeEffect(block.getLocation(), color);
    }
}
