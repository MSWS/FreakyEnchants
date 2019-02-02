package org.mswsplex.enchants.checkers.armor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.msws.FreakyEnchants;

public class SoftTouchCheck implements Listener {
	private FreakyEnchants plugin;

	public SoftTouchCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onMove(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player.getEquipment() == null || player.getEquipment().getBoots() == null)
			return;
		ItemStack armor = player.getEquipment().getBoots();
		if (!plugin.getEnchManager().containsEnchantment(armor, "softtouch"))
			return;
		if (event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL) {
			event.setCancelled(true);
		}
	}
}
