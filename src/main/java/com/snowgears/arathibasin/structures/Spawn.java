package com.snowgears.arathibasin.structures;

import org.bukkit.Location;
import org.bukkit.Material;

public class Spawn extends Structure {

    public Spawn(){
        //TODO load locations from file if they exist
    }

    public void removeGates(){
        //TODO play sound effects and particle effects
        if(locations.containsKey(StructureModule.SPAWN_GATE)) {
            for (Location location : locations.get(StructureModule.SPAWN_GATE)) {
                location.getBlock().setType(Material.AIR);
            }
        }
    }
}
