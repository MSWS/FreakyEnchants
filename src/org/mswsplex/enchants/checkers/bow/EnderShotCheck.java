package org.mswsplex.enchants.checkers.bow;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.mswsplex.enchants.managers.PlayerManager;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Utils;

public class EnderShotCheck implements Listener {

	private FreakyEnchants plugin;

	public EnderShotCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityHit(ProjectileHitEvent event) {
		if (event.getEntity() == null || !event.getEntity().hasMetadata("enderArrow")
				|| event.getEntity().getShooter() == null || !(event.getEntity().getShooter() instanceof Player))
			return;
		Player player = (Player) event.getEntity().getShooter();
		if (!Utils.allowEnchant(player.getWorld(), "endershot"))
			return;
		player.teleport(event.getEntity(), TeleportCause.ENDER_PEARL);
		Utils.playSound(plugin.config, "EnderShot.TeleportSound", player.getLocation());
		if (plugin.config.getBoolean("EnderShot.DeleteArrow"))
			event.getEntity().remove();
	}

	@EventHandler(ignoreCancelled = true)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile proj = event.getEntity();
		if (proj == null || proj.getShooter() == null || !(proj.getShooter() instanceof Player))
			return;
		Player player = (Player) proj.getShooter();
		ItemStack hand = player.getItemInHand();
		if (!hand.containsEnchantment(plugin.getEnchant("endershot")))
			return;
		if (System.currentTimeMillis() - PlayerManager.getDouble(player, "endershot") > plugin.getEnchManager()
				.getBonusAmount("endershot", hand.getEnchantmentLevel(plugin.getEnchant("endershot")))
				|| PlayerManager.getDouble(player, "endershot") == 0) {
			proj.setMetadata("enderArrow", new FixedMetadataValue(plugin, true));
			PlayerManager.setInfo(player, "endershot", (double) System.currentTimeMillis());
			MSG.sendTimedHotbar(player, "EnderShot", hand.getEnchantmentLevel(plugin.getEnchant("endershot")));
		}
	}
}
