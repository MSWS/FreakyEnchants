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
import org.mswsplex.enchants.msws.CustomEnchants;
import org.mswsplex.enchants.utils.MSG;

public class StormbreakerCheck implements Listener {

	private CustomEnchants plugin;

	public StormbreakerCheck(CustomEnchants plugin) {
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
		if (!hand.containsEnchantment(plugin.getEnchantmentManager().enchants.get("stormbreaker")))
			return;
		if (!plugin.getEnchantmentManager().checkProbability("stormbreaker",
				hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("stormbreaker"))))
			return;
		event.getEntity().getWorld().strikeLightning(event.getEntity().getLocation());
		if (ent instanceof Player)
			MSG.sendStatusMessage((Player) ent, plugin.config.getString("StormBreaker.SuccessMessage"));
	}
}
