package com.snowgears.arathibasin;

import com.snowgears.arathibasin.command.ArathiCommand;
import com.snowgears.arathibasin.command.StructureCommand;
import com.snowgears.arathibasin.game.ArathiGame;
import com.snowgears.arathibasin.game.GameListener;
import com.snowgears.arathibasin.score.PlayerScoreboardListener;
import com.snowgears.arathibasin.structure.SetupStructureListener;
import com.snowgears.arathibasin.structure.StructureManager;
import com.snowgears.arathibasin.util.FileUtils;
import com.snowgears.arathibasin.util.UnzipUtility;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ArathiBasin extends JavaPlugin {

    private static ArathiBasin plugin;
    private YamlConfiguration config;
    private PlayerScoreboardListener scoreListener;
    private GameListener gameListener;
    private SetupStructureListener setupListener;
    private StructureManager structureManager;
    private ArathiGame arathiGame;

    private boolean usePerms;
    private String blueTeamName;
    private String redTeamName;
    private int minTeamSize;
    private int maxTeamSize;
    private int scoreWarning;
    private int scoreWin;
    private int startWait;
    private int endWait;
    private int baseAssaultInterval;
    private int baseCaptureInterval;

    public static ArathiBasin getPlugin() {
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


        this.getCommand("arathi").setExecutor(new ArathiCommand(this));
        this.getCommand("structure").setExecutor(new StructureCommand(this));

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            FileUtils.copy(getResource("config.yml"), configFile);
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        usePerms = config.getBoolean("usePermissions");

        blueTeamName = config.getString("blueTeamName");
        redTeamName = config.getString("redTeamName");
        minTeamSize = config.getInt("minTeamSize");
        maxTeamSize = config.getInt("maxTeamSize");
        scoreWarning = config.getInt("scoreWarning");
        scoreWin = config.getInt("scoreWin");
        startWait = config.getInt("startWait");
        endWait = config.getInt("endWait");

        baseAssaultInterval = config.getInt("baseAssaultInterval");
        baseCaptureInterval = config.getInt("baseCaptureInterval");

        generateWorld();

        structureManager = new StructureManager(this);
        arathiGame = new ArathiGame();
    }

    public void onDisable() {
        plugin = null;
        this.structureManager.saveStructures();
    }

    public boolean usePerms(){
        return usePerms;
    }

    public StructureManager getStructureManager(){
        return structureManager;
    }

    public ArathiGame getArathiGame(){
        return arathiGame;
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

    public int getStartWait(){
        return startWait;
    }

    public int getEndWait(){
        return endWait;
    }

    public int getBaseAssaultInterval(){
        return baseAssaultInterval;
    }

    public int getBaseCaptureInterval(){
        return baseCaptureInterval;
    }

    private void generateWorld(){

        File world_arathi = new File(plugin.getServer().getWorldContainer(), "world_arathi");
        if(world_arathi.exists()) {
            getServer().createWorld(new WorldCreator("world_arathi"));
            return;
        }
        else
            world_arathi.mkdir();

        File dest = new File(world_arathi, "world_arathi.zip");
        FileUtils.copy(getResource("world_arathi.zip"), dest); //copy zip file into world folder

        UnzipUtility uu = new UnzipUtility();
        //try to unzip the file into the battleground world folder
        try {
            uu.unzip(dest.getAbsolutePath(), world_arathi.getAbsolutePath());
            dest.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getServer().createWorld(new WorldCreator("world_arathi"));
    }
}