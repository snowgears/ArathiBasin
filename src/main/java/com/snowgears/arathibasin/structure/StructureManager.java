package com.snowgears.arathibasin.structure;

import com.snowgears.arathibasin.ArathiBasin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This class deals with saving and loading to/from files.
 */
public class StructureManager {

    private ArathiBasin plugin;
    private HashMap<String, Structure> structures; //structure name, structure (includes all structure)
    private HashMap<UUID, String> selectedStructures; //player UUID, name of structure selected

    public StructureManager(ArathiBasin instance){
        plugin = instance;
        structures = new HashMap<>();
        selectedStructures = new HashMap<>();
    }

    public void addStructure(Structure structure){
        structures.put(structure.getName(), structure);
    }

    public void removeStructure(Structure structure){
        if(structures.containsKey(structure.getName())){
            structures.remove(structure.getName());
        }
    }

    public Structure getStructure(String name){
        if(structures.containsKey(name)){
            return structures.get(name);
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
        System.out.println("[ArathiBasin] Name of structure selecting: "+name);
        //save old structure if they already have one selected
        if(selectedStructures.containsKey(player.getUniqueId())){
            String structureName = selectedStructures.get(player.getUniqueId());
            Structure current = getStructure(structureName);
            if(current == null)
                return;
            System.out.println("[ArathiBasin] Already has a selection. Adding old structure: "+current.getName());
            addStructure(current);
        }
        selectedStructures.put(player.getUniqueId(), name);
    }

    public void deselectStructure(Player player){
        if(selectedStructures.containsKey(player.getUniqueId())){
            selectedStructures.remove(player.getUniqueId());
        }
    }

    public void saveStructures(){

    }

    public List<Structure> loadStructures(){
        //TODO load all structure from file
        return null;
    }

    public HashMap<String, Structure> getStructures(){
        return structures;
    }
}
