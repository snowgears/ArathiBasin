package com.snowgears.arathibasin.structure;

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
                switch(color){
                    case RED:
                        location.getWorld().playEffect(location, Effect.STEP_SOUND, Material.REDSTONE_BLOCK.getId());
                        break;
                    case BLUE:
                        location.getWorld().playEffect(location, Effect.STEP_SOUND, Material.LAPIS_BLOCK.getId());
                        break;
                }
            }
        }
    }

    public void resetGates(){
        if(locations.containsKey(StructureModule.SPAWN_GATE)) {
            for (Location location : locations.get(StructureModule.SPAWN_GATE)) {
                location.getBlock().setType(Material.IRON_FENCE);
                switch(color){
                    case RED:
                        location.getWorld().playEffect(location, Effect.STEP_SOUND, Material.REDSTONE_BLOCK.getId());
                        break;
                    case BLUE:
                        location.getWorld().playEffect(location, Effect.STEP_SOUND, Material.LAPIS_BLOCK.getId());
                        break;
                }
            }
        }
    }
}
