package com.snowgears.arathibasin.game;

import com.snowgears.arathibasin.ArathiBasin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

public class TeamManager {

    private PlayerQueue queue;
    private BattleTeam redTeam;
    private BattleTeam blueTeam;
    private Scoreboard scoreboard;

    public TeamManager(){
        queue = new PlayerQueue(ArathiBasin.getPlugin());
        this.redTeam = new BattleTeam(DyeColor.RED);
        this.blueTeam = new BattleTeam(DyeColor.BLUE);

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Team redTeam = scoreboard.registerNewTeam("RED");
        setupScoreboardTeam(redTeam, ChatColor.RED);
        Team blueTeam = scoreboard.registerNewTeam("BLUE");
        setupScoreboardTeam(blueTeam, ChatColor.BLUE);
    }

    public boolean addPlayer(Player player, DyeColor teamPreference){
        if(redTeam.contains(player) || blueTeam.contains(player))
            return false;
        return queue.addPlayer(player, teamPreference);
    }

    public boolean removePlayer(Player player){
        boolean removed = queue.removePlayer(player);

        if(redTeam.contains(player))
            removed = redTeam.remove(player);
        if(blueTeam.contains(player))
            removed = blueTeam.remove(player);

        if(redTeam.size() == 0  && blueTeam.size() == 0)
            ArathiBasin.getPlugin().getArathiGame().endGame(true);
        return removed;
    }

    public BattleTeam getCurrentTeam(Player player){
        if(redTeam.contains(player))
            return redTeam;
        if(blueTeam.contains(player))
            return blueTeam;
        return null;
    }

    public BattleTeam getTeam(DyeColor color){
        if(color == DyeColor.RED)
            return redTeam;
        return blueTeam;
    }

    public ArrayList<Player> getAllPlayers(){
        ArrayList<Player> allPlayers = new ArrayList<>(redTeam.size()+blueTeam.size());
        allPlayers.addAll(redTeam.getPlayers());
        allPlayers.addAll(blueTeam.getPlayers());
        return allPlayers;
    }

    public Scoreboard getScoreboard(){
        return scoreboard;
    }

    public void clear(){
        redTeam.clear();
        blueTeam.clear();
        //keep the queue in-tact for previous waiting players
    }

    private void setupScoreboardTeam(Team team, ChatColor color) {
        team.setPrefix(color+"");
        team.setAllowFriendlyFire(false);
        team.setCanSeeFriendlyInvisibles(true);
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
        team.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
    }
}
