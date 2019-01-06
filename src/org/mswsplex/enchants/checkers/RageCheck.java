package org.mswsplex.enchants.checkers;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.msws.CustomEnchants;

public class RageCheck implements Listener {

	private CustomEnchants plugin;

	private HashMap<LivingEntity, Integer> rage;

	public RageCheck(CustomEnchants plugin) {
		this.plugin = plugin;
		rage = new HashMap<>();
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
		if (!hand.containsEnchantment(plugin.getEnchantmentManager().enchants.get("rage")))
			return;
		if (rage.containsKey(ent)) {
			event.setDamage(event.getDamage() * Math.pow(1.1, rage.get(ent)));
			rage.put((LivingEntity) ent, rage.get(ent) + 1);
		} else {
			rage.put((LivingEntity) ent, 1);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		Entity ent = event.getEntity();
		rage.remove(ent);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity().getKiller() == null)
			return;
		rage.remove(event.getEntity().getKiller());
	}
}
