package org.mswsplex.enchants.checkers.bow;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.Sounds;
import org.mswsplex.enchants.utils.Utils;

public class BarrageCheck implements Listener {

	private FreakyEnchants plugin;

	public BarrageCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityHit(ProjectileHitEvent event) {
		if (event.getEntity() == null || !event.getEntity().hasMetadata("barrageArrow"))
			return;
		event.getEntity().remove();
	}

	@EventHandler(ignoreCancelled = true)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile proj = event.getEntity();
		if (proj == null || proj.getShooter() == null || !(proj.getShooter() instanceof LivingEntity))
			return;
		if (!Utils.allowEnchant(proj.getWorld(), "barrage"))
			return;
		LivingEntity ent = (LivingEntity) proj.getShooter();
		ItemStack hand = ent.getEquipment().getItemInHand();
		if (!hand.containsEnchantment(plugin.getEnchant("barrage")))
			return;
		double pullTime, minTime = 950;
		if (ent instanceof Player) {
			pullTime = System.currentTimeMillis() - plugin.getCPlayer(((Player) ent)).getSaveDouble("pulledBarrage");
		} else {
			pullTime = minTime;
		}

		if (pullTime >= minTime || !plugin.config.getBoolean("Barrage.RequireFullCharge")) {
			Random r = new Random();
			float inaccuracy = (float) plugin.config.getDouble("Barrage.OffsetInaccuracy");
			for (int i = 0; i < plugin.getEnchManager().getBonusAmount("barrage",
					hand.getEnchantmentLevel(plugin.getEnchant("barrage"))); i++) {
				if (ent instanceof Player && plugin.config.getBoolean("Barrage.UseInventoryArrows")
						&& ((Player) ent).getGameMode() != GameMode.CREATIVE
						&& !(hand.containsEnchantment(Enchantment.ARROW_INFINITE)
								&& plugin.config.getBoolean("Barrage.AllowInfinity"))) {
					Player p = (Player) ent;
					if (!p.getInventory().contains(Material.ARROW))
						break;
					int bSlot = p.getInventory().first(Material.ARROW);
					ItemStack arr = p.getInventory().getItem(bSlot);
					if (arr.getAmount() == 1) {
						p.getInventory().setItem(bSlot, new ItemStack(Material.AIR));
					} else {
						arr.setAmount(arr.getAmount() - 1);
						p.getInventory().setItem(bSlot, arr);
					}
				}

				Vector offset = new Vector((r.nextDouble() - r.nextDouble()) * inaccuracy,
						(r.nextDouble() - r.nextDouble()) * inaccuracy, (r.nextDouble() - r.nextDouble()) * inaccuracy);
				Arrow a = ent.getWorld().spawnArrow(ent.getEyeLocation(), event.getEntity().getVelocity().add(offset),
						(float) plugin.config.getDouble("Barrage.Speed"),
						(float) plugin.config.getDouble("Barrage.Spread"));
				a.setShooter(ent);
				a.setCritical(((Arrow) event.getEntity()).isCritical());
				a.setMetadata("barrageArrow", new FixedMetadataValue(plugin, true));
				ent.getWorld().playSound(ent.getLocation(), Sounds.SHOOT_ARROW.bukkitSound(), 2, 1);
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack hand = player.getItemInHand();
		if (!hand.containsEnchantment(plugin.getEnchant("barrage")))
			return;
		if (!(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK))
			return;
		if (hand.getType() != Material.BOW
				|| (!player.getInventory().contains(Material.ARROW) && player.getGameMode() != GameMode.CREATIVE))
			return;
		plugin.getCPlayer(player).setSaveData("pulledBarrage", (double) System.currentTimeMillis());
	}
}
