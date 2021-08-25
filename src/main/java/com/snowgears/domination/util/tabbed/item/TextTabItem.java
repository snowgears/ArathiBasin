package com.snowgears.domination.util.tabbed.item;

import com.snowgears.domination.util.tabbed.skin.Skin;
import com.snowgears.domination.util.tabbed.skin.Skins;

import java.util.Objects;

/**
 * A tab item with custom text, ping and skin.
 */
public class TextTabItem implements TabItem {

    private String text;

    private int ping;

    private Skin skin;

    private String newText;
    private int newPing;
    private Skin newSkin;

    public TextTabItem(String text) {
        this(text, 1000);
    }

    public TextTabItem(String text, int ping) {
        this(text, ping, Skins.getDefault());
    }

    public TextTabItem(String text, int ping, Skin skin) {
        this.newText = text;
        this.newPing = ping;
        this.newSkin = skin;
        updateText();
        updatePing();
        updateSkin();
    }

    public void setText(String text) {
        this.newText = text;
    }

    public void setPing(int ping) {
        this.newPing = ping;
    }

    public void setSkin(Skin skin) {
        this.newSkin = skin;
    }

    @Override
    public boolean updateText() {
        boolean update = !Objects.equals(this.text, this.newText);
        this.text = this.newText;
        return update;
    }

    @Override
    public boolean updatePing() {
        boolean update = this.ping != this.newPing;
        this.ping = this.newPing;
        return update;
    }

    @Override
    public boolean updateSkin() {
        boolean update = !Objects.equals(this.skin, this.newSkin);
        this.skin = this.newSkin;
        return update;
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
        if (!(object instanceof TextTabItem))
            return false;
        TextTabItem other = (TextTabItem) object;
        return this.text.equals(other.getText()) && this.skin.equals(other.getSkin()) && this.ping == other.getPing();
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
