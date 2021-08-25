package com.snowgears.domination.util.tabbed.item;


import com.snowgears.domination.util.tabbed.skin.Skin;
import com.snowgears.domination.util.tabbed.skin.Skins;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.function.Function;

/**
 * A tab item that represents a player.
 */

public class PlayerTabItem implements TabItem {

    private final Player player;
    private final PlayerProvider<String> textProvider;
    private final PlayerProvider<Skin> skinProvider;
    private String text;
    private int ping;
    private Skin skin;

    public PlayerTabItem(Player player, PlayerProvider<String> textProvider, PlayerProvider<Skin> skinProvider) {
        this.player = player;
        this.textProvider = textProvider;
        this.skinProvider = skinProvider;
        this.text = textProvider.apply(player);
        this.ping = getNewPing();
        this.skin = skinProvider.apply(player);
    }

    public PlayerTabItem(Player player, PlayerProvider<String> textProvider) {
        this(player, textProvider, Skins::getPlayer);
    }

    public PlayerTabItem(Player player) {
        this(player, Player::getPlayerListName);
    }

    @Override
    public boolean updateText() {
        if (!this.player.isOnline())
            return false;

        String text = this.textProvider.apply(this.player);
        boolean update = !Objects.equals(this.text, text);
        this.text = text;
        return update;
    }

    @Override
    public boolean updatePing() {
        if (!this.player.isOnline())
            return false;

        int ping = getNewPing();
        boolean update = this.ping != ping;
        this.ping = ping;
        return update;
    }

    @Override
    public boolean updateSkin() {
        if (!this.player.isOnline() || !this.player.isValid())
            return false;

        Skin skin = this.skinProvider.apply(this.player);
        boolean update = !Objects.equals(this.skin, skin);
        this.skin = skin;
        return update;
    }

    private int getNewPing() {
        try {
            return this.player.getPing();
        } catch (NoSuchMethodError e){
            return 0;
        }
    }

    /**
     * A provider of player specific information
     *
     * @param <T> the type of the information provided
     */
    public interface PlayerProvider<T> extends Function<Player, T> {

    	/**
    	 * Gets the relevant information about this player
    	 * 
    	 */
        @Override
		T apply(Player player);

    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ping;
		result = prime * result + ((skin == null) ? 0 : skin.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
		return result;
	}
    
    @Override
	public boolean equals(Object object) {
        if (!(object instanceof PlayerTabItem))
            return false;
        PlayerTabItem other = (PlayerTabItem) object;
        return this.text.equals(other.getText()) && this.skin.equals(other.getSkin()) && this.ping == other.getPing();
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerProvider<String> getTextProvider() {
        return textProvider;
    }

    public PlayerProvider<Skin> getSkinProvider() {
        return skinProvider;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public int getPing() {
        return ping;
    }

    @Override
    public Skin getSkin() {
        return skin;
    }
}
