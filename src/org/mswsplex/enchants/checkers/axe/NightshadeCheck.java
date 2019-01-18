package org.mswsplex.enchants.checkers.axe;

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

public class NightshadeCheck implements Listener {

	private FreakyEnchants plugin;

	public NightshadeCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity ent = event.getDamager();
		if (!Utils.allowEnchant(ent.getWorld(), "nightshade"))
			return;
		if (!(ent instanceof LivingEntity) || !(event.getEntity() instanceof LivingEntity))
			return;
		ItemStack hand = ((LivingEntity) ent).getEquipment().getItemInHand();
		if (hand == null || hand.getType() == Material.AIR)
			return;
		if (!hand.containsEnchantment(plugin.getEnchantmentManager().enchants.get("nightshade")))
			return;
		String name = plugin.config.contains("Nightshade.Affects." + event.getEntity().getType())
				? event.getEntity().getType() + ""
				: "Generic";
		if (!plugin.config.getBoolean("Nightshade.Affects." + name))
			return;
		if (!plugin.getEnchantmentManager().checkProbability("Nightshade",
				hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("nightshade"))))
			return;
		LivingEntity target = (LivingEntity) event.getEntity();
		PotionEffect effect = new PotionEffect(
				PotionEffectType.getByName(plugin.config.getString("Nightshade.EffectType")),
				hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("nightshade"))
						* plugin.config.getInt("Nightshade.SecondsPerLevel") / 1000 * 20,
				plugin.getEnchantmentManager().checkAmplifier("nightshade",
						hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("nightshade"))));
		target.addPotionEffect(effect);
		if (ent instanceof Player)
			MSG.sendStatusMessage((Player) ent, plugin.config.getString("Nightshade.SuccessMessage"));
	}
}
