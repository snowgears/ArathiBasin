package com.snowgears.arathibasin.structure;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Structure {

    protected String name;
    protected String world;
    protected DyeColor color;
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

    public void setColor(DyeColor color){
        this.color = color;
    }

    public ArrayList<Location> getLocations(StructureModule module){
        return locations.get(module);
    }

    public List<Player> scan(StructureModule module, int distance){
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
            locs = new ArrayList<Location>();

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

    public StructureModule getCurrentModule(){
        return currentModule;
    }

    public void setCurrentModule(StructureModule module){
        this.currentModule = module;
    }
}
