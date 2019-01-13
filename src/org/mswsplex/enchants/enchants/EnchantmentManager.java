package org.mswsplex.enchants.enchants;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mswsplex.enchants.msws.CustomEnchants;
import org.mswsplex.enchants.utils.MSG;

public class EnchantmentManager {
	public HashMap<String, Enchantment> enchants;

	private CustomEnchants plugin;

	public EnchantmentManager(CustomEnchants plugin) {
		this.plugin = plugin;
		enchants = new HashMap<>();
		enchants.put("explosion", new Explosion(70));
		enchants.put("excavation", new Excavation(71));
		enchants.put("autosmelt", new AutoSmelt(72));
		enchants.put("witherpoint", new WitherPoint(73));
		enchants.put("toxicpoint", new ToxicPoint(74));
		enchants.put("revive", new Revive(75));
		enchants.put("freeze", new Freeze(76));
		enchants.put("stormbreaker", new Stormbreaker(77));
		enchants.put("nightshade", new Nightshade(78));
		enchants.put("severed", new Severed(79));
		enchants.put("hearty", new Hearty(80));
		enchants.put("spring", new Spring(81));
		enchants.put("heatshield", new HeatShield(82));
		enchants.put("summoner", new Summoner(83));
		enchants.put("explosive", new Explosive(84));
		enchants.put("stun", new Stun(85));
		enchants.put("withershot", new WitherShot(86));
		enchants.put("toxicshot", new ToxicShot(87));
		enchants.put("rage", new Rage(88));
		enchants.put("autograb", new AutoGrab(89));
		enchants.put("barrage", new Barrage(90));
		try {
			try {
				Field f = Enchantment.class.getDeclaredField("acceptingNew");
				f.setAccessible(true);
				f.set(null, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			boolean save = false;
			for (Entry<String, Enchantment> r : enchants.entrySet()) {
				try {
					Enchantment.registerEnchantment(r.getValue());
				} catch (IllegalArgumentException e) {
				}
				if (!plugin.enchantCosts.contains(r.getKey())) {
					save = true;
					plugin.enchantCosts.set(r.getKey(), 0);
				}
			}
			if (save)
				plugin.saveCosts();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean checkProbability(Enchantment ench, int lvl) {
		return checkProbability(ench.getName(), lvl);
	}

	public boolean checkProbability(String ench, int lvl) {
		ench = enchants.get(ench.toLowerCase()).getName().replace(" ", "");
		if (!plugin.config.contains(ench) || plugin.config.getConfigurationSection(ench) == null)
			return true;
		int big = 0;
		for (String level : plugin.config.getConfigurationSection(ench + ".Probability").getKeys(false)) {
			int l = Integer.parseInt(level);
			if (lvl >= l && l >= big)
				big = l;
		}
		return new Random().nextDouble() * 100 <= plugin.config.getDouble(ench + ".Probability." + big);
	}

	public int checkAmplifier(Enchantment ench, int lvl) {
		return checkAmplifier(ench.getName(), lvl);
	}

	public int checkAmplifier(String ench, int lvl) {
		ench = enchants.get(ench.toLowerCase()).getName().replace(" ", "");
		if (!plugin.config.contains(ench) || plugin.config.getConfigurationSection(ench) == null)
			return 0;
		int big = 0;
		for (String level : plugin.config.getConfigurationSection(ench + ".Amplifier").getKeys(false)) {
			int l = Integer.parseInt(level);
			if (lvl >= l && l >= big)
				big = l;
		}
		return plugin.config.getInt(ench + ".Amplifier." + big);
	}

	public double getBonusAmount(String ench, int lvl) {
		ench = enchants.get(ench.toLowerCase()).getName().replace(" ", "");
		if (!plugin.config.contains(ench) || plugin.config.getConfigurationSection(ench) == null)
			return 0.0;
		int big = 0;
		for (String level : plugin.config.getConfigurationSection(ench + ".BonusAmount").getKeys(false)) {
			int l = Integer.parseInt(level);
			if (lvl >= l && l >= big)
				big = l;
		}
		return plugin.config.getDouble(ench + ".BonusAmount." + big);
	}

	public ItemStack addEnchant(ItemStack item, int level, Enchantment enchant) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null) {
			lore = new ArrayList<String>();
		} else {
			for (int i = 0; i < lore.size(); i++) {
				if (lore.get(i).contains(enchant.getName()))
					lore.remove(i);
			}
		}

		if (level <= 0) {
			item.removeEnchantment(enchant);
		} else {
			item.addUnsafeEnchantment(enchant, level);
			lore.add(MSG.color("&7" + enchant.getName() + " " + MSG.toRoman(level)));
		}

		meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
