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

public class ArmorChecker {

	private CustomEnchants plugin;

	@SuppressWarnings("deprecation")
	public ArmorChecker(CustomEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, run(), 0, 5);
	}

	public BukkitRunnable run() {
		return new BukkitRunnable() {
			@Override
			public void run() {
				for (World w : Bukkit.getWorlds()) {
					for (LivingEntity ent : w.getEntitiesByClass(LivingEntity.class)) {
						EntityEquipment equipment = ent.getEquipment();
						double maxHealth = 20.0;
						if (!hasArmor(equipment.getArmorContents()) || !Utils.allowEnchant(w, null)) {
							ent.setMaxHealth(maxHealth);
							continue;
						}
						for (ItemStack armor : equipment.getArmorContents()) {
							if (armor.containsEnchantment(plugin.getEnchantmentManager().enchants.get("hearty"))
									&& Utils.allowEnchant(w, "hearty")) {
								maxHealth += plugin.getEnchantmentManager().getBonusAmount("hearty", armor
										.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("hearty")));
							}

							if (armor.containsEnchantment(plugin.getEnchantmentManager().enchants.get("spring"))
									&& Utils.allowEnchant(w, "spring"))
								ent.addPotionEffect(new PotionEffect(
										PotionEffectType.getByName(plugin.config.getString("Spring.EffectType")), 20,
										plugin.getEnchantmentManager().checkAmplifier("spring",
												armor.getEnchantmentLevel(
														plugin.getEnchantmentManager().enchants.get("spring")))));

							if (armor.containsEnchantment(plugin.getEnchantmentManager().enchants.get("heatshield"))
									&& Utils.allowEnchant(w, "heatshield"))
								ent.addPotionEffect(new PotionEffect(
										PotionEffectType.getByName(plugin.config.getString("HeatShield.EffectType")),
										20,
										plugin.getEnchantmentManager().checkAmplifier("heatshield",
												armor.getEnchantmentLevel(
														plugin.getEnchantmentManager().enchants.get("heatshield")))));
						}
						ent.setMaxHealth(maxHealth);
					}
				}
			}
		};
	}

	boolean hasArmor(ItemStack[] armor) {
		for (ItemStack item : armor)
			if (item != null && Utils.isArmor(item.getType()))
				return true;
		return false;
	}

}
