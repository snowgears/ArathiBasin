package com.snowgears.arathibasin.structure;

import com.snowgears.arathibasin.ArathiBasin;
import com.snowgears.arathibasin.events.BaseAssaultEvent;
import com.snowgears.arathibasin.events.BaseCaptureEvent;
import com.snowgears.arathibasin.events.BaseDefendEvent;
import com.snowgears.arathibasin.game.BattleTeam;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Base extends Structure{

    private DyeColor previousColor;
    private int scanTaskID;
    private int delayedCaptureTaskID;
    private boolean isContested;

    //color is current state of Base
    //RED - red team owns the base and it is generating resources for them
    //BLUE - blue team owns the base and it is generating resources for them
    //PINK - red team has captured the base but is in the one minute waiting period
    //LIGHT_BLUE - blue team has captured the base but it is in the one minute waiting period
    //WHITE - neutral

    //TRANSITIONS
    //-------------------------------------------
    //WHITE -> PINK - assault (RED) (instant)
    //PINK -> WHITE - assault (BLUE) (instant)
    //PINK -> RED - capture (RED) (after 1 minute waiting period)
    //PINK -> BLUE - defend (BLUE) (instant)

    public Base(String name, String world){
        super(name, world);
        previousColor = DyeColor.WHITE;
    }

    public void startScanTask(){
        //initialize scan task that calls scanTask() every 2 seconds
        scanTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(ArathiBasin.getPlugin(), new Runnable() {
            @Override
            public void run() {
                scanTask();
            }
        }, 40L, 40L);
    }

    public void stopScanTask(){
        Bukkit.getScheduler().cancelTask(scanTaskID);
    }

    //this method scans for all players near the BASE_FLAG and calls a color transition based on the team distribution
    public void scanTask(){
        List<Player> playersNearFlag = this.scan(StructureModule.BASE_FLAG, 5);

        List<Player> redTeam = new ArrayList<>();
        List<Player> blueTeam = new ArrayList<>();
        for(Player player : playersNearFlag){
            BattleTeam team = ArathiBasin.getPlugin().getArathiGame().getTeamManager().getCurrentTeam(player);
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

    //TODO think of some way to make this more efficient
    //so the base doesn't have to scan all floor blocks every 2 seconds even when uncontested
    //variable like isContested

    private void colorTransition(List<Player> players, DyeColor captureColor){
        //TODO this will call custom BaseEvents (if color transition is complete)
        //TODO implement this either instantly or with 1 minute delayed task to check if color is the same
        //TODO make sure colorTransition cancels all delayed tasks as well, in case base is switched back and forth between 1 minute

        //don't scan all floor blocks when base is uncontested
        if(isContested == false && captureColor == this.color)
            return;

        boolean transitionComplete = false;
        ArrayList<Location> floor = this.getLocations(StructureModule.BASE_GLASS_FLOOR);
        if(floor != null){
            ArrayList<Block> newFloor = new ArrayList<>();
            for(Location location : floor){
                //grab any blocks that are not the current color of the base
                Block block = location.getBlock();
                if(block.getData() != color.getData())
                    newFloor.add(block);
            }

            if(newFloor.isEmpty()){
                isContested = false;
            }
            else{
                isContested = true;

                if(floor.size() == newFloor.size())
                    transitionComplete = true;
            }

            if(transitionComplete){
                if((this.color == DyeColor.RED && (captureColor == DyeColor.RED || captureColor == DyeColor.PINK)) ||
                   (this.color == DyeColor.BLUE && (captureColor == DyeColor.BLUE || captureColor == DyeColor.LIGHT_BLUE))){
                    //only cancel if color is not variation of own team (RED = RED/PINK)
                    //cancel delayed capture task (if any), as base has changed
                    Bukkit.getScheduler().cancelTask(delayedCaptureTaskID);
                }

                if(this.color != captureColor) {
                    this.setColor(captureColor, players);
                }
            }
        }
    }

    @Override
    protected void setColor(DyeColor color, List<Player> players){
        this.previousColor = this.color;
        this.color = color;

        for(Map.Entry<StructureModule, ArrayList<Location>> entry : locations.entrySet()){
            Iterator<Location> iterator = entry.getValue().iterator();
            if(entry.getKey().toString().contains("GLASS")){
                while(iterator.hasNext()){
                    setBlock(iterator.next(), Material.STAINED_GLASS, color);
                }
            }
            else{
                while(iterator.hasNext()){
                    setBlock(iterator.next(), Material.WOOL, color);
                }
            }
        }
        if(locations.containsKey(StructureModule.BASE_FLAG)){
            for(Location flag : locations.get(StructureModule.BASE_FLAG)){
                flag.getWorld().strikeLightningEffect(flag);
            }
        }

        //set a delayed task for the transition of the base from secondary color to primary color
        setupDelayedCaptureTask(players);

        if((color == DyeColor.LIGHT_BLUE || color == DyeColor.PINK) && previousColor != color) {
            BaseAssaultEvent event = new BaseAssaultEvent(this, players);
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
        else if((color == DyeColor.RED && previousColor == DyeColor.LIGHT_BLUE) ||
                (color == DyeColor.BLUE && previousColor == DyeColor.PINK)){
            BaseDefendEvent event = new BaseDefendEvent(this, players);
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
        else if((color == DyeColor.RED || color == DyeColor.BLUE) && previousColor != color) {
            BaseCaptureEvent event = new BaseCaptureEvent(this, players);
            Bukkit.getServer().getPluginManager().callEvent(event);
        }
    }

    private void setupDelayedCaptureTask(final List<Player> players){

        delayedCaptureTaskID = Bukkit.getScheduler().scheduleSyncDelayedTask(ArathiBasin.getPlugin(), new Runnable() {
            @Override
            public void run() {
                if(color == DyeColor.PINK)
                    setColor(DyeColor.RED, players);
                else if(color == DyeColor.LIGHT_BLUE)
                    setColor(DyeColor.BLUE, players);
            }
        }, 1200L); //1 minute
    }

    private void setBlock(Location location, Material type, DyeColor color){
        if(type == Material.STAINED_GLASS)
            location.getBlock().setTypeIdAndData(Material.STAINED_GLASS.getId(), color.getData(), true);
        else
            location.getBlock().setTypeIdAndData(Material.WOOL.getId(), color.getWoolData(), true);

        switch(color){
            case RED:
                location.getWorld().playEffect(location, Effect.STEP_SOUND, Material.REDSTONE_BLOCK.getId());
                break;
            case BLUE:
                location.getWorld().playEffect(location, Effect.STEP_SOUND, Material.LAPIS_BLOCK.getId());
                break;
            case WHITE:
                location.getWorld().playEffect(location, Effect.STEP_SOUND, Material.WOOL.getId());
                break;
        }
    }
}
