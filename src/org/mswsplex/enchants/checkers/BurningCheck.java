package org.mswsplex.enchants.checkers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Utils;

public class BurningCheck implements Listener {

	private FreakyEnchants plugin;

	public BurningCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity ent = event.getEntity();
		if (!(ent instanceof LivingEntity) || !(event.getDamager() instanceof LivingEntity))
			return;
		if (!Utils.allowEnchant(ent.getWorld(), "burning"))
			return;
		Enchantment burn = plugin.getEnchantmentManager().enchants.get("burning");
		for (ItemStack hand : ((LivingEntity) ent).getEquipment().getArmorContents()) {
			if (hand == null || hand.getType() == Material.AIR)
				continue;
			if (!hand.containsEnchantment(burn))
				continue;
			if (!plugin.getEnchantmentManager().checkProbability("burning", hand.getEnchantmentLevel(burn)))
				continue;
			((LivingEntity) event.getDamager()).setFireTicks(
					(int) plugin.getEnchantmentManager().getBonusAmount("burning", hand.getEnchantmentLevel(burn)));
			if (ent instanceof Player)
				MSG.sendStatusMessage((Player) ent, plugin.config.getString("Burning.SuccessMessage"));
		}
	}
}
