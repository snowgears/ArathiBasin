package com.snowgears.arathibasin.score;

import com.keenant.tabbed.item.TextTabItem;
import com.keenant.tabbed.tablist.TableTabList;
import com.snowgears.arathibasin.ArathiBasin;
import com.snowgears.arathibasin.game.BattleTeam;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.UUID;

/**
 * Basic object used to store different scores and statistics of a single {@link Player}.
 *
 * <P>Various values to indicate current state of scoring.
 * <P>Each PlayerScore object contains a score for the associated Player.
 */

public class PlayerScore {

    private String playerName;
    private UUID playerUUID;
    private int kills;
    private int deaths;
    private int captures;
    private int assaults;
    private int defends;
    private int points;
    private boolean showFullScore;
    private boolean isSpectator;

    private Scoreboard scoreboard;
    private TableTabList tabList;

    public PlayerScore(Player player){
        this.playerName = player.getName();
        this.playerUUID = player.getUniqueId();
        showFullScore = false;

        BattleTeam team = ArathiBasin.getPlugin().getArathiGame().getTeamManager().getCurrentTeam(player);
        if(team != null) {
            setupScoreboard(team);
        }
        else{
            setupScoreboardSpectator();
        }
        initTabList();
    }

    public String getPlayerName(){
        return playerName;
    }

    public UUID getPlayerUUID(){
        return playerUUID;
    }

    public int getKills(){
        return kills;
    }

    public void addKills(int kills){
        this.kills += kills;
        this.points += (kills * 20); //20 points per kill
        if(showFullScore)
            this.update();
    }

    public int getDeaths(){
        return deaths;
    }

    public void addDeaths(int deaths){
        this.deaths += deaths;
        if(showFullScore)
            this.update();
    }

    public int getCaptures(){
        return captures;
    }

    public void addCaptures(int captures){
        this.captures += captures;
        this.points += (captures * 100); //100 points per capture
        if(showFullScore)
            this.update();
    }

    public int getAssaults(){
        return assaults;
    }

    public void addAssaults(int assaults){
        this.assaults += assaults;
        this.points += (assaults * 50); //50 points per assault
        if(showFullScore)
            this.update();
    }

    public int getDefends(){
        return defends;
    }

    public void addDefends(int defends){
        this.defends += defends;
        this.points += (defends * 150); //150 points per defend
        if(showFullScore)
            this.update();
    }

    public int getPoints(){
        return points;
    }

    public boolean isSpectator(){
        return isSpectator;
    }

    public void addPoints(int points){
        this.points += points;
        if(showFullScore)
            this.update();
    }

    public boolean getShowFullScore(){
        return showFullScore;
    }

    public void setShowFullScore(boolean showFullScore){
        this.showFullScore = showFullScore;
        this.update();
    }

