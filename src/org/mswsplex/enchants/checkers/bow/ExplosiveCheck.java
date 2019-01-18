package org.mswsplex.enchants.checkers.bow;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.Utils;

public class ExplosiveCheck implements Listener {
	private FreakyEnchants plugin;

	public ExplosiveCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onProjectileLand(ProjectileHitEvent event) {
		if (!event.getEntity().hasMetadata("explosiveArrow"))
			return;
		event.getEntity().getWorld().createExplosion(event.getEntity().getLocation(), 1);
		event.getEntity().remove();
	}

	@EventHandler(ignoreCancelled = true)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if (!Utils.allowEnchant(event.getEntity().getWorld(), "explosive"))
			return;
		Projectile proj = event.getEntity();
		if (proj == null || proj.getShooter() == null || !(proj.getShooter() instanceof LivingEntity))
			return;
		LivingEntity ent = (LivingEntity) proj.getShooter();
		ItemStack hand = ent.getEquipment().getItemInHand();
		if (!hand.containsEnchantment(plugin.getEnchantmentManager().enchants.get("explosive")))
			return;
		proj.setMetadata("explosiveArrow", new FixedMetadataValue(plugin, true));
	}
}
