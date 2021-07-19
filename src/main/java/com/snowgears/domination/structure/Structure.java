package com.snowgears.domination.structure;

import com.snowgears.domination.Domination;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Structure {

    protected String name;
    protected String world;
    protected DyeColor color;
    protected BlockFace direction;
    protected HashMap<StructureModule, ArrayList<Location>> locations;
    protected StructureModule currentModule;

    public Structure(String name, String world){
        this.name = name;
        this.world = world;
        this.color = DyeColor.WHITE;
        locations = new HashMap<>();
        currentModule = null;
    }

    public String getName(){
        return name;
    }

    public World getWorld(){
        return Bukkit.getWorld(world);
    }

    public DyeColor getColor(){
        return color;
    }

    public void setColor(DyeColor color, List<Player> players){
        this.color = color;
    }

    public ArrayList<Location> getLocations(StructureModule module){
        if(locations.containsKey(module))
            return locations.get(module);
        return null;
    }

    protected List<Player> scan(StructureModule module, int distance){
        List<Player> players = new ArrayList<>();
        if(locations.containsKey(module)){
            ArrayList<Location> locs = locations.get(module);
            if(!locs.isEmpty()){
                Location scanPoint = locs.get(0);
                for(Entity e : scanPoint.getWorld().getNearbyEntities(scanPoint, distance, distance, distance)){
                    if(e instanceof Player)
                        players.add((Player)e);
                }
            }
        }
        return players;
    }

    public void setLocations(StructureModule module, ArrayList<Location> locs){
        locations.put(module, locs);
    }

    public boolean addLocation(Location loc){
        if(currentModule == null)
            return false;
        ArrayList<Location> locs;
        if(locations.containsKey(currentModule))
            locs = locations.get(currentModule);
        else
            locs = new ArrayList<>();

        if(locs.contains(loc))
            return false;
        locs.add(loc);
        locations.put(currentModule, locs);
        return true;
    }

    //return the module which the location was removed from
    public StructureModule removeLocation(Location loc){
        for(Map.Entry<StructureModule, ArrayList<Location>> entry : locations.entrySet()){
            ArrayList<Location> locs = entry.getValue();
            if(locs.contains(loc)){
                locs.remove(loc);
                locations.put(entry.getKey(), locs);
                return entry.getKey();
            }
        }
        return null;
    }

    public boolean remove(){
        File fileDirectory = new File(Domination.getPlugin().getDataFolder(), "Data");
        if (!fileDirectory.exists())
            return false;

        File worldDirectory = new File(fileDirectory, this.getWorld().getName());
        if (!worldDirectory.exists())
            return false;

        File structureFile = new File(worldDirectory, this.getName()+".yml");
        if (!structureFile.exists())
            return false;
        try {
            structureFile.delete();
        } catch(Exception e){
            return false;
        }
        return true;
    }

    public StructureModule getCurrentModule(){
        return currentModule;
    }

    public void setCurrentModule(StructureModule module){
        this.currentModule = module;
    }

    public BlockFace getDirection(){
        return direction;
    }

    public float getDirectionYaw(){
        return faceToYaw(direction);
    }

    public void setDirection(BlockFace direction) {
        this.direction = direction;
    }

    protected float faceToYaw(BlockFace bf) {
        switch(bf){
            case NORTH:
                return 180;
            case NORTH_EAST:
                return 225;
            case EAST:
                return 270;
            case SOUTH_EAST:
                return 315;
            case SOUTH:
                return 0;
            case SOUTH_WEST:
                return 45;
            case WEST:
                return 90;
            case NORTH_WEST:
                return 135;
        }
        return 180;
    }
}
