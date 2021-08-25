package com.snowgears.domination;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
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
import com.snowgears.domination.util.tabbed.TabbedManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Domination extends JavaPlugin {

    private static Domination plugin;
    private YamlConfiguration config;
    private PlayerScoreboardListener scoreListener;
    private GameListener gameListener;
    private SetupStructureListener setupListener;
    private StructureManager structureManager;
    private DominationGame dominationGame;
    private TabbedManager tabbedManager;

    private boolean usePerms;
    private String dominationCommand;
    private String structureCommand;
    private String worldName;
    private GameMode playerGameMode;
    private boolean rollbackWorld;
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

    private Economy econ = null;
    private double vaultEntryCost;
    private List<String> winnerCommands;
    private List<String> loserCommands;

    public static Domination getPlugin() {
        return plugin;
    }

    public void onLoad() {
        try {
            //try loading the protocollib plugin to see if we will be using custom player lists
            ProtocolLibrary.getProtocolManager();

            tabbedManager = new TabbedManager();
        }catch(NoClassDefFoundError e){
            tabbedManager = null;
        }
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
        try {
            playerGameMode = GameMode.valueOf(config.getString("gamemode"));
        } catch (IllegalArgumentException e){
            playerGameMode = GameMode.ADVENTURE;
        }
        rollbackWorld = config.getBoolean("rollbackWorldAfterGame");
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

        vaultEntryCost = config.getDouble("vaultEntryCost");
        winnerCommands = config.getStringList("winnerCommands");
        loserCommands = config.getStringList("loserCommands");

        this.getCommand(dominationCommand).setExecutor(new DominationCommand(this));
        this.getCommand(structureCommand).setExecutor(new StructureCommand(this));


        generateWorld();

        dominationGame = new DominationGame();
        structureManager = new StructureManager(this);
        //dominationGame = new DominationGame();

        if (vaultEntryCost > 0 && !setupEconomy()) {
            System.out.println("[Domination] [ERROR] Vault hook not detected!");
        } else {
            System.out.println("[Domination] Vault hook success.");
        }
    }

    public void onDisable() {
        plugin = null;
        //this.structureManager.saveStructures();
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

    public GameMode getPlayerGameMode(){
        return playerGameMode;
    }

    public boolean getRollbackWorld(){
        return rollbackWorld;
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

    public double getVaultEntryCost(){
        return vaultEntryCost;
    }

    public List<String> getWinnerCommands(){
        return winnerCommands;
    }

    public void runWinnerCommands(Player player){
        for(String command: winnerCommands){
            if(command != null && !command.isEmpty()){
                command = command.replace("[player]", player.getName());
                Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), command);
            }
        }
    }

    public List<String> getLoserCommands(){
        return loserCommands;
    }

    public void runLoserCommands(Player player){
        for(String command: loserCommands){
            if(command != null && !command.isEmpty()){
                command = command.replace("[player]", player.getName());
                Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), command);
            }
        }
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

    public Economy getEconomy() {
        if (econ == null) {
            setupEconomy();
        }
        return econ;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public TabbedManager getTabbedManager(){
        return tabbedManager;
    }

}