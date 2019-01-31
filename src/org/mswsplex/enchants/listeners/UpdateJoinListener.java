package org.mswsplex.enchants.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Utils;

public class UpdateJoinListener implements Listener {
	private FreakyEnchants plugin;

	public UpdateJoinListener(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!player.hasPermission("freakyenchants.autoupdater") || !plugin.config.getBoolean("Updater.OnJoin"))
			return;
		String online = plugin.getOnlineVer();
		if (online == null) {
			MSG.tell(player, MSG.getString("Outdated.Error", "unable to grab online version"));
			return;
		}
		if (Utils.outdated(plugin.getDescription().getVersion(), online)) {
			MSG.tell(player, MSG.getString("Outdated.InGame", "FreakyEnchants is outdated %ver%/%oVer%")
					.replace("%ver%", plugin.getDescription().getVersion()).replace("%oVer%", online));
		}

		if (!player.hasPermission("freakyenchants.changelog") || !plugin.config.getBoolean("Changelog.OnJoin"))
			return;

		plugin.getChangelog().forEach((line) -> {
			MSG.tell(player, line);
		});

	}

}
