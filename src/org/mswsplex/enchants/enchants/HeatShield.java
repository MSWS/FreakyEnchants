package org.mswsplex.enchants.enchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.utils.Utils;

public class HeatShield extends Enchantment {

	public HeatShield(int id) {
		super(id);
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return Utils.isArmor(item.getType());
	}

	@Override
	public boolean conflictsWith(Enchantment arg0) {
		return false;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.ARMOR;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public String getName() {
		return "Heat Shield";
	}

	@Override
	public int getStartLevel() {
		return 0;
	}

}
