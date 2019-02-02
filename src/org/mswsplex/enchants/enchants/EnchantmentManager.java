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
import org.mswsplex.enchants.enchants.armor.Alarmer;
import org.mswsplex.enchants.enchants.armor.Burning;
import org.mswsplex.enchants.enchants.armor.DoubleJump;
import org.mswsplex.enchants.enchants.armor.FrostWalker;
import org.mswsplex.enchants.enchants.armor.Hearty;
import org.mswsplex.enchants.enchants.armor.HeatShield;
import org.mswsplex.enchants.enchants.armor.NetherWalker;
import org.mswsplex.enchants.enchants.armor.SelfDestruct;
import org.mswsplex.enchants.enchants.armor.SoftTouch;
import org.mswsplex.enchants.enchants.armor.Speed;
import org.mswsplex.enchants.enchants.armor.Spring;
import org.mswsplex.enchants.enchants.armor.Summoner;
import org.mswsplex.enchants.enchants.axe.Chucker;
import org.mswsplex.enchants.enchants.axe.Recall;
import org.mswsplex.enchants.enchants.axe.TreeFeller;
import org.mswsplex.enchants.enchants.bow.Barrage;
import org.mswsplex.enchants.enchants.bow.EnderShot;
import org.mswsplex.enchants.enchants.bow.Explosive;
import org.mswsplex.enchants.enchants.bow.Stun;
import org.mswsplex.enchants.enchants.bow.ToxicShot;
import org.mswsplex.enchants.enchants.bow.WitherShot;
import org.mswsplex.enchants.enchants.pickaxe.AutoGrab;
import org.mswsplex.enchants.enchants.pickaxe.AutoSmelt;
import org.mswsplex.enchants.enchants.pickaxe.Excavation;
import org.mswsplex.enchants.enchants.pickaxe.Explosion;
import org.mswsplex.enchants.enchants.pickaxe.ExtraXP;
import org.mswsplex.enchants.enchants.pickaxe.OreSeeking;
import org.mswsplex.enchants.enchants.sword.ChainReaction;
import org.mswsplex.enchants.enchants.sword.Freeze;
import org.mswsplex.enchants.enchants.sword.Nightshade;
import org.mswsplex.enchants.enchants.sword.Rage;
import org.mswsplex.enchants.enchants.sword.Revive;
import org.mswsplex.enchants.enchants.sword.Severed;
import org.mswsplex.enchants.enchants.sword.Stormbreaker;
import org.mswsplex.enchants.enchants.sword.ToxicPoint;
import org.mswsplex.enchants.enchants.sword.Tripper;
import org.mswsplex.enchants.enchants.sword.WitherPoint;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.MSG;

public class EnchantmentManager {
	public HashMap<String, Enchantment> enchants;

	private FreakyEnchants plugin;

	private final int offset = 70; // Enchantment ID's are painful to manage

