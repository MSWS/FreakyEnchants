package org.mswsplex.enchants.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mswsplex.enchants.msws.CustomEnchants;
import org.mswsplex.enchants.utils.MSG;

public class PlayerManager {
	public static CustomEnchants plugin;

	public static void setInfo(OfflinePlayer player, String id, Object data) {
		if (!isSaveable(data)) {
			int currentLine = Thread.currentThread().getStackTrace()[2].getLineNumber();

			String fromClass = new Exception().getStackTrace()[1].getClassName();
			if (fromClass.contains("."))
				fromClass = fromClass.split("\\.")[fromClass.split("\\.").length - 1];
			MSG.log("WARNING!!! SAVING ODD DATA FROM " + fromClass + ":" + currentLine);
		}
		plugin.data.set(player.getUniqueId() + "." + id, data);
	}

	public static void deleteInfo(OfflinePlayer player) {
		plugin.data.set(player.getUniqueId() + "", null);
	}

	public static void removeInfo(OfflinePlayer player, String id) {
		plugin.data.set(player.getUniqueId() + "." + id, null);
	}

	public static Object getInfo(OfflinePlayer player, String id) {
		return plugin.data.get(player.getUniqueId() + "." + id);
	}

	public static String getString(OfflinePlayer player, String id) {
		return plugin.data.getString(player.getUniqueId() + "." + id);
	}

	public static Double getDouble(OfflinePlayer player, String id) {
		return plugin.data.getDouble(player.getUniqueId() + "." + id);
	}

	public static Boolean getBoolean(OfflinePlayer player, String id) {
		return plugin.data.getBoolean(player.getUniqueId() + "." + id);
	}

	public static List<String> getStringList(OfflinePlayer player, String id) {
		return plugin.data.getStringList(player.getUniqueId() + "." + id);
	}

	public static ItemStack parseItem(ConfigurationSection section, String path, OfflinePlayer player) {
		ConfigurationSection gui = section.getConfigurationSection(path);
		ItemStack item = new ItemStack(Material.valueOf(gui.getString("Icon")));
		List<String> lore = new ArrayList<String>();
		if (gui.contains("Amount"))
			item.setAmount(gui.getInt("Amount"));
		if (gui.contains("Data"))
			item.setDurability((short) gui.getInt("Data"));
		ItemMeta meta = item.getItemMeta();
		if (gui.contains("Name"))
			meta.setDisplayName(MSG.color("&r" + gui.getString("Name")));
		if (gui.contains("Lore")) {
			for (String temp : gui.getStringList("Lore"))
				lore.add(MSG.color("&r" + temp));
		}
		if (gui.getBoolean("Unbreakable")) {
			meta.spigot().setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		}
		if (gui.contains("Cost")) {
			HashMap<Material, Integer> mats = new HashMap<>();
			ConfigurationSection costs = gui.getConfigurationSection("Cost");
			for (String material : costs.getKeys(false))
				mats.put(Material.valueOf(material), costs.getInt(material));
			lore.add("");
			if (mats.size() == 1) {
				lore.add(MSG.color("&aCost: &c" + mats.values().toArray()[0] + " "
						+ MSG.camelCase(mats.keySet().toArray()[0] + "")));
			} else {
				lore.add(MSG.color("&aCost:"));
				for (Material mat : mats.keySet()) {
					lore.add(MSG.color("&c* " + mats.get(mat) + " "
							+ MSG.camelCase(mat.name() + (mats.get(mat) == 1 ? "" : "s"))));
				}
			}
		}
		if (gui.contains("Enchantments")) {
			ConfigurationSection enchs = gui.getConfigurationSection("Enchantments");
			for (String enchant : enchs.getKeys(false)) {
				int level = 1;
				if (enchs.contains(enchant + ".Level"))
					level = enchs.getInt(enchant + ".Level");
				if (enchs.contains(enchant + ".Visible") && !enchs.getBoolean(enchant + ".Visible"))
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
				item.addUnsafeEnchantment(Enchantment.getByName(enchant.toUpperCase()), level);
				meta = item.getItemMeta();
			}
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * Get whether an object is saveable in YAML
	 * 
	 * @param obj Object type to test
	 * @return True if saveable, false otherwise
	 */
	public static boolean isSaveable(Object obj) {
		return (obj instanceof String || obj instanceof Integer || obj instanceof ArrayList || obj instanceof Boolean
				|| obj == null || obj instanceof Double || obj instanceof Short || obj instanceof Long
				|| obj instanceof Character);
	}
}
