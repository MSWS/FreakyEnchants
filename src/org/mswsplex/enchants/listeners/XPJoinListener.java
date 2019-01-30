package org.mswsplex.enchants.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.mswsplex.enchants.managers.CPlayer;
import org.mswsplex.enchants.msws.FreakyEnchants;

public class XPJoinListener implements Listener {
	private FreakyEnchants plugin;

	public XPJoinListener(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		player.setLevel((int) Math.floor(cp.getBalance()));
		player.setExp((float) (cp.getBalance() % 1));
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		cp.setSaveData("xp", (double) player.getLevel() + player.getExp());
	}

}
