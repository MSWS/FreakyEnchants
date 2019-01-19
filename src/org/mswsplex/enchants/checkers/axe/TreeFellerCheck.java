package org.mswsplex.enchants.checkers.axe;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.Utils;

public class TreeFellerCheck implements Listener {

	private FreakyEnchants plugin;

	public TreeFellerCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!Utils.allowEnchant(player.getWorld(), "treefeller"))
			return;
		ItemStack hand = player.getItemInHand();
		if (hand == null || hand.getType() == Material.AIR)
			return;
		if (!hand.containsEnchantment(plugin.getEnchant("treefeller")))
			return;
		if (event.getBlock().getType() != Material.LOG && event.getBlock().getType() != Material.LOG_2)
			return;

		breakTree(event.getBlock(), hand.getEnchantmentLevel(plugin.getEnchant("treefeller")));

	}

	public void breakTree(Block tree, int level) {
		if (tree.getType() != Material.LOG && tree.getType() != Material.LOG_2)
			return;
		boolean leaves = false;
		for (int y = 0; y <= 50; y++) {
			Block b = tree.getLocation().clone().add(0, y, 0).getBlock();
			if (b.getType() == Material.AIR)
				break;
			if (b.getType().toString().contains("LEAVES")) {
				leaves = true;
				break;
			}
		}
		if (!leaves)
			return;
		tree.breakNaturally();
		Utils.playSound(plugin.config, "TreeFeller.Sound", tree.getLocation());

		for (BlockFace face : BlockFace.values()) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
				breakTree(tree.getRelative(face), level);
			}, (long) plugin.getEnchManager().getBonusAmount("treefeller", level));
		}
	}

}
