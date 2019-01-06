package org.mswsplex.enchants.enchants;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class Explosive extends Enchantment {

	public Explosive(int id) {
		super(id);
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return item.getType()==Material.BOW;
	}

	@Override
	public boolean conflictsWith(Enchantment enchantment) {
		return false;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.BOW;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public String getName() {
		return "Explosive";
	}

	@Override
	public int getStartLevel() {
		return 0;
	}

}
