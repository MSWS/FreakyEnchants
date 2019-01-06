package org.mswsplex.enchants.checkers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.msws.CustomEnchants;

public class ExcavationCheck implements Listener {

	private CustomEnchants plugin;

	public ExcavationCheck(CustomEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack hand = player.getItemInHand();
		if (hand == null || hand.getType() == Material.AIR)
			return;
		if (!hand.containsEnchantment(plugin.getEnchantmentManager().enchants.get("excavation")))
			return;
		int lv = hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("excavation"));
		for (int x = -lv / 2; x <= lv / 2; x++) {
			for (int y = -lv / 2; y <= lv / 2; y++) {
				for (int z = -lv / 2; z <= lv / 2; z++) {
					event.getBlock().getLocation().add(x, y, z).getBlock().breakNaturally();
				}
			}
		}
	}
}
