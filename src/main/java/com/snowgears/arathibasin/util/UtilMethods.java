package com.snowgears.arathibasin.util;

import org.bukkit.DyeColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;

public class UtilMethods {

    public static DyeColor getBlockColor(Block block){

        try {
            int length = 0;
            if (Tag.WOOL.isTagged(block.getType())){
                length = "_WOOL".length();
            }
            else if(block.getType().toString().contains("STAINED_GLASS")){
                length = "_STAINED_GLASS".length();
            }
            String colorString = block.getType().toString().substring(0, block.getType().toString().length()-length);
            DyeColor color = DyeColor.valueOf(colorString);
            return color;
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return null;
    }
}
