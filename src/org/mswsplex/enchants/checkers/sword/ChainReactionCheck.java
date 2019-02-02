package org.mswsplex.enchants.checkers.sword;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.Utils;

public class ChainReactionCheck implements Listener {

	private FreakyEnchants plugin;

	public ChainReactionCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity ent = event.getDamager();
		if (!(event.getDamager() instanceof LivingEntity) || !(event.getEntity() instanceof LivingEntity))
			return;
		if (!Utils.allowEnchant(ent.getWorld(), "chainreaction"))
			return;
		LivingEntity living = (LivingEntity) event.getDamager();
		ItemStack hand = living.getEquipment().getItemInHand();
		if (hand == null || hand.getType() == Material.AIR)
			return;
		Enchantment ench = plugin.getEnchant("chainreaction");
		if (!plugin.getEnchManager().containsEnchantment(hand, ench))
			return;
		double rad = plugin.getEnchManager().getDouble("chainreaction", "Radius", hand.getEnchantmentLevel(ench));
		List<LivingEntity> ents = iterateNearby(event.getEntity(), ent, rad, null);

		for (LivingEntity e : ents) {
			e.damage(plugin.getEnchManager().getDouble("chainreaction", "Damage", hand.getEnchantmentLevel(ench)));
		}
	}

	private List<LivingEntity> iterateNearby(Entity ent, Entity damager, double range, List<LivingEntity> entities) {
		if (entities == null)
			entities = new ArrayList<LivingEntity>();
		for (Entity e : ent.getNearbyEntities(range, range, range)) {
			if (entities.contains(e) || !(ent instanceof LivingEntity) || e.equals(damager))
				continue;

			entities.add((LivingEntity) e);
			if (plugin.config.getBoolean("ChanReaction.Reiterate"))
				entities.addAll(iterateNearby(e, damager, range, entities));
		}
		return entities;
	}
}
