package org.mswsplex.enchants.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.HotbarMessenger;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Sounds;

public class PlayerManager {
	public static FreakyEnchants plugin;

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

	public static double getBalance(OfflinePlayer player) {
		if (plugin.config.getString("Economy.Type").equals("XP")) {
			if (player.isOnline())
				return Double.parseDouble(MSG.parseDecimal(((Player) player).getLevel() + ((Player) player).getExp(),
						plugin.config.getInt("Economy.Precision")));
			return -1;
		}
		if (plugin.getEconomy() == null || plugin.config.getString("Economy.Type").equals("TOKEN")) {
			return getDouble(player, "tokens");
		}
		return plugin.getEconomy().getBalance(player);
	}

	public static void setBalance(OfflinePlayer player, double bal) {
		if (plugin.config.getString("Economy.Type").equals("XP")) {
			if (player.isOnline()) {
				((Player) player).setLevel((int) Math.floor(bal));
				((Player) player).setExp((float) (bal - Math.floor(bal)));
				return;
			}
		}
		if (plugin.getEconomy() == null || plugin.config.getString("Economy.Type").equals("TOKEN")) {
			setInfo(player, "tokens", bal);
			return;
		}
		plugin.getEconomy().depositPlayer(player, bal - getBalance(player));
	}

	public static List<String> getStringList(OfflinePlayer player, String id) {
		return plugin.data.getStringList(player.getUniqueId() + "." + id);
	}

	@SuppressWarnings("deprecation")
	public static void emptyInventory(Player p) {
		if (plugin.lang.getBoolean("InventoryFull.Title.Enabled")) {
			p.sendTitle(MSG.color(plugin.lang.getString("InventoryFull.Title.Top")),
					MSG.color(plugin.lang.getString("InventoryFull.Title.Bottom")));

		}
		if (plugin.lang.getBoolean("InventoryFull.ActionBarMessage.Enabled")) {
			HotbarMessenger.sendHotBarMessage(p,
					MSG.color(plugin.lang.getString("InventoryFull.ActionBarMessage.Message")));
		}
		if (plugin.lang.getBoolean("InventoryFull.Sound.Enabled")) {
			p.playSound(p.getLocation(),
					Sounds.valueOf(plugin.lang.getString("InventoryFull.Sound.Name")).bukkitSound(),
					(float) plugin.lang.getDouble("InventoryFull.Sound.Volume"),
					(float) plugin.lang.getDouble("InventoryFull.Sound.Pitch"));
		}
		MSG.tell(p, plugin.lang.getString("InventoryFull.ChatMessage"));
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
