package org.mswsplex.enchants.checkers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.msws.CustomEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Utils;

public class ExtraXPCheck implements Listener {

	private CustomEnchants plugin;

	public ExtraXPCheck(CustomEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!Utils.allowEnchant(player.getWorld(), "extraxp"))
			return;
		ItemStack hand = player.getItemInHand();
		if (hand == null || hand.getType() == Material.AIR)
			return;
		if (!hand.containsEnchantment(plugin.getEnchantmentManager().enchants.get("extraxp")))
			return;
		if (!event.getBlock().getType().isSolid() && plugin.getConfig().getBoolean("ExtraXP.MustBeSolid"))
			return;
		int lvl = hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("extraxp"));
		if (!plugin.getEnchantmentManager().checkProbability("extraxp", lvl))
			return;
		if (!plugin.getEnchantmentManager().checkProbability("extraxp", lvl))
			player.giveExp((int) plugin.getEnchantmentManager().getBonusAmount("extraxp", lvl));
		MSG.sendStatusMessage(player, plugin.config.getString("ExtraXP.SuccessMessage"));
	}
}
