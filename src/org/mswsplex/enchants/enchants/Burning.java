package org.mswsplex.enchants.enchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.utils.Utils;

public class Burning extends Enchantment {

	public Burning(int id) {
		super(id);
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return Utils.isArmor(item.getType());
	}

	@Override
	public boolean conflictsWith(Enchantment enchantment) {
		return false;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.ARMOR;
	}

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public String getName() {
		return "Burning";
	}

	@Override
	public int getStartLevel() {
		return 0;
	}

}
