package org.mswsplex.enchants.checkers.bow;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.mswsplex.enchants.managers.CPlayer;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Utils;

public class ElectricCheck implements Listener {

	private FreakyEnchants plugin;

	public ElectricCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityHit(ProjectileHitEvent event) {
		if (event.getEntity() == null || !event.getEntity().hasMetadata("electricArrow")
				|| event.getEntity().getShooter() == null || !(event.getEntity().getShooter() instanceof Player))
			return;
		Player player = (Player) event.getEntity().getShooter();
		if (!Utils.allowEnchant(player.getWorld(), "electric"))
			return;
		event.getEntity().getWorld().strikeLightning(event.getEntity().getLocation());
		if (plugin.config.getBoolean("Electric.DeleteArrow"))
			event.getEntity().remove();
	}

	@EventHandler
	public void onHotbarSwap(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		cp.removeTempData("electricArrowLoaded");
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (!Utils.allowEnchant(player.getWorld(), "electric"))
			return;
		ItemStack hand = player.getItemInHand();
		if (hand == null || hand.getType() != Material.BOW)
			return;
		if (event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
		if (!plugin.getEnchManager().containsEnchantment(hand, "electric"))
			return;
		if (!(player.getInventory().contains(Material.ARROW) || player.getGameMode() == GameMode.CREATIVE))
			return;
		if (cp.hasTempData("electricArrowLoaded") && (boolean) cp.getTempData("electricArrowLoaded"))
			return;
		if (!cp.hasSaveData("electric") || System.currentTimeMillis() - cp.getSaveDouble("electric") > plugin
				.getEnchManager().getBonusAmount("electric", hand.getEnchantmentLevel(plugin.getEnchant("electric")))) {
			MSG.sendStatusMessage(player, plugin.config.getString("Electric.Prepared"));
			Utils.playSound(plugin.config, "Electric.PrepareSound", player);
			cp.setTempData("electricArrowLoaded", true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile proj = event.getEntity();
		if (proj == null || proj.getShooter() == null || !(proj.getShooter() instanceof Player))
			return;
		Player player = (Player) proj.getShooter();
		CPlayer cp = plugin.getCPlayer(player);
		ItemStack hand = player.getItemInHand();

		if (!cp.hasTempData("electricArrowLoaded") || !(boolean) cp.getTempData("electricArrowLoaded"))
			return;

		cp.removeTempData("electricArrowLoaded");

		proj.setMetadata("electricArrow", new FixedMetadataValue(plugin, true));
		cp.setSaveData("electric", (double) System.currentTimeMillis());
		MSG.sendTimedHotbar(player, "Electric", hand.getEnchantmentLevel(plugin.getEnchant("electric")));
	}
}
