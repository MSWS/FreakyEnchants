package org.mswsplex.enchants.enchants;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class Spring extends Enchantment {

	public Spring(int id) {
		super(id);
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return item.getType().toString().contains("BOOTS");
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
		return 3;
	}

	@Override
	public String getName() {
		return "Spring";
	}

	@Override
	public int getStartLevel() {
		return 0;
	}

}
