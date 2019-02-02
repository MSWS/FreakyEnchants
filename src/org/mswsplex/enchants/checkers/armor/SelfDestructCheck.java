package org.mswsplex.enchants.checkers.armor;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.Utils;

public class SelfDestructCheck implements Listener {

	private FreakyEnchants plugin;

	public SelfDestructCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDeath(EntityDeathEvent event) {
		Enchantment ench = plugin.getEnchant("selfdestruct");
		LivingEntity ent = event.getEntity();
		if (!Utils.allowEnchant(ent.getWorld(), "selfdestruct"))
			return;
		EntityEquipment e = ent.getEquipment();
		if (e.getChestplate() == null || e.getChestplate().getType() == Material.AIR)
			return;
		if (!plugin.getEnchManager().containsEnchantment(e.getChestplate(), "selfdestruct"))
			return;
		ent.getWorld().createExplosion(ent.getLocation().getX(), ent.getLocation().getY(), ent.getLocation().getZ(),
				(float) plugin.getEnchManager().getBonusAmount("selfdestruct",
						e.getChestplate().getEnchantmentLevel(ench)),
				false, plugin.config.getBoolean("SelfDestruct.BreakBlocks"));
	}

	boolean hasArmor(ItemStack[] armor) {
		for (ItemStack item : armor)
			if (item != null && Utils.isArmor(item.getType()))
				return true;
		return false;
	}
}
