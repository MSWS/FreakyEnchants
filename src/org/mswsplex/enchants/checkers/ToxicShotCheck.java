package org.mswsplex.enchants.checkers;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mswsplex.enchants.msws.CustomEnchants;

public class ToxicShotCheck implements Listener {

	private CustomEnchants plugin;

	public ToxicShotCheck(CustomEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onEntityHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() == null || event.getDamager() == null || !event.getDamager().hasMetadata("toxicArrow"))
			return;
		if (!(event.getEntity() instanceof LivingEntity))
			return;
		LivingEntity ent = (LivingEntity) event.getEntity();
		double duration = event.getDamager().getMetadata("toxicArrow").get(0).asDouble();
		ent.addPotionEffect(
				new PotionEffect(PotionEffectType.getByName(plugin.config.getString("ToxicShot.EffectType")),
						(int) duration / 1000 * 20, event.getDamager().getMetadata("toxicAmplifier").get(0).asInt()));
	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile proj = event.getEntity();
		if (proj == null || proj.getShooter() == null || !(proj.getShooter() instanceof LivingEntity))
			return;
		LivingEntity ent = (LivingEntity) proj.getShooter();
		ItemStack hand = ent.getEquipment().getItemInHand();
		if (!hand.containsEnchantment(plugin.getEnchantmentManager().enchants.get("toxicshot")))
			return;
		proj.setMetadata("toxicAmplifier",
				new FixedMetadataValue(plugin, plugin.getEnchantmentManager().checkAmplifier("ToxicShot",
						hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("toxicshot")))));
		proj.setMetadata("toxicArrow",
				new FixedMetadataValue(plugin, plugin.config.getDouble("ToxicShot.SecondsPerLevel")
						* hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("toxicshot"))));
	}
}
