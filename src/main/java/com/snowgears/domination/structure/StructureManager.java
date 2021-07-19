package com.snowgears.domination.structure;

import com.snowgears.domination.Domination;
import com.snowgears.domination.util.FileUtils;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This class deals with saving and loading to/from files.
 */
public class StructureManager {

    private Domination plugin;
    private HashMap<String, Structure> structures; //structure name, structure (includes all structure)
    private HashMap<UUID, String> selectedStructures; //player UUID, name of structure selected

    public StructureManager(Domination instance){
        plugin = instance;
        structures = new HashMap<>();
        selectedStructures = new HashMap<>();
        this.loadDefaultStructures();
        this.loadStructures();
    }

    public void addStructure(Structure structure){
        structures.put(structure.getName(), structure);
    }

    public void removeStructure(Structure structure){
        if(structures.containsKey(structure.getName())){
            structures.remove(structure.getName());
            structure.remove();
        }
    }

    public Structure getStructure(String name){
        if(structures.containsKey(name)){
            return structures.get(name);
        }
        return null;
    }

    public void startStructureTasks(){
        for(Structure structure : structures.values()){
            if(structure instanceof Base){
                Base base = (Base)structure;
                base.startScanTask();
            }
            else{
                Spawn spawn = (Spawn)structure;
                spawn.removeGates();
            }
        }
    }

    public void stopStructureTasks(){
        for(Structure structure : structures.values()){
            if(structure instanceof Base){
                Base base = (Base)structure;
                base.stopScanTask();
            }
        }
    }

    public Structure getStructure(StructureModule module, Location location){
        for(Structure structure : structures.values()){
            if(structure.getWorld().getName().equals(location.getWorld().getName())){
                for(Location loc : structure.getLocations(module)){
                    if(loc.equals(location))
                        return structure;
                }
            }
        }
        return null;
    }

    public Structure getSelectedStructure(Player player){
        if(selectedStructures.containsKey(player.getUniqueId())){
            String structureName = selectedStructures.get(player.getUniqueId());
            return getStructure(structureName);
        }
        return null;
    }

    public void selectStructure(Player player, String name){
        System.out.println("[Domination] Name of structure selecting: "+name);
        //save old structure if they already have one selected
        if(selectedStructures.containsKey(player.getUniqueId())){
            String structureName = selectedStructures.get(player.getUniqueId());
            Structure current = getStructure(structureName);
            if(current == null)
                return;
            System.out.println("[Domination] Already has a selection. Adding old structure: "+current.getName());
            addStructure(current);
        }
        selectedStructures.put(player.getUniqueId(), name);
    }

    public void deselectStructure(Player player){
        if(selectedStructures.containsKey(player.getUniqueId())){
            selectedStructures.remove(player.getUniqueId());
        }
    }

    public void resetStructures(String world){
        for(Structure structure : structures.values()){
            if(structure.getWorld().getName().equals(world)){
                if(structure instanceof Base)
                    structure.setColor(DyeColor.WHITE, null);
                else
                    ((Spawn)structure).resetGates();
            }
        }
    }

    public Spawn getSpawn(DyeColor spawnColor){
        for(Structure structure : structures.values()){
            if(structure instanceof Spawn) {
                if (structure.getColor() == spawnColor)
                    return (Spawn)structure;
            }
        }
        return null;
    }

    public void saveStructures() {
        try {
            File fileDirectory = new File(plugin.getDataFolder(), "Data");
            if (!fileDirectory.exists())
                fileDirectory.mkdir();

            for (Structure structure : this.structures.values()) {

                File worldDirectory = new File(fileDirectory, structure.getWorld().getName());
                if (!worldDirectory.exists())
                    worldDirectory.mkdir();

                File structureFile = new File(worldDirectory, structure.getName()+".yml");
                if (!structureFile.exists())
                    structureFile.createNewFile();

                YamlConfiguration config = YamlConfiguration.loadConfiguration(structureFile);

                if(structure instanceof Base) {
                    config.set("structure.type", "base");
                    config.set("structure.color", DyeColor.WHITE.toString());
                }
                else {
                    config.set("structure.type", "spawn");
                    config.set("structure.color", structure.getColor().toString());
                }
                config.set("structure.direction", structure.getDirection().toString());

                for(StructureModule module : StructureModule.values()) {
                    ArrayList<Location> locs = structure.getLocations(module);
                   if(locs != null && !locs.isEmpty()){
                       List<String> stringLocs = new ArrayList<>(locs.size());
                       for(Location loc : locs){
                           stringLocs.add(locationToString(loc));
                       }
                       config.set("structure.locations."+module.toString(), stringLocs);
                   }
                }
                config.save(structureFile);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadStructures(){
        try {
            File fileDirectory = new File(plugin.getDataFolder(), "Data");
            if (!fileDirectory.exists())
                fileDirectory.mkdir();

            for (File worldDirectory : fileDirectory.listFiles()) {
                if(worldDirectory.isDirectory()) {
                    for (File structureFile : worldDirectory.listFiles()) {
                        if (structureFile.getName().endsWith(".yml")) {
                            YamlConfiguration config = YamlConfiguration.loadConfiguration(structureFile);

                            String name = structureFile.getName().substring(0, structureFile.getName().length() - 4); //remove .yml
                            String world = worldDirectory.getName();

                            BlockFace direction;
                            try {
                                String dir = config.getString("structure.direction");
                                direction = BlockFace.valueOf(dir);
                            } catch(Exception e){
                                direction = BlockFace.NORTH;
                            }

                            Structure structure = null;
                            String type = config.getString("structure.type");
                            if (type.equalsIgnoreCase("base")) {
                                structure = new Base(name, world);
                            } else {
                                structure = new Spawn(name, world);

                            }

                            String color = config.getString("structure.color");
                            DyeColor c = null;
                            try {
                                c = DyeColor.valueOf(color);
                            } catch (Exception e) {
                                c = DyeColor.WHITE;
                            }
                            structure.setColor(c, null);

                            for (StructureModule module : StructureModule.values()) {
                                List<String> stringLocs = config.getStringList("structure.locations." + module.toString());
                                if (stringLocs != null) {
                                    ArrayList<Location> locs = new ArrayList<>(stringLocs.size());
                                    for (String stringLoc : stringLocs) {
                                        Location loc = locationFromString(stringLoc);
                                        locs.add(loc);
                                    }
                                    structure.setLocations(module, locs);
                                }
                            }
                            structure.setDirection(direction);
                            this.structures.put(structure.getName(), structure);
                        }
                    }
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadDefaultStructures(){
        try {
            File world_domination = new File(plugin.getServer().getWorldContainer(), plugin.getWorldName());
            if (!world_domination.exists())
                return;

            File fileDirectory = new File(plugin.getDataFolder(), "Data");
            if (!fileDirectory.exists())
                fileDirectory.mkdir();

            File dataFolder = new File(world_domination, "world_structure_data");
            File dest = new File(fileDirectory, plugin.getWorldName());
            if (!dest.exists())
                dest.mkdir();

            FileUtils.copyFolder(dataFolder, dest);
            FileUtils.deleteFileOrFolder(dataFolder.toPath());
        } catch (Exception e){
            //do nothing
        }
    }

    public HashMap<String, Structure> getStructures(){
        return structures;
    }

    private String locationToString(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    private Location locationFromString(String locString) {
        String[] parts = locString.split(",");
        //return new Location(plugin.getServer().getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
        return new Location(plugin.getServer().getWorld(plugin.getWorldName()), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
    }
}
