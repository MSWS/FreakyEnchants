package org.mswsplex.enchants.checkers;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mswsplex.enchants.managers.PlayerManager;
import org.mswsplex.enchants.msws.CustomEnchants;
import org.mswsplex.enchants.utils.MSG;

public class StunCheck implements Listener {
	private CustomEnchants plugin;

	public StunCheck(CustomEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onEntityHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() == null || event.getDamager() == null || !event.getDamager().hasMetadata("stunArrow"))
			return;
		if (!(event.getEntity() instanceof LivingEntity))
			return;
		LivingEntity ent = (LivingEntity) event.getEntity();

		double duration = event.getDamager().getMetadata("stunArrow").get(0).asDouble();
		if (event.getEntity() instanceof Player) {
			PlayerManager.setInfo((Player) ent, "restrictMovement", System.currentTimeMillis() + duration);
		} else {
			ent.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, (int) duration / 1000 * 20, 6));
		}
		if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
			MSG.sendStatusMessage((Player) ((Projectile) event.getDamager()).getShooter(),
					plugin.config.getString("Stun.SuccessMessage"));
		}
	}

	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile proj = event.getEntity();
		if (proj == null || proj.getShooter() == null || !(proj.getShooter() instanceof LivingEntity))
			return;
		LivingEntity ent = (LivingEntity) proj.getShooter();
		ItemStack hand = ent.getEquipment().getItemInHand();
		if (!hand.containsEnchantment(plugin.getEnchantmentManager().enchants.get("stun")))
			return;
		if (!plugin.getEnchantmentManager().checkProbability("stun",
				hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("stun"))))
			return;
		proj.setMetadata("stunArrow",
				new FixedMetadataValue(plugin, plugin.getEnchantmentManager().getBonusAmount("stun",
						hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("stun")))));
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (PlayerManager.getInfo(player, "restrictMovement") == null)
			return;
		if (PlayerManager.getDouble(player, "restrictMovement") < System.currentTimeMillis())
			return;
		if (event.getTo().getX() != event.getFrom().getX() || event.getTo().getZ() != event.getFrom().getZ())
			event.setTo(event.getFrom());
	}
}