	public EnchantmentManager(FreakyEnchants plugin) {
		this.plugin = plugin;
		enchants = new HashMap<>();

		enchants.put("explosion", new Explosion(70 + offset));
		enchants.put("excavation", new Excavation(71 + offset));
		enchants.put("autosmelt", new AutoSmelt(72 + offset));
		enchants.put("witherpoint", new WitherPoint(73 + offset));
		enchants.put("toxicpoint", new ToxicPoint(74 + offset));
		enchants.put("revive", new Revive(75 + offset));
		enchants.put("freeze", new Freeze(76 + offset));
		enchants.put("stormbreaker", new Stormbreaker(77 + offset));
		enchants.put("nightshade", new Nightshade(78 + offset));
		enchants.put("severed", new Severed(79 + offset));
		enchants.put("hearty", new Hearty(80 + offset));
		enchants.put("spring", new Spring(81 + offset));
		enchants.put("heatshield", new HeatShield(82 + offset));
		enchants.put("summoner", new Summoner(83 + offset));
		enchants.put("explosive", new Explosive(84 + offset));
		enchants.put("stun", new Stun(85 + offset));
		enchants.put("withershot", new WitherShot(86 + offset));
		enchants.put("toxicshot", new ToxicShot(87 + offset));
		enchants.put("rage", new Rage(88 + offset));
		enchants.put("autograb", new AutoGrab(89 + offset));
		enchants.put("barrage", new Barrage(90 + offset));
		enchants.put("extraxp", new ExtraXP(91 + offset));
		enchants.put("selfdestruct", new SelfDestruct(92 + offset));
		enchants.put("endershot", new EnderShot(93 + offset));
		enchants.put("burning", new Burning(94 + offset));
		enchants.put("speed", new Speed(95 + offset));
		enchants.put("treefeller", new TreeFeller(96 + offset));
		enchants.put("frostwalker", new FrostWalker(97 + offset));
		enchants.put("tripper", new Tripper(98 + offset));
		enchants.put("doublejump", new DoubleJump(99 + offset));
		enchants.put("chainreaction", new ChainReaction(100 + offset));
		enchants.put("oreseeking", new OreSeeking(101 + offset));
		enchants.put("alarmer", new Alarmer(102 + offset));
		enchants.put("chucker", new Chucker(103 + offset));
		enchants.put("recall", new Recall(104 + offset));
		enchants.put("softtouch", new SoftTouch(105 + offset));
		enchants.put("netherwalker", new NetherWalker(106 + offset));

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
					if (Enchantment.getByName(r.getValue().getName()) == null)
						MSG.log("[WARNING] Failed to register enchantment " + r.getValue().getName());
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

	/**
	 * Returns the id of a specified enchantment
	 * 
	 * @param ench
	 * @return null if not found
	 */
	public String getId(Enchantment ench) {
		for (Entry<String, Enchantment> e : enchants.entrySet()) {
			if (ench.getName().equals(e.getValue().getName()))
				return e.getKey();
		}
		return null;
	}

	/**
	 * Returns a list of all enchantment IDs
	 * 
	 * @return
	 */
	public List<String> getEnchantmentIDs() {
		return new ArrayList<>(enchants.keySet());
	}

	/**
	 * Returns the highest defined number in the config Use this for probability,
	 * attributes, etc. 0 if undefined
	 * 
	 * @param ench
	 * @param id
	 * @param lvl
	 * @return
	 */
	public double getDouble(String ench, String id, int lvl) {
		ench = enchants.get(ench.toLowerCase()).getName().replace(" ", "");
		if (!plugin.config.contains(ench) || plugin.config.getConfigurationSection(ench) == null)
			return 0.0;
		int big = 0;
		for (String level : plugin.config.getConfigurationSection(ench + "." + id).getKeys(false)) {
			int l = Integer.parseInt(level);
			if (lvl >= l && l >= big)
				big = l;
		}
		return plugin.config.getDouble(ench + "." + id + "." + big);
	}

	/**
	 * @see getDouble(String, "BonusAmount", int);
	 * @param ench
	 * @param lvl
	 * @return
	 */
	public double getBonusAmount(String ench, int lvl) {
		return getDouble(ench, "BonusAmount", lvl);
	}

	/**
	 * @see getDouble(String, "Probability", lvl);
	 * @param ench
	 * @param lvl
	 * @return Returns true if a random number is <= the probability
	 */
	public boolean checkProbability(String ench, int lvl) {
		return new Random().nextDouble() * 100 <= getDouble(ench, "Probability", lvl);
	}

	/**
	 * @see getDouble(String, "Amplifier", lvl);
	 * @param ench
	 * @param lvl
	 * @return
	 */
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
		return (int) getDouble(ench, "Amplifier", lvl);
	}

	/**
	 * Adds an enchantment to an item
	 * 
	 * @param item
	 * @param level
	 * @param enchant
	 */
	public void addEnchant(ItemStack item, int level, Enchantment enchant) {
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
	}

	public boolean containsEnchantment(ItemStack item, Enchantment ench) {
		return item.getEnchantments().keySet().contains(ench);
	}

	public boolean containsEnchantment(ItemStack item, String ench) {
		return containsEnchantment(item, plugin.getEnchant(ench));
	}
}
