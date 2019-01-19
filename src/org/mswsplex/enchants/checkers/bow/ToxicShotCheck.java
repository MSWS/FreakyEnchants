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

public class ToxicShotCheck implements Listener {

	private FreakyEnchants plugin;

	public ToxicShotCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() == null || event.getDamager() == null || !event.getDamager().hasMetadata("toxicArrow"))
			return;
		if (!(event.getEntity() instanceof LivingEntity))
			return;
		LivingEntity ent = (LivingEntity) event.getEntity();
		if (!Utils.allowEnchant(ent.getWorld(), "toxicshot"))
			return;
		double duration = event.getDamager().getMetadata("toxicArrow").get(0).asDouble();
		ent.addPotionEffect(
				new PotionEffect(PotionEffectType.getByName(plugin.config.getString("ToxicShot.EffectType")),
						(int) duration / 1000 * 20, event.getDamager().getMetadata("toxicAmplifier").get(0).asInt()));
		if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
			MSG.sendStatusMessage((Player) ((Projectile) event.getDamager()).getShooter(),
					plugin.config.getString("ToxicShot.SuccessMessage"));
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile proj = event.getEntity();
		if (proj == null || proj.getShooter() == null || !(proj.getShooter() instanceof LivingEntity))
			return;
		LivingEntity ent = (LivingEntity) proj.getShooter();
		ItemStack hand = ent.getEquipment().getItemInHand();
		if (!hand.containsEnchantment(plugin.getEnchant("toxicshot")))
			return;
		if (!plugin.getEnchManager().checkProbability("toxicshot",
				hand.getEnchantmentLevel(plugin.getEnchant("toxicshot"))))
			return;
		proj.setMetadata("toxicAmplifier", new FixedMetadataValue(plugin, plugin.getEnchManager()
				.checkAmplifier("ToxicShot", hand.getEnchantmentLevel(plugin.getEnchant("toxicshot")))));
		proj.setMetadata("toxicArrow",
				new FixedMetadataValue(plugin, plugin.config.getDouble("ToxicShot.SecondsPerLevel")
						* hand.getEnchantmentLevel(plugin.getEnchant("toxicshot"))));
	}
}
