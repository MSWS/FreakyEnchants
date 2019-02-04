package org.mswsplex.enchants.checkers.pickaxe;

import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.Utils;

public class AntiGravityCheck implements Listener {

	private FreakyEnchants plugin;

	private HashMap<Location, Long> prevent;

	public AntiGravityCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
		prevent = new HashMap<>();

		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				Iterator<Location> it = prevent.keySet().iterator();
				while (it.hasNext()) {
					Location loc = it.next();
					if (!plugin.config.getStringList("AntiGravity.ApplyTo")
							.contains(loc.clone().add(0, 1, 0).getBlock().getType().toString()))
						return;

					for (Player p : loc.getWorld().getPlayers())
						p.spigot().playEffect(loc.clone().add(.5, 0, .5), Effect.TILE_BREAK,
								loc.clone().add(0, 1, 0).getBlock().getTypeId(), 0, 0, 0, 0, 0, 5, 50);

					if (prevent.get(loc) == null || prevent.get(loc) > System.currentTimeMillis())
						continue;
					Material type = loc.getBlock().getType();
					loc.getBlock().setType(Material.BARRIER);
					loc.getBlock().setType(type);
					it.remove();
				}
			}
		}.runTaskTimer(plugin, 0, 20);

	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		Enchantment ench = plugin.getEnchant("antigravity");
		ItemStack hand = event.getPlayer().getItemInHand();
		if (!plugin.getEnchManager().containsEnchantment(hand, ench))
			return;
		if (!Utils.allowEnchant(event.getBlock().getWorld(), "antigravity"))
			return;
		long suspendTime = (long) (System.currentTimeMillis()
				+ plugin.getEnchManager().getDouble("antigravity", "Duration", hand.getEnchantmentLevel(ench)));
		if (!prevent.containsKey(block.getLocation()))
			prevent.put(block.getLocation(), suspendTime);
	}

	@EventHandler
	public void onBlockPhysic(BlockPhysicsEvent event) {
		Block block = event.getBlock();
		if (!plugin.config.getStringList("AntiGravity.ApplyTo").contains(block.getType().toString()))
			return;
		Location rounded = block.getLocation().subtract(0, 1, 0);
		if (prevent.containsKey(rounded) && prevent.get(rounded) >= System.currentTimeMillis())
			event.setCancelled(true);
	}
}
