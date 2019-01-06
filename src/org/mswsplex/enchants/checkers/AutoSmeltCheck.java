package org.mswsplex.enchants.checkers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.msws.CustomEnchants;

public class AutoSmeltCheck implements Listener {

	private CustomEnchants plugin;

	public AutoSmeltCheck(CustomEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		ItemStack hand = player.getItemInHand();
		if (hand == null || hand.getType() == Material.AIR)
			return;
		if (!hand.containsEnchantment(plugin.getEnchantmentManager().enchants.get("autosmelt")))
			return;
		for (ItemStack item : event.getBlock().getDrops()) {
			ItemStack replace = item;
			ConfigurationSection reps = plugin.config.getConfigurationSection("AutoSmeltDrops");
			if (reps.contains(item.getType() + ""))
				replace.setType(Material.valueOf(reps.getString(item.getType() + "")));
			event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), replace);
		}
		event.getBlock().setType(Material.AIR);
	}
}