    public void update(){

        Player player = Bukkit.getPlayer(playerUUID);
        if(player == null)
            return;

        Objective buffer;
        if(scoreboard.getObjective(DisplaySlot.SIDEBAR).getName().equals("score")){
            buffer = scoreboard.getObjective("buffer");
        }
        else{
            buffer = scoreboard.getObjective("score");
        }

        //unregister buffer objective
        String name = buffer.getName();
        buffer.unregister();
        //register new buffer objective
        buffer = scoreboard.registerNewObjective(name, "dummy");
        buffer.setDisplayName(ChatColor.BOLD+"Score");

        int redPoints = ArathiBasin.getPlugin().getArathiGame().getScoreManager().getRedScore();
        int bluePoints = ArathiBasin.getPlugin().getArathiGame().getScoreManager().getBlueScore();
        //put all scores in new buffer objective
        if(showFullScore) {
            //make sure the higher score is shown above the other
            int red = 12;
            int redNum = 11;
            int blue = 10;
            int blueNum = 9;
            if(redPoints < bluePoints){
                red = 10;
                redNum = 9;
                blue = 12;
                blueNum = 11;
            }
            Score redScore = buffer.getScore(ChatColor.RED + "" + ChatColor.BOLD + ArathiBasin.getPlugin().getRedTeamName());
            redScore.setScore(red);

            Score redScoreNum = buffer.getScore("    " + ChatColor.RED + "" + ChatColor.BOLD + ArathiBasin.getPlugin().getArathiGame().getScoreManager().getRedScore());
            redScoreNum.setScore(redNum);

            Score blueScore = buffer.getScore(ChatColor.BLUE + "" + ChatColor.BOLD + ArathiBasin.getPlugin().getBlueTeamName());
            blueScore.setScore(blue);

            Score blueScoreNum = buffer.getScore("    " + ChatColor.BLUE + "" + ChatColor.BOLD + ArathiBasin.getPlugin().getArathiGame().getScoreManager().getBlueScore());
            blueScoreNum.setScore(blueNum);

            Score bases = buffer.getScore(ChatColor.BOLD + "Bases:");
            bases.setScore(8);

            //if blue score is being shown above redscore
            if(redPoints < bluePoints) {
                Score basesNum = buffer.getScore("    " + ChatColor.BLUE+ArathiBasin.getPlugin().getArathiGame().getScoreManager().getBlueBases()+ChatColor.GRAY+" - "+ChatColor.RED + "" + ArathiBasin.getPlugin().getArathiGame().getScoreManager().getRedBases());
                basesNum.setScore(7);
            }
            else{
                Score basesNum = buffer.getScore("    " +ChatColor.RED+ArathiBasin.getPlugin().getArathiGame().getScoreManager().getRedBases()+ChatColor.GRAY+" - "+ ChatColor.BLUE+ArathiBasin.getPlugin().getArathiGame().getScoreManager().getBlueBases());
                basesNum.setScore(7);
            }

            Score points = buffer.getScore(ChatColor.GOLD + "Points:");
            points.setScore(6);

            Score pointsNum = buffer.getScore("    " + ChatColor.GOLD + "" + this.points);
            pointsNum.setScore(5);


//            Score assaults = buffer.getScore(ChatColor.RED+ "Assaults:");
//            assaults.setScore(8);
//
//            Score assaultsNum = buffer.getScore("    " + ChatColor.RED + "" + this.assaults);
//            assaultsNum.setScore(7);
//
//            Score captures = buffer.getScore(ChatColor.AQUA + "Captures:");
//            captures.setScore(6);
//
//            Score capturesNum = buffer.getScore("    " + ChatColor.AQUA + "" + this.captures);
//            capturesNum.setScore(5);

            //assaults, captures, and defends
            //TODO maybe make each letter in here a different chat color separated by GRAY '/' chars
            Score acd = buffer.getScore(ChatColor.LIGHT_PURPLE + "A / C / D:");
            acd.setScore(4);

            Score acdNum = buffer.getScore("    " + ChatColor.LIGHT_PURPLE + "" + this.assaults+" / "+this.captures+" / "+this.defends);
            acdNum.setScore(3);

            Score kills = buffer.getScore(ChatColor.GREEN + "K/D");
            kills.setScore(2);

            Score killsNum = buffer.getScore("    " + ChatColor.GREEN + "" + this.kills + " / " + this.deaths);
            killsNum.setScore(1);
        }
        //only show partial score
        else{
            Score redScore = buffer.getScore(ChatColor.RED + "" + ChatColor.BOLD + ArathiBasin.getPlugin().getRedTeamName());
            redScore.setScore(ArathiBasin.getPlugin().getArathiGame().getScoreManager().getRedScore());

            Score redBasesCount = buffer.getScore(ChatColor.RED + "" + "Bases");
            redBasesCount.setScore(ArathiBasin.getPlugin().getArathiGame().getScoreManager().getRedBases());

            Score blueScore = buffer.getScore(ChatColor.BLUE + "" + ChatColor.BOLD + ArathiBasin.getPlugin().getBlueTeamName());
            blueScore.setScore(ArathiBasin.getPlugin().getArathiGame().getScoreManager().getBlueScore());

            Score blueBasesCount = buffer.getScore(ChatColor.BLUE + "" + "Bases");
            blueBasesCount.setScore(ArathiBasin.getPlugin().getArathiGame().getScoreManager().getBlueBases());

        }
        //set the new buffer objective to active by assigning it to the display slot
        //scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        buffer.setDisplaySlot(DisplaySlot.SIDEBAR);

        refreshTabList();
    }

