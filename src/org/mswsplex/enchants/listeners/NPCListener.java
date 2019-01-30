package org.mswsplex.enchants.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.mswsplex.enchants.managers.CPlayer;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.Utils;

public class NPCListener implements Listener {
	private FreakyEnchants plugin;

	public NPCListener(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		Entity clicked = event.getRightClicked();
		if (clicked.hasMetadata("isNPC") && plugin.config.getBoolean("NPC.AllowRightClick")) {
			event.setCancelled(true);
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
				player.openInventory(Utils.getGui(player, "MainMenu", 0));
				Utils.playSound(plugin.config, "Sounds.OpenEnchantmentInventory", player);
				cp.setTempData("openInventory", "MainMenu");
			}, 1);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!event.getEntity().hasMetadata("isNPC") || !(event.getDamager() instanceof Player)
				|| !plugin.config.getBoolean("NPC.AllowLeftClick"))
			return;
		Player player = (Player) event.getDamager();
		CPlayer cp = plugin.getCPlayer(player);
		player.openInventory(Utils.getGui(player, "MainMenu", 0));
		Utils.playSound(plugin.config, "Sounds.OpenEnchantmentInventory", player);
		cp.setTempData("openInventory", "MainMenu");
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity().hasMetadata("isNPC"))
			event.setCancelled(true);
	}
}
