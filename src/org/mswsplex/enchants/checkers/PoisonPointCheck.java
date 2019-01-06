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
import org.mswsplex.enchants.msws.CustomEnchants;
import org.mswsplex.enchants.utils.MSG;

public class PoisonPointCheck implements Listener {

	private CustomEnchants plugin;

	public PoisonPointCheck(CustomEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity ent = event.getDamager();

		if (!(ent instanceof LivingEntity) || !(event.getEntity() instanceof LivingEntity))
			return;
		ItemStack hand = ((LivingEntity) ent).getEquipment().getItemInHand();
		if (hand == null || hand.getType() == Material.AIR)
			return;
		if (!hand.containsEnchantment(plugin.getEnchantmentManager().enchants.get("poisonpoint")))
			return;
		String name = plugin.config.contains("PoisonPoint.Affects." + event.getEntity().getType())
				? event.getEntity().getType() + ""
				: "Generic";
		if (!plugin.config.getBoolean("PoisonPoint.Affects." + name))
			return;
		if (!plugin.getEnchantmentManager().checkProbability("poisonpoint",
				hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("poisonpoint"))))
			return;
		LivingEntity target = (LivingEntity) event.getEntity();
		PotionEffect effect = new PotionEffect(
				PotionEffectType.getByName(plugin.config.getString("PoisonPoint.EffectType")),
				hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("poisonpoint"))
						* plugin.config.getInt("PoisonPoint.SecondsPerLevel") / 1000 * 20,
				plugin.getEnchantmentManager().checkAmplifier("poinsonpoint",
						hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("poisonpoint"))));
		target.addPotionEffect(effect);
		if (ent instanceof Player)
			MSG.sendStatusMessage((Player) ent, plugin.config.getString("PoisonPoint.SuccessMessage"));
	}
}
