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
							for (String eName : new String[] { "Spring", "Speed", "HeatShield" }) {
								if (armor.containsEnchantment(
										plugin.getEnchantmentManager().enchants.get(eName.toLowerCase()))
										&& Utils.allowEnchant(w, eName.toLowerCase()))
									ent.addPotionEffect(new PotionEffect(
											PotionEffectType.getByName(plugin.config.getString(eName + ".EffectType")),
											20,
											plugin.getEnchantmentManager().checkAmplifier(eName,
													armor.getEnchantmentLevel(plugin.getEnchantmentManager().enchants
															.get(eName.toLowerCase())))));
							}
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
