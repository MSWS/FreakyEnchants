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

	@SuppressWarnings("deprecation")
	public EnchantmentManager(CustomEnchants plugin) {
		this.plugin = plugin;
		enchants = new HashMap<>();
		List<Integer> validIds = new ArrayList<Integer>();
		for (int i = 0; i < 255; i++) {
			if (Enchantment.getById(i) == null) {
				validIds.add(i);
			}
		}
		
		enchants.put("burning", new Burning(validIds.get(0)));
		enchants.put("explosion", new Explosion(validIds.get(1)));
		enchants.put("excavation", new Excavation(validIds.get(2)));
		enchants.put("autosmelt", new AutoSmelt(validIds.get(3)));
		enchants.put("witherpoint", new WitherPoint(validIds.get(4)));
		enchants.put("toxicpoint", new ToxicPoint(validIds.get(5)));
		enchants.put("revive", new Revive(validIds.get(6)));
		enchants.put("freeze", new Freeze(validIds.get(7)));
		enchants.put("stormbreaker", new Stormbreaker(validIds.get(8)));
		enchants.put("nightshade", new Nightshade(validIds.get(9)));
		enchants.put("severed", new Severed(validIds.get(10)));
		enchants.put("hearty", new Hearty(validIds.get(11)));
		enchants.put("spring", new Spring(validIds.get(12)));
		enchants.put("heatshield", new HeatShield(validIds.get(13)));
		enchants.put("summoner", new Summoner(validIds.get(14)));
		enchants.put("explosive", new Explosive(validIds.get(15)));
		enchants.put("stun", new Stun(validIds.get(16)));
		enchants.put("withershot", new WitherShot(validIds.get(17)));
		enchants.put("toxicshot", new ToxicShot(validIds.get(18)));
		enchants.put("rage", new Rage(validIds.get(19)));
		enchants.put("autograb", new AutoGrab(validIds.get(20)));
		enchants.put("barrage", new Barrage(validIds.get(21)));
		enchants.put("extraxp", new ExtraXP(validIds.get(22)));
		enchants.put("selfdestruct", new SelfDestruct(validIds.get(23)));
		enchants.put("endershot", new EnderShot(validIds.get(24)));

		if (validIds.size() < enchants.size()) {
			MSG.log("[WARNING] This server has reached the limit of 255 custom enchantments.");
			MSG.log("[WARNING] This means that only certain enchantments may work.");
		}

		try {
			try {
				Field f = Enchantment.class.getDeclaredField("acceptingNew");
				f.setAccessible(true);
				f.set(null, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			boolean save = false;
			boolean fail = false;
			for (Entry<String, Enchantment> r : enchants.entrySet()) {
				try {
					Enchantment.registerEnchantment(r.getValue());
				} catch (IllegalArgumentException e) {
					fail = true;
				}
				if (!plugin.enchantCosts.contains(r.getKey())) {
					save = true;
					plugin.enchantCosts.set(r.getKey(), 0);
				}
			}
			if (fail)
				MSG.log("[WARNING] Some enchantments have failed to register. If you have just reloaded the server then do not worry about this. If this is after a fresh restart then a different enchantment plugin is interfering with this plugin.");

			if (save)
				plugin.saveCosts();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getId(Enchantment ench) {
		for (Entry<String, Enchantment> e : enchants.entrySet()) {
			if (ench.getName().equals(e.getValue().getName()))
				return e.getKey();
		}
		return "";
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
