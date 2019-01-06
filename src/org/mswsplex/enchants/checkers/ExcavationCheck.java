package org.mswsplex.enchants.checkers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.mswsplex.enchants.msws.CustomEnchants;
import org.mswsplex.enchants.utils.Cuboid;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class ExcavationCheck implements Listener {
	private CustomEnchants plugin;

	public ExcavationCheck(final CustomEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents((Listener) this, (Plugin) plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBlockBreak(final BlockBreakEvent event) {
		final Player player = event.getPlayer();
		final ItemStack hand = player.getItemInHand();
		if (hand == null || hand.getType() == Material.AIR) {
			return;
		}
		if (!hand.containsEnchantment((Enchantment) this.plugin.getEnchantmentManager().enchants.get("excavation"))) {
			return;
		}
		final int lv = hand
				.getEnchantmentLevel((Enchantment) this.plugin.getEnchantmentManager().enchants.get("excavation"));
		WorldGuardPlugin wg = null;
		if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
			wg = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
		}
		boolean autosmelt = hand
				.containsEnchantment((Enchantment) this.plugin.getEnchantmentManager().enchants.get("autosmelt"));
		Location loc = event.getBlock().getLocation();
		Cuboid cube = new Cuboid(player.getWorld(), loc.getBlockX() - lv / 2, loc.getBlockY() - lv / 2,
				loc.getBlockZ() - lv / 2, loc.getBlockX() + lv / 2, loc.getBlockY() + lv / 2, loc.getBlockZ() + lv / 2);
		final WorldGuardPlugin fwg = wg;
		int[] pos = { 0 };
		int[] id = new int[1];
		id[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			if (pos[0] >= cube.getBlocks().size()) {
				Bukkit.getScheduler().cancelTask(id[0]);
				return;
			}
			for (int i = 0; i < 200 && i + pos[0] < cube.getBlocks().size(); i++) {
				Block b = cube.getBlocks().get(pos[0]);
				if (b.getType() == Material.BEDROCK || b.isLiquid()) {
					pos[0]++;
					continue;
				}
				if (fwg == null || fwg.canBuild(player, b)) {
					if (autosmelt) {
						for (final ItemStack item : b.getDrops()) {
							b.getWorld().dropItem(b.getLocation(), this.replaceAuto(item));
						}
						b.setType(Material.AIR);
					} else {
						b.breakNaturally();
					}
				}
				pos[0]++;
			}

		}, 0, 1);

//		for (int x = -lv / 2; x <= lv / 2; ++x) {
//			for (int y = -lv / 2; y <= lv / 2; ++y) {
//				for (int z = -lv / 2; z <= lv / 2; ++z) {
//					Location l = event.getBlock().getLocation().add((double) x, (double) y, (double) z);
//					if (wg == null || wg.canBuild(player, l)) {
//						MSG.tell((CommandSender) player, "smelt: " + autosmelt);
//						if (autosmelt) {
//							for (final ItemStack item : l.getBlock().getDrops()) {
//								l.getWorld().dropItem(l, this.replaceAuto(item));
//							}
//							l.getBlock().setType(Material.AIR);
//						} else {
//							l.getBlock().breakNaturally();
//						}
//					}
//				}
//			}
//		}
	}

	private ItemStack replaceAuto(final ItemStack item) {
		final ConfigurationSection reps = this.plugin.config.getConfigurationSection("AutoSmeltDrops");
		if (reps.contains(new StringBuilder().append(item.getType()).toString())) {
			item.setType(Material.valueOf(reps.getString(new StringBuilder().append(item.getType()).toString())));
		}
		return item;
	}
}