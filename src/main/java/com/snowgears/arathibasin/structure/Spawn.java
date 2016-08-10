package com.snowgears.arathibasin.structure;

import org.bukkit.Location;
import org.bukkit.Material;

public class Spawn extends Structure {

    public Spawn(String name, String world){
        super(name, world);

    }

    public void removeGates(){
        //TODO play sound effects and particle effects
        if(locations.containsKey(StructureModule.SPAWN_GATE)) {
            for (Location location : locations.get(StructureModule.SPAWN_GATE)) {
                location.getBlock().setType(Material.AIR);
            }
        }
    }

    public void resetGates(){
        //TODO play sound effects and particle effects
        if(locations.containsKey(StructureModule.SPAWN_GATE)) {
            for (Location location : locations.get(StructureModule.SPAWN_GATE)) {
                location.getBlock().setType(Material.AIR);
            }
        }
    }
}
