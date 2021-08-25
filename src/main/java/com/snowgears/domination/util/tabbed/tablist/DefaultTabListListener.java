package com.snowgears.domination.util.tabbed.tablist;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

class DefaultTabListListener implements Listener {

	private final DefaultTabList tabList;
	
	DefaultTabListListener(DefaultTabList tabList) {
		this.tabList = tabList;
	}
	
    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        tabList.addPlayer(event.getPlayer());
    }

    @EventHandler
    private void onPlayerJoin(PlayerQuitEvent event) {
        tabList.remove(tabList.getTabItemIndex(event.getPlayer()));
    }
	
}
