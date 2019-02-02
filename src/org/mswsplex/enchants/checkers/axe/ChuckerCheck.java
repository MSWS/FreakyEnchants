package org.mswsplex.enchants.checkers.axe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.Utils;

public class ChuckerCheck implements Listener {

	private FreakyEnchants plugin;

	private List<Item> thrown;

	@SuppressWarnings("deprecation")
	public ChuckerCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		thrown = new ArrayList<Item>();
		Bukkit.getPluginManager().registerEvents(this, plugin);
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, runParticles(), 0, 1);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		if (!Utils.allowEnchant(player.getWorld(), "chucker"))
			return;
		ItemStack hand = player.getItemInHand();

		if (hand == null || hand.getType() == Material.AIR)
			return;
		if (!plugin.getEnchManager().containsEnchantment(hand, "chucker"))
			return;
		if (player.getGameMode() == GameMode.CREATIVE && plugin.config.getBoolean("Chucker.IgnoreCreative"))
			return;
		int level = hand.getEnchantmentLevel(plugin.getEnchant("chucker"));

		Item item = player.getWorld().dropItem(player.getEyeLocation(), hand);

		double strength = plugin.getEnchManager().getDouble("chucker", "ThrowStrength", level);

		item.setVelocity(player.getEyeLocation().getDirection().multiply(strength)
				.setY(player.getLocation().getDirection().getY() * (strength / 2)));
		item.setMetadata("chuckThrower", new FixedMetadataValue(plugin, player.getUniqueId() + ""));
		item.setMetadata("chuckLevel", new FixedMetadataValue(plugin, level));

		Utils.playSound(plugin.config, "Chucker.ThrowSound", player.getLocation());

		player.setItemInHand(new ItemStack(Material.AIR));

		thrown.add(item);
		event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void itemPickupEvent(PlayerPickupItemEvent event) {
		thrown.remove(event.getItem());
	}

	public BukkitRunnable runParticles() {
		return new BukkitRunnable() {
			@Override
			public void run() {
				Iterator<Item> it = thrown.iterator();
				while (it.hasNext()) {
					Item item = it.next();
					if (item.isOnGround()) {
						it.remove();
						continue;
					}

					Player thrower = Bukkit
							.getPlayer(UUID.fromString(item.getMetadata("chuckThrower").get(0).asString()));

					if (thrower == null) { // If the player logged off
						it.remove();
						continue;
					}

					for (Entity ent : item.getNearbyEntities(1.5, 1.5, 1.5)) {
						if (!(ent instanceof LivingEntity) || ent.equals(thrower) || ent.isDead())
							continue;
						LivingEntity e = (LivingEntity) ent;
						e.damage(plugin.getEnchManager().getDouble("Chucker", "DamageAmount",
								item.getMetadata("chuckLevel").get(0).asInt())); // If we set the thrower to the
																					// damager, for some reason if the
																					// thrower is too far the entity
																					// goes undamaged
						thrower.getInventory().addItem(item.getItemStack());
						item.remove();
						it.remove();
						Utils.playSound(plugin.config, "Chucker.HitSound", thrower.getLocation());
						break;
					}

					if (plugin.config.getBoolean("Chucker.Particle.Enabled")) {
						// ParticleEffect.valueOf(plugin.config.getString("Chucker.Particle.Type"))
						// .display(item.getVelocity(), 1, item.getLocation(), 5000);

						for (Player p : item.getWorld().getPlayers()) {
							p.spigot().playEffect(item.getLocation(), Effect.FLAME, 0, 0, 0, 0, 0, 0, 1, 5000);
						}
					}
				}
			}
		};
	}
}
