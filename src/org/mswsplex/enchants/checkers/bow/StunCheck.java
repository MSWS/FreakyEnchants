package org.mswsplex.enchants.checkers.bow;

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
import org.mswsplex.enchants.managers.CPlayer;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Utils;

public class StunCheck implements Listener {
	private FreakyEnchants plugin;

	public StunCheck(FreakyEnchants plugin) {
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
		if (!Utils.allowEnchant(ent.getWorld(), "stun"))
			return;
		double duration = event.getDamager().getMetadata("stunArrow").get(0).asDouble();
		if (event.getEntity() instanceof Player) {
			plugin.getCPlayer((Player) event.getEntity()).setTempData("restrictMovement",
					System.currentTimeMillis() + duration);
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
		if (!plugin.getEnchManager().containsEnchantment(hand, "stun"))
			return;
		if (!plugin.getEnchManager().checkProbability("stun", hand.getEnchantmentLevel(plugin.getEnchant("stun"))))
			return;
		proj.setMetadata("stunArrow", new FixedMetadataValue(plugin,
				plugin.getEnchManager().getBonusAmount("stun", hand.getEnchantmentLevel(plugin.getEnchant("stun")))));
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (!cp.hasTempData("restrictMovement"))
			return;
		if (cp.getTempDouble("restrictMovement") < System.currentTimeMillis())
			return;
		if (event.getTo().getX() != event.getFrom().getX() || event.getTo().getZ() != event.getFrom().getZ())
			event.setTo(event.getFrom());
	}
}
