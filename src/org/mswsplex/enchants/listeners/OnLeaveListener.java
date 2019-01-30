package org.mswsplex.enchants.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.mswsplex.enchants.msws.FreakyEnchants;

public class OnLeaveListener implements Listener {

	private FreakyEnchants plugin;

	public OnLeaveListener(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		plugin.getPlayerManager().removePlayer(event.getPlayer());
	}
}
