package org.mswsplex.enchants.checkers.sword;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Utils;

public class ReviveCheck implements Listener {

	private FreakyEnchants plugin;

	public ReviveCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity ent = event.getDamager();
		if (!Utils.allowEnchant(ent.getWorld(), "revive"))
			return;
		if (!(ent instanceof LivingEntity) || !(event.getEntity() instanceof LivingEntity))
			return;
		if (ent instanceof Player && ((Player) ent).getGameMode() == GameMode.CREATIVE)
			return;
		ItemStack hand = ((LivingEntity) ent).getEquipment().getItemInHand();
		if (hand == null || hand.getType() == Material.AIR)
			return;
		if (!hand.containsEnchantment(plugin.getEnchant("revive")))
			return;
		LivingEntity living = (LivingEntity) ent;
		if (!plugin.getEnchManager().checkProbability("revive", hand.getEnchantmentLevel(plugin.getEnchant("revive"))))
			return;
		if (living.getHealth() > plugin.config.getDouble("Revive.MinimumHealth"))
			return;
		living.setHealth(Math.min(living.getHealth() + plugin.getEnchManager().getBonusAmount("revive",
				hand.getEnchantmentLevel(plugin.getEnchant("revive"))), living.getMaxHealth()));
		if (ent instanceof Player)
			MSG.sendStatusMessage((Player) ent, plugin.config.getString("Revive.SuccessMessage"));
	}
}
