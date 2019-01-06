package org.mswsplex.enchants.checkers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.msws.CustomEnchants;

public class ExplosionCheck implements Listener {

	private CustomEnchants plugin;

	public ExplosionCheck(CustomEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack hand = player.getItemInHand();
		if (hand == null || hand.getType() == Material.AIR)
			return;
		if (!hand.containsEnchantment(plugin.getEnchantmentManager().enchants.get("explosion")))
			return;
		event.getBlock().getWorld().createExplosion(event.getBlock().getLocation(),
				hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("explosion")));
	}
}
