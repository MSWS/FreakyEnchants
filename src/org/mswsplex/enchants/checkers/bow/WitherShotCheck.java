package org.mswsplex.enchants.checkers.bow;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Utils;

public class WitherShotCheck implements Listener {

	private FreakyEnchants plugin;

	public WitherShotCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() == null || event.getDamager() == null || !event.getDamager().hasMetadata("witherArrow"))
			return;
		if (!(event.getEntity() instanceof LivingEntity))
			return;
		LivingEntity ent = (LivingEntity) event.getEntity();
		if (!Utils.allowEnchant(ent.getWorld(), "withershot"))
			return;
		double duration = event.getDamager().getMetadata("witherArrow").get(0).asDouble();
		ent.addPotionEffect(
				new PotionEffect(PotionEffectType.getByName(plugin.config.getString("WitherShot.EffectType")),
						(int) duration / 1000 * 20, event.getDamager().getMetadata("witherAmplifier").get(0).asInt()));
		if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
			MSG.sendStatusMessage((Player) ((Projectile) event.getDamager()).getShooter(),
					plugin.config.getString("WitherShot.SuccessMessage"));
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile proj = event.getEntity();
		if (proj == null || proj.getShooter() == null || !(proj.getShooter() instanceof LivingEntity))
			return;
		LivingEntity ent = (LivingEntity) proj.getShooter();
		ItemStack hand = ent.getEquipment().getItemInHand();
		if (!hand.containsEnchantment(plugin.getEnchantmentManager().enchants.get("withershot")))
			return;
		if (!plugin.getEnchantmentManager().checkProbability("withershot",
				hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("withershot"))))
			return;
		proj.setMetadata("witherAmplifier",
				new FixedMetadataValue(plugin, plugin.getEnchantmentManager().checkAmplifier("WitherShot",
						hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("withershot")))));
		proj.setMetadata("witherArrow",
				new FixedMetadataValue(plugin, plugin.config.getDouble("WitherShot.SecondsPerLevel")
						* hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("withershot"))));
	}
}
