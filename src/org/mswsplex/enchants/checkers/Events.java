package org.mswsplex.def.events;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.mswsplex.def.msws.Main;

public class Events implements Listener{
	private Main plugin;
	public Events(Main plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}
}
