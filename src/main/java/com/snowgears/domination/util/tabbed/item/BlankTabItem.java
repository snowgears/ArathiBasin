package com.snowgears.domination.util.tabbed.item;

import com.snowgears.domination.util.tabbed.skin.Skin;
import com.snowgears.domination.util.tabbed.skin.Skins;

/**
 * A blank TextTabItem
 */

public class BlankTabItem extends TextTabItem {
    public BlankTabItem(Skin skin) {
        super("", 1000, skin);
    }

    public BlankTabItem() {
        this(Skins.getDefault());
    }
}
