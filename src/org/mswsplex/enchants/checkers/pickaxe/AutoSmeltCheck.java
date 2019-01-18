package org.mswsplex.enchants.checkers.pickaxe;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.managers.PlayerManager;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.Utils;

public class AutoSmeltCheck implements Listener {

	private FreakyEnchants plugin;

	public AutoSmeltCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!Utils.allowEnchant(player.getWorld(), "autosmelt"))
			return;
		ItemStack hand = player.getItemInHand();
		if (hand == null || hand.getType() == Material.AIR)
			return;
		if (!hand.containsEnchantment(plugin.getEnchantmentManager().enchants.get("autosmelt")))
			return;

		if (hand.containsEnchantment(plugin.getEnchantmentManager().enchants.get("autograb"))) {
			for (ItemStack item : event.getBlock().getDrops()) {
				ItemStack replace = item;
				ConfigurationSection reps = plugin.config.getConfigurationSection("AutoSmeltDrops");
				if (reps.contains(item.getType() + ""))
					replace.setType(Material.valueOf(reps.getString(item.getType() + "")));
				player.getInventory().addItem(replace);
			}
			if (player.getInventory().firstEmpty() == -1)
				PlayerManager.emptyInventory(player);
			return;
		}
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
