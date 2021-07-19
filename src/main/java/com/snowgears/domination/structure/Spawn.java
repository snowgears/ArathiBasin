package com.snowgears.domination.structure;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;

public class Spawn extends Structure {

    public Spawn(String name, String world){
        super(name, world);
    }

    public void removeGates(){
        if(locations.containsKey(StructureModule.SPAWN_GATE)) {
            for (Location location : locations.get(StructureModule.SPAWN_GATE)) {
                location.getBlock().setType(Material.AIR);
                this.playChangeEffect(location, color);
            }
        }
    }

    public void resetGates(){
        if(locations.containsKey(StructureModule.SPAWN_GATE)) {
            for (Location location : locations.get(StructureModule.SPAWN_GATE)) {
                location.getBlock().setType(Material.IRON_BARS);
                this.playChangeEffect(location, color);
            }
        }
    }
}
