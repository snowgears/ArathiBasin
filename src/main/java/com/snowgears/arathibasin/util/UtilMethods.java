package com.snowgears.arathibasin.util;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class UtilMethods {


    public static DyeColor getColor(Block b) {
        if(b.getType() == Material.GLASS)
            return DyeColor.WHITE;
        switch (b.getType()){
            case RED_STAINED_GLASS:
            case RED_WOOL:
                return DyeColor.RED;
            case PINK_STAINED_GLASS:
            case PINK_WOOL:
                return DyeColor.PINK;
            case BLUE_STAINED_GLASS:
            case BLUE_WOOL:
                return DyeColor.BLUE;
            case LIGHT_BLUE_STAINED_GLASS:
            case LIGHT_BLUE_WOOL:
                return DyeColor.LIGHT_BLUE;
            default:
                return DyeColor.WHITE;
        }
    }

    public static Material getWoolMaterial(DyeColor color) {
        switch (color){
            case RED:
                return Material.RED_WOOL;
            case PINK:
                return Material.PINK_WOOL;
            case BLUE:
                return Material.BLUE_WOOL;
            case LIGHT_BLUE:
                return Material.LIGHT_BLUE_WOOL;
            default:
                return Material.WHITE_WOOL;
        }
    }

    public static Material getStainedGlassMaterial(DyeColor color) {
        switch (color){
            case RED:
                return Material.RED_STAINED_GLASS;
            case PINK:
                return Material.PINK_STAINED_GLASS;
            case BLUE:
                return Material.BLUE_STAINED_GLASS;
            case LIGHT_BLUE:
                return Material.LIGHT_BLUE_STAINED_GLASS;
            default:
                return Material.WHITE_STAINED_GLASS;
        }
    }

    public static BlockFace yawToFace(float yaw) {
        final BlockFace[] axis = {BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST};
        return axis[Math.round(yaw / 90f) & 0x3];
    }

    public static float faceToYaw(BlockFace bf) {
        switch (bf) {
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

    public static String capitalize(String line) {
        String[] spaces = line.split("\\s+");
        String capped = "";
        for (String s : spaces) {
            if (s.length() > 1)
                capped = capped + Character.toUpperCase(s.charAt(0)) + s.substring(1) + " ";
            else {
                capped = capped + s.toUpperCase() + " ";
            }
        }
        return capped.substring(0, capped.length() - 1);
    }

    public static String getCleanLocation(Location loc, boolean includeWorld) {
        String text = "";
        if (includeWorld)
            text = loc.getWorld().getName() + " - ";
        text = text + "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
        return text;
    }

    public static Location getLocation(String cleanLocation) {
        World world = null;

        if (cleanLocation.contains(" - ")) {
            int dashIndex = cleanLocation.indexOf(" - ");
            world = Bukkit.getWorld(cleanLocation.substring(0, dashIndex));
            cleanLocation = cleanLocation.substring(dashIndex + 1, cleanLocation.length());
        } else {
            world = Bukkit.getWorld("world");
        }
        cleanLocation = cleanLocation.replaceAll("[^\\d-]", " ");

        String[] sp = cleanLocation.split("\\s+");

        try {
            return new Location(world, Integer.valueOf(sp[1]), Integer.valueOf(sp[2]), Integer.valueOf(sp[3]));
        } catch (Exception e) {
            return null;
        }
    }

    public static Location pushLocationInDirection(Location location, BlockFace direction, double add) {
        switch (direction) {
            case NORTH:
                location = location.add(0, 0, -add);
            case EAST:
                location = location.add(add, 0, 0);
            case SOUTH:
                location = location.add(0, 0, add);
            case WEST:
                location = location.add(-add, 0, 0);
        }
        return location;
    }

    public static String getItemName(ItemStack is) {
        ItemMeta itemMeta = is.getItemMeta();

        if (itemMeta.getDisplayName() == null || itemMeta.getDisplayName().isEmpty())
            return capitalize(is.getType().name().replace("_", " ").toLowerCase());
        else
            return itemMeta.getDisplayName();
    }

    public static boolean basicLocationMatch(Location loc1, Location loc2) {
        return (loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ());
    }

    public static ChatColor getChatColorByCode(String colorCode) {
        switch (colorCode) {
            case "&b":
                return ChatColor.AQUA;
            case "&0":
                return ChatColor.BLACK;
            case "&9":
                return ChatColor.BLUE;
            case "&l":
                return ChatColor.BOLD;
            case "&3":
                return ChatColor.DARK_AQUA;
            case "&1":
                return ChatColor.DARK_BLUE;
            case "&8":
                return ChatColor.DARK_GRAY;
            case "&2":
                return ChatColor.DARK_GREEN;
            case "&5":
                return ChatColor.DARK_PURPLE;
            case "&4":
                return ChatColor.DARK_RED;
            case "&6":
                return ChatColor.GOLD;
            case "&7":
                return ChatColor.GRAY;
            case "&a":
                return ChatColor.GREEN;
            case "&o":
                return ChatColor.ITALIC;
            case "&d":
                return ChatColor.LIGHT_PURPLE;
            case "&k":
                return ChatColor.MAGIC;
            case "&c":
                return ChatColor.RED;
            case "&r":
                return ChatColor.RESET;
            case "&m":
                return ChatColor.STRIKETHROUGH;
            case "&n":
                return ChatColor.UNDERLINE;
            case "&f":
                return ChatColor.WHITE;
            case "&e":
                return ChatColor.YELLOW;
            default:
                return ChatColor.RESET;
        }
    }

    public static ChatColor getChatColor(String message) {
        if (message.startsWith("&") && message.length() > 1) {
            ChatColor cc = getChatColorByCode(message.substring(0, 2));
            if (cc != ChatColor.RESET)
                return cc;
        }
        return null;
    }
}