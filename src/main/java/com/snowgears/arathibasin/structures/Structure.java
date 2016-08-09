package com.snowgears.arathibasin.structures;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class Structure {

    protected String name;
    protected DyeColor color;
    protected HashMap<StructureModule, ArrayList<Location>> locations;

    public ArrayList<Location> getLocations(StructureModule module){
        return locations.get(module);
    }

    public void setColor(DyeColor color){
        //TODO play particle effect at all locations + THUNDER_2/3 sound effect + lightning effect at flag location
        for(Map.Entry<StructureModule, ArrayList<Location>> entry : locations.entrySet()){
            if(entry.getKey().toString().contains("BASE")){
                Iterator<Location> iterator = entry.getValue().iterator();
                if(entry.getKey().toString().contains("GLASS")){
                    if(iterator.hasNext()){
                        iterator.next().getBlock().setTypeIdAndData(Material.GLASS.getId(), color.getWoolData(), true);
                    }
                }
                else{
                    if(iterator.hasNext()){
                        iterator.next().getBlock().setTypeIdAndData(Material.WOOL.getId(), color.getWoolData(), true);
                    }
                }
            }
        }
    }
}
