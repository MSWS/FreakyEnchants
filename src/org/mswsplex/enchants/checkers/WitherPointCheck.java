package org.mswsplex.enchants.checkers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Utils;

public class WitherPointCheck implements Listener {

	private FreakyEnchants plugin;

	public WitherPointCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity ent = event.getDamager();
		if (!(event.getDamager() instanceof LivingEntity) || !(event.getEntity() instanceof LivingEntity))
			return;
		if (!Utils.allowEnchant(ent.getWorld(), "witherpoint"))
			return;
		LivingEntity living = (LivingEntity) event.getDamager();
		ItemStack hand = living.getEquipment().getItemInHand();
		if (hand == null || hand.getType() == Material.AIR)
			return;
		if (!hand.containsEnchantment(plugin.getEnchantmentManager().enchants.get("witherpoint")))
			return;
		String name = plugin.config.contains("WitherPoint.Affects." + event.getEntity().getType())
				? event.getEntity().getType() + ""
				: "Generic";
		if (!plugin.config.getBoolean("WitherPoint.Affects." + name))
			return;
		if (!plugin.getEnchantmentManager().checkProbability("witherpoint",
				hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("witherpoint"))))
			return;
		LivingEntity target = (LivingEntity) event.getEntity();
		target.addPotionEffect(
				new PotionEffect(PotionEffectType.getByName(plugin.config.getString("WitherPoint.EffectType")),
						hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("witherpoint"))
								* plugin.config.getInt("WitherPoint.SecondsPerLevel") / 1000 * 20,
						plugin.getEnchantmentManager().checkAmplifier("witherpoint",
								hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("witherpoint")))));
		if (ent instanceof Player)
			MSG.sendStatusMessage((Player) ent, plugin.config.getString("WitherPoint.SuccessMessage"));
	}
}
