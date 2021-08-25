package com.snowgears.domination.util.tabbed.tablist;

import com.snowgears.domination.Domination;
import com.snowgears.domination.util.tabbed.item.PlayerTabItem;
import com.snowgears.domination.util.tabbed.item.TabItem;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

/**
 * An implementation of SimpleTabList that behaves like vanilla Minecraft.
 */
public final class DefaultTabList extends SimpleTabList {
    private final Map<UUID, String> names = new HashMap<>();

    private BukkitTask task;
    
    private DefaultTabListListener listener;

    public DefaultTabList(Player player, int maxItems) {
        super(player, maxItems, -1, -1);
    }

    @Override
    public void enable() {
        super.enable();
        listener = new DefaultTabListListener(this);
        Domination.getPlugin().getServer().getPluginManager().registerEvents(listener, Domination.getPlugin());

        Domination.getPlugin().getServer().getOnlinePlayers().forEach(this::addPlayer);

        // Because there is no PlayerListNameUpdateEvent in Bukkit
       this.task = Domination.getPlugin().getServer().getScheduler().runTaskTimer(Domination.getPlugin(), () -> {
        	for (Player target : Domination.getPlugin().getServer().getOnlinePlayers()) {
				if (!names.containsKey(target.getUniqueId()))
					continue;

				String prevName = names.get(target.getUniqueId());
				String currName = target.getPlayerListName();

				if (prevName.equals(currName))
					continue;

				int index = getTabItemIndex(target);
				update(index);
				names.put(target.getUniqueId(), currName);
			}
        }, 0L, 5L);
    }

    @Override
    public void disable() {
        super.disable();
        HandlerList.unregisterAll(listener);
        this.task.cancel();
        listener = null; // prevents memory leaks
    }

    void addPlayer(Player player) {
        add(getInsertLocation(player), new PlayerTabItem(player));
        this.names.put(player.getUniqueId(), player.getPlayerListName());
    }

    int getTabItemIndex(Player player) {
        for (Entry<Integer, TabItem> item : this.items.entrySet()) {
            // items will always be players in this case, cast is safe
            PlayerTabItem tabItem = (PlayerTabItem) item.getValue();
            if (tabItem.getPlayer().equals(player))
                return item.getKey();
        }
        return -1;
    }

    private int getInsertLocation(Player player) {
        for (Entry<Integer,TabItem> item : this.items.entrySet()) {
            // items will always be players in this case, cast is safe
            PlayerTabItem tabItem = (PlayerTabItem) item.getValue();

            if (player.getName().compareTo(tabItem.getPlayer().getName()) < 0)
                return item.getKey();
        }
        return getNextIndex();
    }
}
