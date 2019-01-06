package org.mswsplex.enchants.checkers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.enchants.msws.CustomEnchants;
import org.mswsplex.enchants.utils.Utils;

public class ArmorChecker extends BukkitRunnable {

	private CustomEnchants plugin;

	public ArmorChecker(CustomEnchants plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		for (World w : Bukkit.getWorlds()) {
			for (LivingEntity ent : w.getEntitiesByClass(LivingEntity.class)) {
				EntityEquipment equipment = ent.getEquipment();
				double maxHealth = 20.0;
				if (!hasArmor(equipment.getArmorContents())) {
					ent.setMaxHealth(maxHealth);
					continue;
				}
				for (ItemStack armor : equipment.getArmorContents()) {
					if (armor.containsEnchantment(plugin.getEnchantmentManager().enchants.get("hearty"))) {
						maxHealth += plugin.getEnchantmentManager().getBonusAmount("hearty",
								armor.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("hearty")));
					}
					if (armor.containsEnchantment(plugin.getEnchantmentManager().enchants.get("spring")))
						ent.addPotionEffect(new PotionEffect(
								PotionEffectType.getByName(plugin.config.getString("Spring.EffectType")), 5,
								plugin.getEnchantmentManager().checkAmplifier("spring", armor
										.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("spring")))));
					if (armor.containsEnchantment(plugin.getEnchantmentManager().enchants.get("heatshield")))
						ent.addPotionEffect(new PotionEffect(
								PotionEffectType.getByName(plugin.config.getString("HeatShield.EffectType")), 5,
								plugin.getEnchantmentManager().checkAmplifier("heatshield", armor.getEnchantmentLevel(
										plugin.getEnchantmentManager().enchants.get("heatshield")))));
				}
				ent.setMaxHealth(maxHealth);
			}
		}
	}

	boolean hasArmor(ItemStack[] armor) {
		for (ItemStack item : armor)
			if (item != null && Utils.isArmor(item.getType()))
				return true;
		return false;
	}

}
