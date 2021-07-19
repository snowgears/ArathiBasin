package com.snowgears.domination.util;

import com.snowgears.domination.Domination;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerData{

    private UUID playerUUID;
    private String oldDisplayName;
    private ItemStack[] oldInventoryContents;
    private ItemStack[] oldArmorContents;
    private Collection<PotionEffect> oldPotionEffects;
    private Location oldLocation;
    private GameMode oldGameMode;
    private double oldHealth;
    private double oldMaxHealth;
    private int oldHunger;
    private int oldExperience;
    private int oldLevel;
    private int oldRemainingAir;
    private int oldFireTicks;

    public PlayerData(Player player) {
        this.playerUUID = player.getUniqueId();
        this.oldDisplayName = player.getDisplayName();
        this.oldInventoryContents = player.getInventory().getContents();
        this.oldArmorContents = player.getInventory().getArmorContents();
        this.oldLocation = player.getLocation().clone();
        this.oldGameMode = player.getGameMode();
        this.oldHealth = player.getHealth();
        this.oldMaxHealth = player.getMaxHealth();
        this.oldHunger = player.getFoodLevel();
        this.oldExperience = player.getTotalExperience();
        this.oldLevel = player.getLevel();
        this.oldRemainingAir = player.getRemainingAir();
        this.oldFireTicks = player.getFireTicks();
        this.oldPotionEffects = player.getActivePotionEffects();
        saveToFile();
    }

    private PlayerData(UUID playerUUID,
                       String oldDisplayName,
                       ItemStack[] oldInventoryContents,
                       ItemStack[] oldArmorContents,
                       Collection<PotionEffect> oldPotionEffects,
                       Location oldLocation,
                       GameMode oldGameMode,
                       double oldMaxHealth,
                       double oldHealth,
                       int oldHunger,
                       int oldExperience,
                       int oldLevel,
                       int oldRemainingAir,
                       int oldFireTicks){
        this.playerUUID = playerUUID;
        this.oldDisplayName = oldDisplayName;
        this.oldInventoryContents = oldInventoryContents;
        this.oldArmorContents = oldArmorContents;
        this.oldPotionEffects = oldPotionEffects;
        this.oldLocation = oldLocation;
        this.oldGameMode = oldGameMode;
        this.oldHealth = oldHealth;
        this.oldMaxHealth = oldMaxHealth;
        this.oldHunger = oldHunger;
        this.oldExperience = oldExperience;
        this.oldLevel = oldLevel;
        this.oldRemainingAir = oldRemainingAir;
        this.oldFireTicks = oldFireTicks;
    }

    public UUID getUUID(){
        return playerUUID;
    }

    public Location getOldLocation(){
        return oldLocation;
    }

    public ItemStack[] getOldArmorContents(){
        return oldArmorContents;
    }

    private void saveToFile(){
        try {
            File fileDirectory = new File(Domination.getPlugin().getDataFolder(), "Data");

            File worldDirectory = new File(fileDirectory, Domination.getPlugin().getWorldName());
            if (!worldDirectory.exists())
                worldDirectory.mkdir();

            File dataDirectory = new File(worldDirectory, "PlayerData");
            if (!dataDirectory.exists())
                dataDirectory.mkdir();

            File playerDataFile = new File(dataDirectory, this.playerUUID+ ".yml");
            if (!playerDataFile.exists())
                playerDataFile.createNewFile();

            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);

            config.set("player.UUID", this.playerUUID.toString());
            config.set("player.displayName", this.oldDisplayName);
            config.set("player.inventory", this.oldInventoryContents);
            config.set("player.armor", this.oldArmorContents);
            config.set("player.location", locationToString(this.oldLocation));
            config.set("player.maxHealth", this.oldMaxHealth);
            config.set("player.health", this.oldHealth);
            config.set("player.gamemode", this.oldGameMode.toString());
            config.set("player.hunger", this.oldHunger);
            config.set("player.experience", this.oldExperience);
            config.set("player.level", this.oldLevel);
            config.set("player.air", this.oldRemainingAir);
            config.set("player.fireTicks", this.oldFireTicks);
            config.set("player.potionEffects", this.oldPotionEffects);

            config.save(playerDataFile);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static PlayerData loadFromFile(Player player){
        if(player == null)
            return null;
        File fileDirectory = new File(Domination.getPlugin().getDataFolder(), "Data");

        File worldDirectory = new File(fileDirectory, Domination.getPlugin().getWorldName());
        if (!worldDirectory.exists())
            worldDirectory.mkdir();

        File dataDirectory = new File(worldDirectory, "PlayerData");
        if (!dataDirectory.exists())
            dataDirectory.mkdir();

        File playerDataFile = new File(dataDirectory, player.getUniqueId()+ ".yml");
        if (playerDataFile.exists()) {

            YamlConfiguration config = YamlConfiguration.loadConfiguration(playerDataFile);

            UUID uuid = UUID.fromString(config.getString("player.UUID"));
            String displayName = config.getString("player.displayName");
            List<ItemStack> inventory = (List<ItemStack>)config.getList("player.inventory");
            List<ItemStack> armor = (List<ItemStack>)config.getList("player.armor");
            List<PotionEffect> potionEffects = (List<PotionEffect>)config.getList("player.potionEffects");
            String locString = config.getString("player.location");
            Location location = locationFromString(locString);
            GameMode gameMode = GameMode.valueOf(config.getString("player.gamemode"));
            int maxHealth = config.getInt("player.maxHealth");
            int health = config.getInt("player.health");
            int hunger = config.getInt("player.hunger");
            int experience = config.getInt("player.experience");
            int level = config.getInt("player.level");
            int air = config.getInt("player.air");
            int fireTicks = config.getInt("player.fireTicks");

            PlayerData data = new PlayerData(uuid, displayName, inventory.toArray(new ItemStack[inventory.size()]), armor.toArray(new ItemStack[armor.size()]), potionEffects, location, gameMode, maxHealth, health, hunger, experience, level, air, fireTicks);
            return data;
        }
        return null;
    }

    //this method is called when the player data is returned to the controlling player
    public void apply() {
        Player player = Bukkit.getPlayer(this.playerUUID);
        if(player == null)
            return;
        player.setDisplayName(oldDisplayName);
        player.setPlayerListName(player.getName());
        player.getInventory().setContents(oldInventoryContents);
        player.getInventory().setArmorContents(oldArmorContents);
        player.setGameMode(oldGameMode);
        player.setFoodLevel(this.oldHunger);
        player.setTotalExperience(this.oldExperience);
        player.setLevel(this.oldLevel);
        player.teleport(oldLocation);

        player.setMaxHealth(this.oldMaxHealth);
        player.setHealth(this.oldHealth);
        player.setRemainingAir(this.oldRemainingAir);
        player.setFireTicks(this.oldFireTicks);

        for(PotionEffect effect : oldPotionEffects) {
            player.addPotionEffect(effect);
        }
        removeFile();
    }

    private boolean removeFile(){
        File fileDirectory = new File(Domination.getPlugin().getDataFolder(), "Data");

        File worldDirectory = new File(fileDirectory, Domination.getPlugin().getWorldName());
        if (!worldDirectory.exists())
            worldDirectory.mkdir();

        File dataDirectory = new File(worldDirectory, "PlayerData");
        if (!dataDirectory.exists())
            dataDirectory.mkdir();

        File playerDataFile = new File(dataDirectory, this.playerUUID+ ".yml");

        if (!playerDataFile.exists()) {
            return false;
        }
        else{
            playerDataFile.delete();
            return true;
        }
    }

    private static String locationToString(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    private static Location locationFromString(String locString) {
        String[] parts = locString.split(",");
        return new Location(Bukkit.getServer().getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
    }
}
