package com.snowgears.arathibasin;

import com.snowgears.arathibasin.scoreboard.PlayerScore;
import com.snowgears.arathibasin.scoreboard.PlayerScoreboardListener;
import com.snowgears.arathibasin.scoreboard.ScoreManager;
import com.snowgears.arathibasin.util.UnzipUtility;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class ArathiBasin extends JavaPlugin {

    private static ArathiBasin plugin;
    private YamlConfiguration config;
    private final PlayerScoreboardListener controlListener = new PlayerScoreboardListener(this);
    private TeamManager teamManager;
    private ScoreManager scoreManager;

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
        getServer().getPluginManager().registerEvents(controlListener, this);

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

        teamManager = new TeamManager();
        scoreManager = new ScoreManager();


        generateWorld();
    }

    public void onDisable() {
        plugin = null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("arathi") && args.length == 0){
            if(sender instanceof Player){
                Player player = (Player)sender;
                teamManager.addPlayer(player);
                PlayerScore score = new PlayerScore(player);
                if(player.getWorld().getName().equals("world_arathi"))
                    player.teleport(getServer().getWorld("world").getSpawnLocation());
                else
                    player.teleport(getServer().getWorld("world_arathi").getSpawnLocation());
            }
        }
        else if(cmd.getName().equalsIgnoreCase("arathi") && args.length == 1){
            if(sender instanceof Player){
                Player player = (Player)sender;
                player.getWorld().strikeLightningEffect(player.getLocation());
            }
        }
        return true;
    }

    public boolean usePerms(){
        return usePerms;
    }

    public TeamManager getTeamManager(){
        return teamManager;
    }

    public ScoreManager getScoreManager(){
        return scoreManager;
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
        System.out.println(world_arathi.getAbsolutePath());
        if(world_arathi.exists()) {
            getServer().createWorld(new WorldCreator("world_arathi"));
            return;
        }

        File dest = new File(world_arathi.getAbsolutePath()+"/world_arathi.zip");
        System.out.println(dest.getAbsolutePath());
        this.copy(getResource("world_arathi.zip"), dest); //copy zip file into world folder

        UnzipUtility uu = new UnzipUtility();
        //try to unzip the file into the battleground world folder
        try {
            uu.unzip(dest.getAbsolutePath(), plugin.getServer().getWorldContainer().getAbsolutePath());
            dest.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getServer().createWorld(new WorldCreator("world_arathi"));
    }
}