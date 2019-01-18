package org.mswsplex.enchants.checkers.sword;

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

public class FreezeCheck implements Listener {

	private FreakyEnchants plugin;

	public FreezeCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity ent = event.getDamager();
		if (!Utils.allowEnchant(ent.getWorld(), "freeze"))
			return;
		if (!(ent instanceof LivingEntity) || !(event.getEntity() instanceof LivingEntity))
			return;
		ItemStack hand = ((LivingEntity) ent).getEquipment().getItemInHand();
		if (hand == null || hand.getType() == Material.AIR)
			return;
		if (!hand.containsEnchantment(plugin.getEnchantmentManager().enchants.get("freeze")))
			return;
		String name = plugin.config.contains("Freeze.Affects." + event.getEntity().getType())
				? event.getEntity().getType() + ""
				: "Generic";
		if (!plugin.config.getBoolean("Freeze.Affects." + name))
			return;
		if (!plugin.getEnchantmentManager().checkProbability("freeze",
				hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("freeze"))))
			return;
		LivingEntity target = (LivingEntity) event.getEntity();
		PotionEffect effect = new PotionEffect(PotionEffectType.getByName(plugin.config.getString("Freeze.EffectType")),
				hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("freeze"))
						* plugin.config.getInt("Freeze.SecondsPerLevel") / 1000 * 20,
				plugin.getEnchantmentManager().checkAmplifier("freeze",
						hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("freeze"))));
		target.addPotionEffect(effect);
		if (ent instanceof Player)
			MSG.sendStatusMessage((Player) ent, plugin.config.getString("Freeze.SuccessMessage"));
	}
}
