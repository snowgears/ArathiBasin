package com.snowgears.domination.util;

import com.snowgears.domination.Domination;
import com.snowgears.domination.game.BattleTeam;
import com.snowgears.domination.structure.Structure;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;


public class DominationMessage {

    private static HashMap<String, String> messageMap = new HashMap<String, String>();
    private static YamlConfiguration chatConfig;

    public DominationMessage(Domination plugin) {

        File chatConfigFile = new File(plugin.getDataFolder(), "chatConfig.yml");
        chatConfig = YamlConfiguration.loadConfiguration(chatConfigFile);

        loadMessagesFromConfig();
    }

    public static String getMessage(String key, String subKey, Structure structure, BattleTeam team, Player player) {
        String message = "";
        String mainKey = key;
        if (subKey != null) {
            mainKey = key + "_" + subKey;
        }

        if(messageMap.containsKey(mainKey))
            message = messageMap.get(mainKey);
        else
            return message;

        message = formatMessage(message, structure, team, player);
        return message;
    }

    public static String getUnformattedMessage(String key, String subKey) {
        String message;
        if (subKey != null)
            message = messageMap.get(key + "_" + subKey);
        else
            message = messageMap.get(key);
        return message;
    }

    public static String formatMessage(String unformattedMessage, Structure structure, BattleTeam team, Player player){
        if(unformattedMessage == null) {
            loadMessagesFromConfig();
            return "";
        }
        if(structure != null) {
            unformattedMessage = unformattedMessage.replace("[structure name]", "" + structure.getName());
        }

        if(team != null) {
            unformattedMessage = unformattedMessage.replace("[team color]", "" + team.getColor().toString());
        }

        if(player != null) {
            unformattedMessage = unformattedMessage.replace("[player]", "" + player.getName());
        }

        unformattedMessage = unformattedMessage.replace("[main command]", "" + Domination.getPlugin().getDominationCommand());
        unformattedMessage = unformattedMessage.replace("[structure command]", "" + Domination.getPlugin().getStructureCommand());

        unformattedMessage = ChatColor.translateAlternateColorCodes('&', unformattedMessage);
        return unformattedMessage;
    }

    private static void loadMessagesFromConfig() {


        messageMap.put("permission_use", chatConfig.getString("permission.use"));

    }
}