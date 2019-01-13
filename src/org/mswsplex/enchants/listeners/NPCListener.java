package org.mswsplex.enchants.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.mswsplex.enchants.managers.PlayerManager;
import org.mswsplex.enchants.msws.CustomEnchants;
import org.mswsplex.enchants.utils.Sounds;
import org.mswsplex.enchants.utils.Utils;

public class NPCListener implements Listener {
	private CustomEnchants plugin;

	public NPCListener(CustomEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onInteract(PlayerInteractAtEntityEvent event) {
		Player player = event.getPlayer();
		Entity clicked = event.getRightClicked();
		if (clicked.hasMetadata("isNPC")) {
			event.setCancelled(true);
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
				player.openInventory(Utils.getGui(player, "MainMenu", 0));
				player.playSound(player.getLocation(),
						Sounds.valueOf(plugin.config.getString("Sounds.OpenEnchantmentInventory.Name")).bukkitSound(),
						(float) plugin.config.getDouble("Sounds.OpenEnchantmentInventory.Volume"),
						(float) plugin.config.getDouble("Sounds.OpenEnchantmentInventory.Pitch"));
				PlayerManager.setInfo(player, "openInventory", "MainMenu");
			}, 1);
		}
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (!event.getEntity().hasMetadata("isNPC") || !(event.getDamager() instanceof Player))
			return;
		Player player = (Player) event.getDamager();
		player.openInventory(Utils.getGui(player, "MainMenu", 0));
		player.playSound(player.getLocation(), Sound.ANVIL_USE, 1, 2);
		PlayerManager.setInfo(player, "openInventory", "MainMenu");
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity().hasMetadata("isNPC"))
			event.setCancelled(true);
	}
}
