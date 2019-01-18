package org.mswsplex.enchants.enchants.armor;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.utils.Utils;

public class Hearty extends Enchantment {

	public Hearty(int id) {
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
		return 4;
	}

	@Override
	public String getName() {
		return "Hearty";
	}

	@Override
	public int getStartLevel() {
		return 0;
	}

}