    public void addPlayerToTeam(Player player, DyeColor color){
        scoreboard.getTeam(color.toString()).addEntry(player.getName());
    }

    public void removePlayerFromTeam(Player player, DyeColor color){
        scoreboard.getTeam(color.toString()).removeEntry(player.getName());
    }

    private void setupScoreboard(BattleTeam team){
        if(team == null)
            return;

        Player player = Bukkit.getPlayer(playerUUID);
        if(player == null)
            return;

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Team redTeam = scoreboard.registerNewTeam("RED");
        setupScoreboardTeam(redTeam, ChatColor.RED);
        Team blueTeam = scoreboard.registerNewTeam("BLUE");
        setupScoreboardTeam(blueTeam, ChatColor.BLUE);


        //add yourself to your own team
        //this.addPlayerToTeam(player, team.getColor()); //TODO covered under loop below?
        //also add everyone who is already in the game to their respective teams
        for(Player p : ArathiBasin.getPlugin().getArathiGame().getTeamManager().getAllPlayers()){
            this.addPlayerToTeam(p, ArathiBasin.getPlugin().getArathiGame().getTeamManager().getCurrentTeam(p).getColor());
        }

        Objective objective = scoreboard.registerNewObjective("score", "dummy");
        objective.setDisplayName(ChatColor.BOLD+"Score");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Objective bufferObjective = scoreboard.registerNewObjective("buffer", "dummy");
        bufferObjective.setDisplayName(ChatColor.BOLD+"Score");

        Score bufferScore = bufferObjective.getScore("buffer");
        bufferScore.setScore(1);

        update();
        player.setScoreboard(scoreboard);
    }

    private void setupScoreboardSpectator(){

        Player player = Bukkit.getPlayer(playerUUID);
        if(player == null)
            return;

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Team redTeam = scoreboard.registerNewTeam("RED");
        setupScoreboardTeam(redTeam, ChatColor.RED);
        Team blueTeam = scoreboard.registerNewTeam("BLUE");
        setupScoreboardTeam(blueTeam, ChatColor.BLUE);

        Objective objective = scoreboard.registerNewObjective("score", "dummy");
        objective.setDisplayName(ChatColor.BOLD+"Score");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        Objective bufferObjective = scoreboard.registerNewObjective("buffer", "dummy");
        bufferObjective.setDisplayName(ChatColor.BOLD+"Score");

        Score bufferScore = bufferObjective.getScore("buffer");
        bufferScore.setScore(1);

        update();
        player.setScoreboard(scoreboard);

        isSpectator = true;
    }

    private void setupScoreboardTeam(Team team, ChatColor color) {
        try {
            team.setPrefix(color + "");
            //team.setAllowFriendlyFire(false); //TODO put this back after event
            team.setCanSeeFriendlyInvisibles(true);
            //team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OWN_TEAM);
            //team.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);
            //team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        } catch (NoClassDefFoundError e) {
            team.setPrefix(color + "");
            //team.setAllowFriendlyFire(false); //TODO put this back after event
            team.setCanSeeFriendlyInvisibles(true);
        }
    }

    private void initTabList(){
        if(ArathiBasin.getPlugin().getTabbed() != null) {
            Player player = Bukkit.getPlayer(playerUUID);
            if(player == null)
                return;

            if(this.tabList != null)
                return;

            ArathiBasin.getPlugin().getTabbed().destroyTabList(player);
            this.tabList = ArathiBasin.getPlugin().getTabbed().newTableTabList(player);

            refreshTabList();
        }
    }

