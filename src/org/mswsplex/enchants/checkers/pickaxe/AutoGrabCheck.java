package org.mswsplex.enchants.checkers.pickaxe;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.managers.PlayerManager;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.Utils;

public class AutoGrabCheck implements Listener {

	private FreakyEnchants plugin;

	public AutoGrabCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!Utils.allowEnchant(player.getWorld(), "autograb"))
			return;
		ItemStack hand = player.getItemInHand();
		if (player.getGameMode() == GameMode.CREATIVE)
			return;
		if (hand == null || hand.getType() == Material.AIR)
			return;
		if (!hand.containsEnchantment(plugin.getEnchant("autograb")))
			return;
		if (hand.containsEnchantment(plugin.getEnchant("autosmelt")))
			return;
		event.getBlock().getDrops().forEach((item) -> {
			player.getInventory().addItem(item);
		});
		event.getBlock().setType(Material.AIR);
		if (player.getInventory().firstEmpty() == -1)
			PlayerManager.emptyInventory(player);
	}
}
