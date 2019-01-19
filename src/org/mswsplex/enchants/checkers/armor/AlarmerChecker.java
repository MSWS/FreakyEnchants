package org.mswsplex.enchants.checkers.armor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.Sounds;

public class AlarmerChecker {

	private FreakyEnchants plugin;

	@SuppressWarnings("deprecation")
	public AlarmerChecker(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, run(), 0,
				plugin.config.getInt("Alarmer.PlayEvery"));
	}

	public BukkitRunnable run() {
		return new BukkitRunnable() {
			@Override
			public void run() {
				Enchantment ench = plugin.getEnchant("alarmer");
				for (Player target : Bukkit.getOnlinePlayers()) {
					ItemStack helmet = target.getEquipment().getHelmet();
					if (helmet == null || helmet.getType() == Material.AIR || !helmet.containsEnchantment(ench))
						continue;
					double radius = plugin.getEnchManager().getDouble("alarmer", "Range",
							helmet.getEnchantmentLevel(ench));
					for (Entity ent : target.getNearbyEntities(radius, radius, radius)) {
						if (ent.isDead())
							continue;
						if (ent instanceof Creature) {
							if (((Creature) ent).getTarget() == null || !((Creature) ent).getTarget().equals(target))
								continue;
						} else if (!(ent instanceof Player))
							continue;
						target.playSound(ent.getLocation(),
								Sounds.valueOf(plugin.config.getString("Alarmer.Sound")).bukkitSound(),
								(float) plugin.config.getDouble("Alarmer.Volume"),
								(float) ((radius / 2.0f) / ent.getLocation().distance(target.getLocation())));
					}
				}
			}
		};
	}
}
