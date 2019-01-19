package org.mswsplex.enchants.checkers.sword;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.Utils;

public class RageCheck implements Listener {

	private FreakyEnchants plugin;

	private HashMap<LivingEntity, Integer> rage;

	public RageCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		rage = new HashMap<>();
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity ent = event.getDamager();
		if (!Utils.allowEnchant(ent.getWorld(), "rage"))
			return;
		if (!(ent instanceof LivingEntity) || !(event.getEntity() instanceof LivingEntity))
			return;
		ItemStack hand = ((LivingEntity) ent).getEquipment().getItemInHand();
		if (hand == null || hand.getType() == Material.AIR)
			return;
		Enchantment ench = plugin.getEnchant("rage");
		if (!hand.containsEnchantment(ench))
			return;
		if (rage.containsKey(ent)) {
			event.setDamage(event.getDamage()
					* Math.pow(plugin.getEnchManager().getDouble("rage", "Multiplier", hand.getEnchantmentLevel(ench)),
							rage.get(ent)));
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
		if (event.getEntity().getKiller() == null || !plugin.config.getBoolean("Rage.ResetAfterKill"))
			return;
		rage.remove(event.getEntity().getKiller());
	}
}
