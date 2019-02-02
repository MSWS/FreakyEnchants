package org.mswsplex.enchants.checkers.pickaxe;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.Utils;

public class ExplosionCheck implements Listener {

	private FreakyEnchants plugin;

	public ExplosionCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!Utils.allowEnchant(player.getWorld(), "explosion"))
			return;
		ItemStack hand = player.getItemInHand();
		if (hand == null || hand.getType() == Material.AIR)
			return;
		if (!plugin.getEnchManager().containsEnchantment(hand, "explosion"))
			return;
		event.getBlock().getWorld().createExplosion(event.getBlock().getLocation(),
				hand.getEnchantmentLevel(plugin.getEnchant("explosion")));
	}
}
