package org.mswsplex.enchants.checkers.pickaxe;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.Cuboid;
import org.mswsplex.enchants.utils.Sounds;
import org.mswsplex.enchants.utils.Utils;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class ExcavationCheck implements Listener {
	private FreakyEnchants plugin;

	public ExcavationCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!Utils.allowEnchant(player.getWorld(), "excavation"))
			return;
		if (player.getGameMode() == GameMode.CREATIVE && !plugin.config.getBoolean("Excavation.AllowCreative"))
			return;
		ItemStack hand = player.getItemInHand();
		if (hand == null || hand.getType() == Material.AIR) {
			return;
		}
		if (!plugin.getEnchManager().containsEnchantment(hand, "excavation"))
			return;
		int lv = (int) plugin.getEnchManager().getBonusAmount("excavation",
				hand.getEnchantmentLevel(plugin.getEnchant("excavation")));

		WorldGuardPlugin wg = null;
		if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
			wg = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
		}

		boolean autosmelt = hand.containsEnchantment(this.plugin.getEnchant("autosmelt"));

		boolean autograb = hand.containsEnchantment(this.plugin.getEnchant("autograb"));
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
			for (int i = 0; i < plugin.config.getInt("Excavation.MaxBlocks") && pos[0] < cube.getBlocks().size(); i++) {
				Block b = cube.getBlocks().get(pos[0]);
				if (b.isLiquid() || b.getType() == Material.AIR) {
					pos[0]++;
					continue;
				}
				if (fwg != null && !fwg.canBuild(player, b)) {
					pos[0]++;
					continue;
				}
				if (plugin.config.getStringList("Excavation.DontBreak").contains(b.getType().toString())) {
					pos[0]++;
					continue;
				}
				if (plugin.config.getBoolean("Excavation.PlayEffect"))
					b.getWorld().playEffect(b.getLocation(), Effect.TILE_BREAK, 1);
				b.getWorld().playSound(b.getLocation(),
						Sounds.valueOf((Utils.getBreakSound(b.getType()) + "")).bukkitSound(), .5f, 1);
				if (autosmelt) {
					if (autograb) {
						for (ItemStack item : b.getDrops()) {
							player.getInventory().addItem(replaceAuto(item));
						}
					} else {
						for (ItemStack item : b.getDrops()) {
							b.getWorld().dropItem(b.getLocation(), replaceAuto(item));
						}
					}
					b.setType(Material.AIR);
				} else {
					if (autograb) {
						for (ItemStack item : b.getDrops()) {
							player.getInventory().addItem(item);
						}
						b.setType(Material.AIR);
					} else {
						b.breakNaturally();
					}
				}
				pos[0]++;
			}
		}, 0, plugin.config.getInt("Excavation.IteratePer"));
		if (autograb && player.getInventory().firstEmpty() == -1)
			Utils.emptyInventory(player);
		event.setCancelled(true);
	}

	private ItemStack replaceAuto(final ItemStack item) {
		final ConfigurationSection reps = this.plugin.config.getConfigurationSection("AutoSmeltDrops");
		if (reps.contains(new StringBuilder().append(item.getType()).toString())) {
			item.setType(Material.valueOf(reps.getString(new StringBuilder().append(item.getType()).toString())));
		}
		return item;
	}
}