    private void refreshTabList(){
        if(ArathiBasin.getPlugin().getTabbed() != null) {

            if(tabList == null)
                return;

            Player player = Bukkit.getPlayer(playerUUID);
            if(player == null)
                return;

            //tabList = ArathiBasin.getPlugin().getTabbed().getTabList(player); //TODO might need to try this

            //TODO you may need to loop batch update in tabbed in ScoreTask to not get blinking tabs for players

            //this.tabList = ArathiBasin.getPlugin().getTabbed().newTableTabList(player);

            //List<TabItem> items = new ArrayList<TabItem>();

            tabList.setBatchEnabled(true);

            //initialize row headers first
            tabList.set(0, new TextTabItem(ChatColor.WHITE+""+ChatColor.BOLD+"Name"));
            tabList.set(20, new TextTabItem(ChatColor.WHITE+""+ChatColor.BOLD+"Points"));
            tabList.set(40, new TextTabItem(ChatColor.WHITE+""+ChatColor.BOLD+"A / C / D"));
            tabList.set(60, new TextTabItem(ChatColor.WHITE+""+ChatColor.BOLD+"Kills / Deaths"));

            BattleTeam currentTeam;
            Player scorePlayer;
            ChatColor chatColor;
            int tabIndex = 1;
            int i=0;
            for(PlayerScore score : ArathiBasin.getPlugin().getArathiGame().getScoreManager().getOrderedPlayerScores()){
                //tablist only has room for 20 rows
                if(i > 17)
                    break;

                scorePlayer = Bukkit.getPlayer(score.getPlayerName());

                if(scorePlayer != null){
                    currentTeam = ArathiBasin.getPlugin().getArathiGame().getTeamManager().getCurrentTeam(scorePlayer);
                    if(currentTeam != null) {
                        chatColor = currentTeam.getChatColor();
                        //tabList.set(tabIndex, new PlayerTabItem(scorePlayer)); // this works (without chatcolor on player) but does not pull down skin
                        //tabList.set(tabIndex, new TextTabItem(chatColor+scorePlayer.getName(), 1000, Skins.getPlayer(score.getPlayerName())));
                        tabList.set(tabIndex, new TextTabItem(chatColor+scorePlayer.getName())); //this works but does not pull in skin
                        tabList.set(tabIndex+20, new TextTabItem(chatColor+""+score.getPoints()));
                        tabList.set(tabIndex+40, new TextTabItem(chatColor+""+score.getAssaults()+" / "+score.getCaptures()+" / "+score.getDefends()));
                        tabList.set(tabIndex+60, new TextTabItem(chatColor+""+score.getKills()+" / "+score.getDeaths()));
                        tabIndex++;
                    }
                }

                i++;
            }
            //tabList.fill(0, 0, 1, 1, items, TableTabList.TableCorner.TOP_LEFT, TableTabList.FillDirection.HORIZONTAL);

            //tabList.setHeader(ChatColor.GOLD + ""+ChatColor.BOLD+"Mizkif"+ChatColor.RESET+ChatColor.GRAY+" x "+ChatColor.DARK_PURPLE+ChatColor.BOLD+"Twitch Rivals "+ChatColor.RED+ChatColor.BOLD+"Domination");

            //tabList.setFooter(ChatColor.LIGHT_PURPLE + ""+ChatColor.BOLD+"To download this mini-game, visit: "+ChatColor.RESET+ChatColor.AQUA+ChatColor.BOLD+"smarturl.it/arathi");

            tabList.setHeader(ChatColor.GOLD + ""+ChatColor.BOLD+"Mizkif"+ChatColor.RESET+ChatColor.GRAY+" x "+ChatColor.DARK_PURPLE+ChatColor.BOLD+"Twitch Rivals "+ChatColor.RED+ChatColor.BOLD+"Domination");

            tabList.setFooter(ChatColor.LIGHT_PURPLE + ""+ChatColor.BOLD+"To download this mini-game, visit: "+ChatColor.RESET+ChatColor.AQUA+ChatColor.BOLD+"smarturl.it/arathi");

            tabList.batchUpdate();
            tabList.setBatchEnabled(false);
        }
    }
}
