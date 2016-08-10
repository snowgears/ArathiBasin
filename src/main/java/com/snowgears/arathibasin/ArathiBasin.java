package com.snowgears.arathibasin;

import com.snowgears.arathibasin.command.ArathiCommand;
import com.snowgears.arathibasin.command.StructureCommand;
import com.snowgears.arathibasin.game.GameListener;
import com.snowgears.arathibasin.score.PlayerScoreboardListener;
import com.snowgears.arathibasin.structure.SetupStructureListener;
import com.snowgears.arathibasin.structure.StructureManager;
import com.snowgears.arathibasin.util.UnzipUtility;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class ArathiBasin extends JavaPlugin {

    private static ArathiBasin plugin;
    private YamlConfiguration config;
    private final PlayerScoreboardListener scoreListener = new PlayerScoreboardListener(this);
    private final GameListener gameListener = new GameListener(this);
    private final SetupStructureListener setupListener = new SetupStructureListener(this);
    private StructureManager structureManager;

    private boolean usePerms;
    private String blueTeamName;
    private String redTeamName;
    private int minTeamSize;
    private int maxTeamSize;
    private int scoreWarning;
    private int scoreWin;

    public static ArathiBasin getPlugin() {
        return plugin;
    }

    public void onEnable() {
        plugin = this;
        getServer().getPluginManager().registerEvents(scoreListener, this);
        getServer().getPluginManager().registerEvents(gameListener, this);
        getServer().getPluginManager().registerEvents(setupListener, this);


        this.getCommand("arathi").setExecutor(new ArathiCommand(this));
        this.getCommand("structure").setExecutor(new StructureCommand(this));

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            copy(getResource("config.yml"), configFile);
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        usePerms = config.getBoolean("usePermissions");

        blueTeamName = config.getString("blueTeamName");
        redTeamName = config.getString("redTeamName");
        minTeamSize = config.getInt("minTeamSize");
        maxTeamSize = config.getInt("maxTeamSize");
        scoreWarning = config.getInt("scoreWarning");
        scoreWin = config.getInt("scoreWin");

        structureManager = new StructureManager(this);

        generateWorld();
    }

    public void onDisable() {
        plugin = null;
    }

    public boolean usePerms(){
        return usePerms;
    }

    public StructureManager getStructureManager(){
        return structureManager;
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

    public void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        this.copy(getResource("world_arathi.zip"), dest); //copy zip file into world folder

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