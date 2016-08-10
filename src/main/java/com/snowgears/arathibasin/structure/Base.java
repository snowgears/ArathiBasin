package com.snowgears.arathibasin.structure;

import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Base extends Structure{

    private int scanTaskID;

    public Base(String name, String world){
        super(name, world);
    }

    @Override
    public void setColor(DyeColor color){
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
