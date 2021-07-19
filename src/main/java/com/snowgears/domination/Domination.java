package com.snowgears.domination;

import com.snowgears.domination.command.DominationCommand;
import com.snowgears.domination.command.StructureCommand;
import com.snowgears.domination.game.DominationGame;
import com.snowgears.domination.game.GameListener;
import com.snowgears.domination.score.PlayerScoreboardListener;
import com.snowgears.domination.structure.SetupStructureListener;
import com.snowgears.domination.structure.StructureManager;
import com.snowgears.domination.util.ConfigUpdater;
import com.snowgears.domination.util.DominationPlaceholderExpansion;
import com.snowgears.domination.util.FileUtils;
import com.snowgears.domination.util.UnzipUtility;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Domination extends JavaPlugin {

    private static Domination plugin;
    private YamlConfiguration config;
    private PlayerScoreboardListener scoreListener;
    private GameListener gameListener;
    private SetupStructureListener setupListener;
    private StructureManager structureManager;
    private DominationGame dominationGame;

    private boolean usePerms;
    private String dominationCommand;
    private String structureCommand;
    private String worldName;
    private String blueTeamName;
    private String redTeamName;
    private int minTeamSize;
    private int maxTeamSize;
    private int scoreWarning;
    private int scoreWin;
    private int gameMaxTime;
    private int gameTimeWarning;
    private int startWait;
    private int endWait;
    private int respawnWait;
    private int baseAssaultInterval;
    private int baseCaptureInterval;
    private boolean debug;

    public static Domination getPlugin() {
        return plugin;
    }

    public void onEnable() {
        plugin = this;
        gameListener = new GameListener(this);
        scoreListener = new PlayerScoreboardListener(this);
        setupListener = new SetupStructureListener(this);
        getServer().getPluginManager().registerEvents(scoreListener, this);
        getServer().getPluginManager().registerEvents(gameListener, this);
        getServer().getPluginManager().registerEvents(setupListener, this);

        // Small check to make sure that PlaceholderAPI is installed (used for tab scoreboards in game as an option)
        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
            new DominationPlaceholderExpansion(this).register();
        }

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            FileUtils.copy(getResource("config.yml"), configFile);
        }
        try {
            ConfigUpdater.update(plugin, "config.yml", configFile, new ArrayList<>());
        } catch (IOException e) {
            e.printStackTrace();
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        usePerms = config.getBoolean("usePermissions");

        dominationCommand = config.getString("dominationCommand");
        structureCommand = config.getString("structureCommand");
        worldName = config.getString("worldName");
        blueTeamName = config.getString("blueTeamName");
        redTeamName = config.getString("redTeamName");
        minTeamSize = config.getInt("minTeamSize");
        maxTeamSize = config.getInt("maxTeamSize");
        scoreWarning = config.getInt("scoreWarning");
        scoreWin = config.getInt("scoreWin");
        gameMaxTime = config.getInt("gameMaxTime");
        gameTimeWarning = config.getInt("gameTimeWarning");
        startWait = config.getInt("startWait");
        endWait = config.getInt("endWait");
        respawnWait = config.getInt("respawnWait");

        baseAssaultInterval = config.getInt("baseAssaultInterval");
        baseCaptureInterval = config.getInt("baseCaptureInterval");

        debug = config.getBoolean("debug");

        this.getCommand(dominationCommand).setExecutor(new DominationCommand(this));
        this.getCommand(structureCommand).setExecutor(new StructureCommand(this));


        generateWorld();

        structureManager = new StructureManager(this);
        dominationGame = new DominationGame();
    }

    public void onDisable() {
        plugin = null;
        this.structureManager.saveStructures();
    }

    public boolean usePerms(){
        return usePerms;
    }

    public String getDominationCommand(){
        return dominationCommand;
    }

    public String getStructureCommand(){
        return structureCommand;
    }

    public String getWorldName(){
        return worldName;
    }

    public StructureManager getStructureManager(){
        return structureManager;
    }

    public DominationGame getDominationGame(){
        return dominationGame;
    }

    public String getBlueTeamName(){
        return blueTeamName;
    }

    public String getRedTeamName(){
        return redTeamName;
    }

    public int getMinTeamSize(){
        return minTeamSize;
    }

    public int getMaxTeamSize(){
        return maxTeamSize;
    }

    public int getScoreWarning(){
        return scoreWarning;
    }

    public int getScoreWin(){
        return scoreWin;
    }

    public int getGameMaxTime(){
        return gameMaxTime;
    }

    public int getGameTimeWarning(){
        return gameTimeWarning;
    }

    public int getStartWait(){
        return startWait;
    }

    public int getEndWait(){
        return endWait;
    }

    public int getRespawnWait(){
        return respawnWait;
    }

    public int getBaseAssaultInterval(){
        return baseAssaultInterval;
    }

    public int getBaseCaptureInterval(){
        return baseCaptureInterval;
    }

    public boolean isDebug(){
        return debug;
    }

    private void generateWorld(){

        File world_domination = new File(plugin.getServer().getWorldContainer(), plugin.getWorldName());
        if(world_domination.exists()) {
            getServer().createWorld(new WorldCreator(plugin.getWorldName()));
            return;
        }
        else
            world_domination.mkdir();

        File dest = new File(world_domination, "world_domination.zip");
        FileUtils.copy(getResource("world_domination.zip"), dest); //copy zip file into world folder

        UnzipUtility uu = new UnzipUtility();
        //try to unzip the file into the battleground world folder
        try {
            uu.unzip(dest.getAbsolutePath(), world_domination.getAbsolutePath());
            dest.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getServer().createWorld(new WorldCreator(plugin.getWorldName()));
    }
}