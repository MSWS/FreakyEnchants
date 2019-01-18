package org.mswsplex.enchants.checkers.pickaxe;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.mswsplex.enchants.managers.PlayerManager;
import org.mswsplex.enchants.managers.TimeManager;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.Cuboid;
import org.mswsplex.enchants.utils.MSG;

public class OreSeekingCheck implements Listener {

	private FreakyEnchants plugin;

	public OreSeekingCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR)
			return;
		Player player = event.getPlayer();
		ItemStack hand = player.getItemInHand();
		if (hand == null || hand.getType() == Material.AIR)
			return;
		Enchantment ench = plugin.getEnchantmentManager().enchants.get("oreseeking");
		if (!hand.containsEnchantment(ench))
			return;
		if (System.currentTimeMillis() - PlayerManager.getDouble(player, "oreseeking") < plugin.getEnchantmentManager()
				.getBonusAmount("oreseeking", hand.getEnchantmentLevel(ench))
				&& PlayerManager.getDouble(player, "oreseeking") != 0) {
			MSG.tell(player, plugin.config.getString("OreSeeking.Delay").replace("%time%",
					TimeManager.getTime(
							plugin.getEnchantmentManager().getBonusAmount("oreseeking", hand.getEnchantmentLevel(ench))
									- (System.currentTimeMillis() - PlayerManager.getDouble(player, "oreseeking")))));
			return;
		}
		List<String> materials = plugin.config.getStringList("OreSeeking.IncludeOres");

		double range = plugin.getEnchantmentManager().getDouble("oreseeking", "Range",
				hand.getEnchantmentLevel(ench));

		Cuboid cube = new Cuboid(player.getLocation().add(range / 2, range / 2, range / 2),
				player.getLocation().subtract(range / 2, range / 2, range / 2));

		Block closest = null;
		double dist = 0;

		for (Block b : cube) {
			if (!materials.contains(b.getType() + ""))
				continue;
			double d = b.getLocation().distanceSquared(player.getLocation());
			if (dist != 0 && d > dist)
				continue;
			dist = d;
			closest = b;
		}

		if (closest == null) {
			MSG.tell(player, plugin.config.getString("OreSeeking.NotFound"));
		} else {
			Vector dir = closest.getLocation().add(.5, -1, .5).toVector().subtract(player.getLocation().toVector());
			Location loc = player.getLocation();
			loc.setDirection(dir);
			player.teleport(loc);
			MSG.tell(player,
					plugin.config.getString("OreSeeking.Found").replace("%type%", MSG.camelCase(closest.getType() + ""))
							.replace("%distance%", MSG.parseDecimal(Math.sqrt(dist), 1) + ""));
		}
		PlayerManager.setInfo(player, "oreseeking", (double) System.currentTimeMillis());
		MSG.sendTimedHotbar(player, "OreSeeking", hand.getEnchantmentLevel(ench));
	}
}
