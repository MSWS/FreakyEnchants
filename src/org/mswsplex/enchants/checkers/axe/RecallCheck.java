package org.mswsplex.enchants.checkers.axe;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.Utils;

public class RecallCheck implements Listener {

	private FreakyEnchants plugin;

	@SuppressWarnings("deprecation")
	public RecallCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, runParticles(), 0, 1);
	}

	public BukkitRunnable runParticles() {
		return new BukkitRunnable() {
			@Override
			public void run() {
				for (World w : Bukkit.getWorlds()) {
					for (Item item : w.getEntitiesByClass(Item.class)) {
						if (!item.hasMetadata("chuckThrower") || !item.isOnGround())
							continue;
						Player thrower = Bukkit
								.getPlayer(UUID.fromString(item.getMetadata("chuckThrower").get(0).asString()));
						if (thrower == null) {
							item.removeMetadata("chuckThrower", plugin);
							continue;
						}

						if (!plugin.getEnchManager().containsEnchantment(item.getItemStack(), "recall")) {
							item.removeMetadata("chuckThrower", plugin);
							continue;
						}

						if (getTotalVelocity(item.getVelocity()) > .01)
							continue;

						Utils.playSound(plugin.config, "Recall.RecallSound", thrower.getLocation());
						thrower.getInventory().addItem(item.getItemStack());
						item.remove();
					}
				}
			}
		};
	}

	private double getTotalVelocity(Vector v) {
		return Math.abs(v.getX()) + Math.abs(v.getY()) + Math.abs(v.getZ());
	}
}
