package org.mswsplex.enchants.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.MSG;

public class CPlayer {
	private OfflinePlayer player;
	private UUID uuid;

	private HashMap<String, Object> tempData;

	private File saveFile, dataFile;
	private YamlConfiguration data;

	private FreakyEnchants plugin;

	/**
	 * CPlayer is a custom player object that holds two different types of data
	 * temp: Temporary, do not use this for currencies, cooldowns, anything you want
	 * stored over reloads/restarts save: "Permanent", use this for currencies or
	 * anything you want saved note that these are completely separate, you cannot
	 * do {@link CPlayer#setSaveData(String, Object)} and expect to grab it with
	 * {@link CPlayer#getTempData(String)}
	 * 
	 * @param player OfflinePlayer to get data of, files are stored using UUID's
	 *               stripped of -'s
	 * @param plugin FreakyEnchants instance
	 */
	public CPlayer(OfflinePlayer player, FreakyEnchants plugin) {
		this.plugin = plugin;
		this.player = player;
		this.uuid = player.getUniqueId();

		this.tempData = new HashMap<>();

		dataFile = new File(plugin.getDataFolder() + "/data");
		dataFile.mkdir();

		saveFile = new File(plugin.getDataFolder() + "/data/" + (uuid + "").replace("-", "") + ".yml");
		if (!saveFile.exists())
			try {
				saveFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		data = YamlConfiguration.loadConfiguration(saveFile);
	}

	public OfflinePlayer getPlayer() {
		return this.player;
	}

	public YamlConfiguration getDataFile() {
		return this.data;
	}

	public void setTempData(String id, Object obj) {
		tempData.put(id, obj);
	}

	public void setSaveData(String id, Object obj) {
		data.set(id, obj);
	}

	public void setSaveData(String id, Object obj, boolean save) {
		setSaveData(id, obj);
		if (save)
			saveData();
	}

	public void saveData() {
		try {
			data.save(saveFile);
		} catch (Exception e) {
			MSG.log("&cError saving data file");
			MSG.log("&a----------Start of Stack Trace----------");
			e.printStackTrace();
			MSG.log("&a----------End of Stack Trace----------");
		}
	}

	public void clearTempData() {
		tempData.clear();
	}

	public void clearSaveData() {
		saveFile.delete();
		saveFile.mkdir();
		data = YamlConfiguration.loadConfiguration(saveFile);
	}

	public Object getTempData(String id) {
		return tempData.get(id);
	}

	public String getTempString(String id) {
		return (String) getTempData(id);
	}

	public double getTempDouble(String id) {
		return hasTempData(id) ? (double) getTempData(id) : 0;
	}

	public int getTempInteger(String id) {
		return hasTempData(id) ? (int) getTempData(id) : 0;
	}

	public boolean hasTempData(String id) {
		return tempData.containsKey(id);
	}

	public Object getSaveData(String id) {
		return data.get(id);
	}

	public boolean hasSaveData(String id) {
		return data.contains(id);
	}

	public String getSaveString(String id) {
		return (String) getSaveData(id).toString();
	}

	public double getSaveDouble(String id) {
		return hasSaveData(id) ? (double) getSaveData(id) : 0;
	}

	public int getSaveInteger(String id) {
		return hasSaveData(id) ? (int) getSaveData(id) : 0;
	}

	public void removeTempData(String id) {
		tempData.remove(id);
	}

	public void removeSaveData(String id) {
		data.set(id, null);
	}

	public List<String> getTempEntries() {
		return new ArrayList<>(tempData.keySet());
	}

	public List<String> getSaveEntries() {
		return new ArrayList<>(data.getKeys(false));
	}

	/**
	 * Get's the player's balance based on the server's config.yml Possible
	 * currencies: XP, TOKEN, VAULT the player's xp progress is used for the decimal
	 * value
	 * 
	 * @return
	 */
	public double getBalance() {
		if (plugin.config.getString("Economy.Type").equals("XP")) {
			if (player.isOnline()) {
				return Double.parseDouble(MSG.parseDecimal(((Player) player).getLevel() + ((Player) player).getExp(),
						plugin.config.getInt("Economy.Precision")));
			} else {
				return getSaveDouble("xp");
			}
		}
		if (plugin.getEconomy() == null || plugin.config.getString("Economy.Type").equals("TOKEN")) {
			return getSaveDouble("tokens");
		}
		return plugin.getEconomy().getBalance(player);
	}

	/**
	 * Set's the player's balance based on the server's config.yml If the currency
	 * is XP, this will modify the player's XP too
	 * 
	 * @param bal
	 */
	public void setBalance(double bal) {
		if (plugin.config.getString("Economy.Type").equals("XP")) {
			if (player.isOnline()) {
				((Player) player).setLevel((int) Math.floor(bal));
				((Player) player).setExp((float) (bal - Math.floor(bal)));
				return;
			} else {
				setSaveData("xp", bal);
			}
		}
		if (plugin.getEconomy() == null || plugin.config.getString("Economy.Type").equals("TOKEN")) {
			setSaveData("tokens", bal);
			return;
		}
		plugin.getEconomy().depositPlayer(player, bal - getBalance());
	}
}
