package org.mswsplex.enchants.enchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class Nightshade extends Enchantment {

	public Nightshade(int id) {
		super(id);
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return item.getType().toString().contains("AXE");
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
		return 3;
	}

	@Override
	public String getName() {
		return "Nightshade";
	}

	@Override
	public int getStartLevel() {
		return 0;
	}

}
