package org.mswsplex.enchants.enchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class WitherPoint extends Enchantment {

	public WitherPoint(int id) {
		super(id);
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return item.getType().toString().contains("SWORD") || item.getType().toString().contains("AXE");
	}

	@Override
	public boolean conflictsWith(Enchantment enchantment) {
		return false;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.TOOL;
	}

	@Override
	public int getMaxLevel() {
		return 5;
	}

	@Override
	public String getName() {
		return "Wither Point";
	}

	@Override
	public int getStartLevel() {
		return 0;
	}

}
