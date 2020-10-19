package com.snowgears.arathibasin.util;

import com.snowgears.arathibasin.ArathiBasin;
import com.snowgears.arathibasin.score.PlayerScore;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;

/**
 * This class will be registered through the register-method in the
 * plugins onEnable-method.
 */
public class ArathiPlaceholderExpansion extends PlaceholderExpansion {

    private ArathiBasin plugin;

    /**
     * Since we register the expansion inside our own plugin, we
     * can simply use this method here to get an instance of our
     * plugin.
     *
     * @param plugin
     *        The instance of our plugin.
     */
    public ArathiPlaceholderExpansion(ArathiBasin plugin){
        this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist(){
        return true;
    }

    /**
     * Because this is a internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister(){
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convienience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "arathibasin";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     *
     * For convienience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param  player
     *         A {@link org.bukkit.entity.Player Player}.
     * @param  identifier
     *         A String containing the identifier/value.
     *
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null){
            return "";
        }

        try {

            switch (identifier) {
                case "red_name":
                    return plugin.getRedTeamName();
                case "blue_name":
                    return plugin.getBlueTeamName();
                case "red_score":
                    return "" + plugin.getArathiGame().getScoreManager().getRedScore();
                case "blue_score":
                    return "" + plugin.getArathiGame().getScoreManager().getBlueScore();

                    //add player stat here
                default:
                    break;
            }

            int index = identifier.indexOf("stat");
            if(index < 0)
                return null;

            //stat_1_kills
            //stat_self_kills
            //stat_self_place

            String[] split = identifier.split("_");
            if(split[1].equals("self")){
                return this.getSelfScoreValue(player, split[2]);
            }
            else{
                int rank = Integer.parseInt(split[1]);
                return this.getPlayerScoreValue(rank, split[2]);
            }

        } catch (Exception e){
            return null;
        }

        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%)
        // was provided
    }

    private String getPlayerScoreValue(int rankIndex, String stat){

        try {
            PlayerScore score = plugin.getArathiGame().getScoreManager().getScoreAtRank(rankIndex);
            Player player = plugin.getServer().getPlayer(score.getPlayerName());
            DyeColor team = plugin.getArathiGame().getTeamManager().getCurrentTeam(player).getColor();

            String value = "";
            switch(stat) {
                case "name":
                    value = score.getPlayerName();
                    break;
                case "points":
                    value = ""+score.getPoints();
                    break;
                case "kills":
                    value = ""+score.getKills();
                    break;
                case "deaths":
                    value = ""+score.getDeaths();
                    break;
                case "captures":
                    value = ""+score.getCaptures();
                    break;
                case "assaults":
                    value = ""+score.getAssaults();
                    break;
                case "defends":
                    value = ""+score.getDefends();
                    break;
                default:
                    break;
            }

            //put the team color in front of each score
            switch (team) {
                case RED:
                    return ChatColor.RED + value;
                case BLUE:
                    return ChatColor.BLUE + value;

                    default:
                        return "";
            }

        } catch(IndexOutOfBoundsException e){
            return "";
        } catch(NullPointerException e) {
            return "";
        }
    }

    private String getSelfScoreValue(Player player, String stat){
        PlayerScore score = plugin.getArathiGame().getScoreManager().getPlayerScore(player);

        try {
            DyeColor team = plugin.getArathiGame().getTeamManager().getCurrentTeam(player).getColor();

            String value = "";
            switch(stat) {
                case "name":
                    value = score.getPlayerName();
                    break;
                case "points":
                    value = ""+score.getPoints();
                    break;
                case "kills":
                    value = ""+score.getKills();
                    break;
                case "deaths":
                    value = ""+score.getDeaths();
                    break;
                case "captures":
                    value = ""+score.getCaptures();
                    break;
                case "assaults":
                    value = ""+score.getAssaults();
                    break;
                case "defends":
                    value = ""+score.getDefends();
                    break;
                case "place":
                    value = ""+plugin.getArathiGame().getScoreManager().getCurrentRanking(player);
                    break;
                default:
                    break;
            }

            //put the team color in front of each score
            switch (team) {
                case RED:
                    return ChatColor.RED + value;
                case BLUE:
                    return ChatColor.BLUE + value;

                default:
                    return "";
            }

        } catch(IndexOutOfBoundsException e){
            return "";
        } catch(NullPointerException e) {
            return "";
        }
    }
}
