package org.mswsplex.enchants.checkers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.msws.CustomEnchants;
import org.mswsplex.enchants.utils.MSG;

public class SummonerCheck implements Listener {

	private CustomEnchants plugin;

	public SummonerCheck(CustomEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity ent = event.getEntity();
		if (!(ent instanceof LivingEntity) || !(event.getDamager() instanceof LivingEntity))
			return;
		for (ItemStack hand : ((LivingEntity) ent).getEquipment().getArmorContents()) {
			if (hand == null || hand.getType() == Material.AIR)
				continue;
			if (!hand.containsEnchantment(plugin.getEnchantmentManager().enchants.get("summoner")))
				continue;
			if (!plugin.getEnchantmentManager().checkProbability("summoner",
					hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("summoner"))))
				continue;
			for (int i = 0; i < plugin.getEnchantmentManager().getBonusAmount("summoner",
					hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("summoner"))); i++) {
				Entity t = ent.getWorld().spawnEntity(ent.getLocation(),
						EntityType.valueOf(plugin.config.getString("Summoner.EntityType")));
				if (t instanceof Creature)
					((Creature) t).setTarget((LivingEntity) event.getDamager());
			}
			if (ent instanceof Player)
				MSG.sendStatusMessage((Player) ent, plugin.config.getString("Summoner.SuccessMessage"));
		}

	}
}
