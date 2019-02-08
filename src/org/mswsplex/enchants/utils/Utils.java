package org.mswsplex.enchants.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.mswsplex.enchants.managers.CPlayer;
import org.mswsplex.enchants.msws.FreakyEnchants;

public class Utils {
	public static FreakyEnchants plugin;

	/**
	 * Returns a ranking of all the armor from value
	 * 
	 * @param mat Material to compare
	 * @return Diamond: 4 Iron: 3 Chain: 2 Gold: 1 Leather: 0 Default: 0
	 */
	public static int getArmorValue(Material mat) {
		switch (getArmorType(mat).toLowerCase()) {
		case "diamond":
			return 4;
		case "iron":
			return 3;
		case "chainmail":
			return 2;
		case "gold":
			return 1;
		case "leather":
			return 0;
		default:
			return 0;
		}
	}

	/**
	 * Gets the armor slot that a type of armor should be in
	 * 
	 * @param type Material type (DIAMOND_CHESTPLATE, IRON_LEGGINGS, etc)
	 * @return Armor slot Helmet: 3 Chestplate: 2 Leggings: 1 Boots: 0
	 */
	public static int getSlot(Material type) {
		if (!type.name().contains("_"))
			return 0;
		switch (type.name().split("_")[1]) {
		case "HELMET":
			return 3;
		case "CHESTPLATE":
			return 2;
		case "LEGGINGS":
			return 1;
		case "BOOTS":
			return 0;
		}
		return 0;
	}

	/**
	 * Returns type of armor
	 * 
	 * @param mat Material to get type of
	 * @return DIAMOND, IRON, GOLD, CHAINMAIL
	 */
	public static String getArmorType(Material mat) {
		if (!mat.name().contains("_")) {
			return "";
		}
		String name = mat.name().split("_")[0];
		return name;
	}

	/**
	 * Returns if the specified material is armor
	 * 
	 * @param mat Material to check
	 * @return True if armor, false otherwise
	 */
	public static boolean isArmor(Material mat) {
		return mat.name().contains("CHESTPLATE") || mat.name().contains("LEGGINGS") || mat.name().contains("HELMET")
				|| mat.name().contains("BOOTS");
	}

	/**
	 * Returns a sound that a block would play if placed/broken
	 * 
	 * @param mat Material to check
	 * @return Sound closest, DIG_GRASS if unmatched
	 */
	public static Sound getBreakSound(Material mat) {
		if (mat.name().contains("GLOW") || mat.name().contains("GLASS"))
			return Sound.GLASS;
		if (mat.name().contains("STONE"))
			return Sound.DIG_STONE;
		if (mat.name().contains("SAND"))
			return Sound.DIG_SAND;
		if (mat.name().contains("SNOW"))
			return Sound.DIG_SNOW;
		if (mat.name().contains("WOOD") || mat.name().contains("LOG"))
			return Sound.DIG_WOOD;
		if (mat.name().contains("ORE"))
			return Sound.DIG_STONE;
		switch (mat.name()) {
		case "GRAVEL":
			return Sound.DIG_GRAVEL;
		case "GRASS":
		case "DIRT":
			return Sound.DIG_GRASS;
		case "WOOL":
			return Sound.DIG_WOOL;
		default:
			return Sound.DIG_GRASS;
		}
	}

	/**
	 * Gets a block based on the blockface
	 * 
	 * @param block Block to compare face to
	 * @param face  Relative face to get block
	 * @return
	 */
	public static Block blockFromFace(Block block, BlockFace face) {
		int x = 0, y = 0, z = 0;
		if (face == BlockFace.EAST)
			x = 1;
		if (face == BlockFace.WEST)
			x = -1;
		if (face == BlockFace.NORTH)
			z = -1;
		if (face == BlockFace.SOUTH)
			z = 1;
		if (face == BlockFace.UP)
			y = 1;
		if (face == BlockFace.DOWN)
			y = -1;
		return block.getLocation().add(x, y, z).getBlock();
	}

	/**
	 * Returns parsed Inventory from YAML config (guis.yml)
	 * 
	 * @param player Player to parse information with (%player% and other
	 *               placeholders)
	 * @param id     Name of the inventory to parse
	 * @param page   Page of the inventory
	 * @return
	 */
	public static Inventory getGui(OfflinePlayer player, String id, int page) {
		if (!plugin.gui.contains(id))
			return null;
		ConfigurationSection gui = plugin.gui.getConfigurationSection(id);
		if (!gui.contains("Size") || !gui.contains("Title"))
			return null;
		String title = gui.getString("Title").replace("%player%", player.getName());
		if (player.isOnline())
			title = title.replace("%world%", ((Player) player).getWorld().getName());
		title = title.replace("%world%", "");
		Inventory inv = Bukkit.createInventory(null, gui.getInt("Size"), MSG.color(title));
		ItemStack bg = null;
		boolean empty = true;
		for (String res : gui.getKeys(false)) {
			if (!gui.contains(res + ".Icon"))
				continue;
			empty = false;
			if (gui.contains(res + ".Page")) {
				if (page != gui.getInt(res + ".Page"))
					continue;
			} else if (page != 0)
				continue;
			if (player.isOnline()) {
				if (gui.contains(res + ".Permission")
						&& !((Player) player).hasPermission(gui.getString(res + ".Permission"))) {
					continue;
				}
			}
			ItemStack item = parseItem(plugin.gui, id + "." + res, player);
			if (res.equals("BACKGROUND_ITEM")) {
				bg = item;
				continue;
			}
			int slot = 0;
			if (!gui.contains(res + ".Slot")) {
				while (inv.getItem(slot) != null)
					slot++;
				inv.setItem(slot, item);
			} else {
				inv.setItem(gui.getInt(res + ".Slot"), item);
			}
		}
		if (empty)
			return null;
		if (bg != null) {
			for (int i = 0; i < inv.getSize(); i++) {
				if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) {
					inv.setItem(i, bg);
				}
			}
		}
		return inv;
	}

	/**
	 * Parses and returns an item from the specified YAML Path Supports
	 * enchantments, damage values, amounts, skulls, lores, and unbreakable
	 * 
	 * @param section Section to get item from
	 * @param path    Specified path after section
	 * @param player  Player to parse the items with (for %player% and other
	 *                placeholders)
	 * @return Parsed ItemStack
	 */
	public static ItemStack parseItem(ConfigurationSection section, String path, OfflinePlayer player) {
		CPlayer cp = plugin.getCPlayer(player);

		ConfigurationSection gui = section.getConfigurationSection(path);
		ItemStack item = new ItemStack(Material.valueOf(gui.getString("Icon")));
		List<String> lore = new ArrayList<String>();
		String enchName = path.split("\\.")[path.split("\\.").length - 1];
		if (gui.contains("Amount"))
			item.setAmount(gui.getInt("Amount"));
		if (plugin.getEnchManager().enchants.containsKey(enchName)) {
			item.setAmount(plugin.getEnchant(enchName).getStartLevel() + 1);
		}
		if (cp.hasTempData(enchName))
			item.setAmount(cp.getTempInteger(enchName));

		if (gui.contains("Data"))
			item.setDurability((short) gui.getInt("Data"));
		if (gui.contains("Owner")) {
			SkullMeta meta = (SkullMeta) item.getItemMeta();
			meta.setOwner(gui.getString("Owner"));
			item.setItemMeta(meta);
		}
		ItemMeta meta = item.getItemMeta();
		if (gui.contains("Name"))
			meta.setDisplayName(MSG.color("&r" + gui.getString("Name").replace("%balance%", cp.getBalance() + "")));
		if (gui.contains("Lore")) {
			for (String temp : gui.getStringList("Lore"))
				lore.add(MSG.color("&r" + temp.replace("%balance%", cp.getBalance() + "")));
		}
		if (gui.getBoolean("Unbreakable")) {
			meta.spigot().setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		}

		if (gui.getBoolean("HideAttributes")) {
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
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

		if (plugin.enchantCosts.contains(enchName)) {
			if (gui.contains("Name"))
				meta.setDisplayName(meta.getDisplayName().replace("%level%", MSG.toRoman(item.getAmount())));

			if (plugin.getEnchant(enchName).getMaxLevel() != 1) {
				plugin.config.getStringList("EnchantmentSuffix.Level").forEach((line) -> {
					lore.add(MSG.color(line.replace("%level%", plugin.getEnchant(enchName).getMaxLevel() + "")));
				});
			}
			plugin.config.getStringList("EnchantmentSuffix.Price").forEach((line) -> {
				lore.add(MSG.color(line
						.replace("%price%", plugin.enchantCosts.getDouble(enchName + "." + item.getAmount()) + "")
						.replace("%s%",
								plugin.enchantCosts.getDouble(enchName + "." + item.getAmount()) == 1 ? "" : "s")));
			});
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	@SuppressWarnings("unchecked")
	public static Inventory getRedeemGUI(Player player) {
		CPlayer cp = plugin.getCPlayer(player);
		List<String> tokens = (List<String>) cp.getSaveData("enchantmentTokens");
		if (tokens == null)
			tokens = new ArrayList<String>();
		Collections.sort(tokens);
		int maxSize = 54;
		int size = (int) Math.min(Math.max((Math.ceil(tokens.size() / 9.0) * 9), 9), maxSize);
		int page = cp.getTempInteger("page");
		Inventory inv = Bukkit.createInventory(null, size, "Token Redeemer");
		if (tokens.size() == 0) {
			for (int i = 0; i < inv.getSize(); i++) {
				inv.setItem(i, parseItem(plugin.config, "NoTokens", player));
			}
			return inv;
		}
		int pos = (maxSize - 2) * page;
		for (int i = 0; i < size && i + (page * (maxSize - 2)) + 1 <= tokens.size(); i++) {
			if (inv.getSize() == maxSize && (i == inv.getSize() - 9 || i == inv.getSize() - 1))
				continue;
			String enchant = tokens.get(pos);
			int level = Integer.parseInt(enchant.split(" ")[1]);
			ItemStack item = new ItemStack(Material.valueOf(enchant.split(" ")[2]),
					plugin.config.getBoolean("TokenAmountUsesEnchantmentLevel") ? level : 1);
			ItemMeta meta = item.getItemMeta();
			meta.addEnchant(plugin.getEnchant(enchant.split(" ")[0]), level, true);
			meta.setDisplayName(MSG.color(plugin.config.getString("TokenTitle")));
			meta.setLore(Arrays.asList(
					MSG.color("&7" + plugin.getEnchant(enchant.split(" ")[0]).getName() + " " + MSG.toRoman(level)), "",
					MSG.color(plugin.config.getString("TokenDeleteLine"))));
			item.setItemMeta(meta);
			inv.setItem(i, item);
			pos++;
		}

		if (page * (maxSize - 2) + (maxSize - 2) < tokens.size()) {
			ItemStack nextArrow = new ItemStack(Material.ARROW);
			ItemMeta meta = nextArrow.getItemMeta();
			meta.setDisplayName(MSG.color("&a&lNext Page"));
			nextArrow.setItemMeta(meta);
			inv.setItem(inv.getSize() - 1, nextArrow);
		}

		if (page > 0) {
			ItemStack lastArrow = new ItemStack(Material.ARROW);
			ItemMeta lastMeta = lastArrow.getItemMeta();
			lastMeta.setDisplayName(MSG.color("&c&lLast Page"));
			lastArrow.setItemMeta(lastMeta);
			inv.setItem(inv.getSize() - 9, lastArrow);
		}

		return inv;
	}

	/**
	 * Calculates a player's total exp based on level and progress to next.
	 * 
	 * @see http://minecraft.gamepedia.com/Experience#Leveling_up
	 * 
	 * @param player the Player
	 * 
	 * @return the amount of exp the Player has
	 */
	public static int getExp(Player player) {
		return getExpFromLevel(player.getLevel()) + Math.round(getExpToNext(player.getLevel()) * player.getExp());
	}

	/**
	 * Calculates total experience based on level.
	 * 
	 * @see http://minecraft.gamepedia.com/Experience#Leveling_up
	 * 
	 *      "One can determine how much experience has been collected to reach a
	 *      level using the equations:
	 * 
	 *      Total Experience = [Level]2 + 6[Level] (at levels 0-15) 2.5[Level]2 -
	 *      40.5[Level] + 360 (at levels 16-30) 4.5[Level]2 - 162.5[Level] + 2220
	 *      (at level 31+)"
	 * 
	 * @param level the level
	 * 
	 * @return the total experience calculated
	 */
	public static int getExpFromLevel(int level) {
		if (level > 30) {
			return (int) (4.5 * level * level - 162.5 * level + 2220);
		}
		if (level > 15) {
			return (int) (2.5 * level * level - 40.5 * level + 360);
		}
		return level * level + 6 * level;
	}

	/**
	 * Calculates level based on total experience.
	 * 
	 * @param exp the total experience
	 * 
	 * @return the level calculated
	 */
	public static double getLevelFromExp(long exp) {
		if (exp > 1395) {
			return (Math.sqrt(72 * exp - 54215) + 325) / 18;
		}
		if (exp > 315) {
			return Math.sqrt(40 * exp - 7839) / 10 + 8.1;
		}
		if (exp > 0) {
			return Math.sqrt(exp + 9) - 3;
		}
		return 0;
	}

	/**
	 * @see http://minecraft.gamepedia.com/Experience#Leveling_up
	 * 
	 *      "The formulas for figuring out how many experience orbs you need to get
	 *      to the next level are as follows: Experience Required = 2[Current Level]
	 *      + 7 (at levels 0-15) 5[Current Level] - 38 (at levels 16-30) 9[Current
	 *      Level] - 158 (at level 31+)"
	 */
	private static int getExpToNext(int level) {
		if (level > 30) {
			return 9 * level - 158;
		}
		if (level > 15) {
			return 5 * level - 38;
		}
		return 2 * level + 7;
	}

	/**
	 * Change a Player's exp.
	 * <p>
	 * This method should be used in place of {@link Player#giveExp(int)}, which
	 * does not properly account for different levels requiring different amounts of
	 * experience.
	 * 
	 * @param player the Player affected
	 * @param exp    the amount of experience to add or remove
	 */
	public static void changeExp(Player player, int exp) {
		exp += getExp(player);

		if (exp < 0) {
			exp = 0;
		}

		double levelAndExp = getLevelFromExp(exp);

		int level = (int) levelAndExp;
		player.setLevel(level);
		player.setExp((float) (levelAndExp - level));
	}

	/**
	 * if oldVer is < newVer, both versions can only have numbers and .'s Outputs:
	 * 5.5, 10.3 | true 2.3.1, 3.1.4.6 | true 1.2, 1.1 | false
	 **/
	public static Boolean outdated(String oldVer, String newVer) {
		oldVer = oldVer.replace(".", "");
		newVer = newVer.replace(".", "");
		Double oldV = null, newV = null;
		try {
			oldV = Double.valueOf(oldVer);
			newV = Double.valueOf(newVer);
		} catch (Exception e) {
			MSG.log("&cError! &7Versions incompatible.");
			return false;
		}
		if (oldVer.length() > newVer.length()) {
			newV = newV * (10 * (oldVer.length() - newVer.length()));
		} else if (oldVer.length() < newVer.length()) {
			oldV = oldV * (10 * (newVer.length() - oldVer.length()));
		}
		return oldV < newV;
	}

	/**
	 * Returns the bukkit name of an enchantment
	 * 
	 * @param name "Nickname" of enchant (sharpness, infinity, power, etc.)
	 * @return String og Enchantment Enum
	 */
	public static String getEnchant(String name) {
		switch (name.toLowerCase().replace("_", "")) {
		case "power":
			return "ARROW_DAMAGE";
		case "flame":
			return "ARROW_FIRE";
		case "infinity":
		case "infinite":
			return "ARROW_INFINITE";
		case "punch":
		case "arrowkb":
			return "ARROW_KNOCKBACK";
		case "sharpness":
			return "DAMAGE_ALL";
		case "arthropods":
		case "spiderdamage":
		case "baneofarthropods":
			return "DAMAGE_ARTHORPODS";
		case "smite":
			return "DAMAGE_UNDEAD";
		case "depthstrider":
		case "waterwalk":
			return "DEPTH_STRIDER";
		case "efficiency":
			return "DIG_SPEED";
		case "unbreaking":
			return "DURABILITY";
		case "fireaspect":
		case "fire":
			return "FIRE_ASPECT";
		case "knockback":
		case "kb":
			return "KNOCKBACK";
		case "fortune":
			return "LOOT_BONUS_BLOCKS";
		case "looting":
			return "LOOT_BONUS_MOBS";
		case "luck":
			return "LUCK";
		case "lure":
			return "LURE";
		case "waterbreathing":
		case "respiration":
			return "OXYGEN";
		case "prot":
		case "protection":
			return "PROTECTION_ENVIRONMENTAL";
		case "blastprot":
		case "blastprotection":
			return "PROTECTION_EXPLOSIONS";
		case "feather":
		case "featherfalling":
			return "PROTECTION_FALL";
		case "fireprot":
		case "fireprotection":
			return "PROTECTION_FIRE";
		case "projectileprot":
		case "projectileprotection":
		case "projprot":
			return "PROTECTION_PROJECTILE";
		case "silktouch":
		case "silk":
			return "SILK_TOUCH";
		case "thorns":
			return "THORNS";
		case "aquaaffinity":
		case "aqua":
		case "waterworker":
			return "WATER_WORKER";
		}
		return name.toUpperCase();
	}

	/**
	 * Get the online plugin version from SpigotMC.org
	 * 
	 * @param id ID of the online resource
	 * @return Version
	 */
	public static String getSpigotVersion(int id) {
		try {
			HttpsURLConnection con = (HttpsURLConnection) new URL(
					"https://api.spigotmc.org/legacy/update.php?resource=" + id).openConnection();
			try (BufferedReader buffer = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
				return buffer.readLine();
			} catch (Exception ex) {
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 
	 * @param data Wool dye value
	 * @return ChatColor (&a, &b, etc.) for the matching data
	 */
	public static String colorByWoolData(short data) {
		switch ((data) == -1 ? 15 : (data % 16)) {
		case 12:
			return "&b";
		case 11:
			return "&e";
		case 10:
			return "&a";
		case 9:
			return "&d";
		case 8:
			return "&8";
		case 7:
			return "&7";
		case 6:
			return "&3";
		case 5:
			return "&5";
		case 4:
			return "&9";
		case 3:
			return "&6";
		case 2:
			return "&2";
		case 1:
			return "&4";
		case 0:
			return "&0";
		case 15:
			return "&f";
		case 14:
			return "&6";
		case 13:
			return "&d";
		}
		return "";
	}

	/**
	 * Delete a world file from the worlds (World should be unloaded first)
	 * 
	 * @param path File to delete
	 * @return If the world was successfully deleted
	 */
	public static boolean deleteWorld(File path) {
		if (path.exists()) {
			File files[] = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteWorld(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	/**
	 * Returns a list of all unloaded worlds
	 * 
	 * @param includeLoaded Whether or not to include loaded worlds
	 * @return List of the world names
	 */
	public static List<String> getUnloadedWorlds(boolean includeLoaded) {
		List<String> worlds = new ArrayList<>();
		if (includeLoaded) {
			for (World world : Bukkit.getWorlds())
				worlds.add(world.getName());
		}

		for (String res : Bukkit.getWorldContainer().list()) {
			File file = new File(Bukkit.getWorldContainer().toPath() + File.separator + res);
			if (isWorldFile(file) && !worlds.contains(file.getName()))
				worlds.add(file.getName());
		}
		return worlds;
	}

	/**
	 * Returns whether or not a file is a world file
	 * 
	 * @param file Path to check
	 * @return True/False
	 */
	public static boolean isWorldFile(File file) {
		if (file != null && file.list() != null)
			for (String r : file.list())
				if (r.equals("session.lock"))
					return true;
		return false;
	}

	public static Entity getEntity(String uuid, World world) {
		if (world == null) {
			for (World w : Bukkit.getWorlds()) {
				Entity e = getEntity(uuid, w);
				if (e != null)
					return e;
			}
		} else {
			for (Entity e : world.getEntities()) {
				if (e.getUniqueId().toString().equals(uuid))
					return e;
			}
		}
		return null;
	}

	public static Entity getEntity(UUID uuid, World world) {
		return getEntity(uuid + "", world);
	}

	public static boolean allowEnchant(World world, String ench) {
		if (plugin.config.getStringList("DisabledWorlds.All").contains(world.getName()))
			return false;
		return !plugin.config.getStringList("DisabledWorlds." + ench).contains(world.getName());
	}

	public static float getEntityHeight(EntityType type) {
		switch (type) {
		case GHAST:
			return 5;
		case ENDER_DRAGON:
		case WITHER:
			return 3.5f;
		case ENDERMAN:
			return 3;
		case GIANT:
			return 13;
		case CHICKEN:
		case GUARDIAN:
		case BAT:
		case CAVE_SPIDER:
		case SPIDER:
		case WOLF:
		case PIG:
		case OCELOT:
			return 1;
		case SILVERFISH:
			return .5f;
		case SHEEP:
			return 1.5f;
		case IRON_GOLEM:
		case WITCH:
			return 2.5f;
		default:
			return 2;
		}
	}

	public static int romanToDecimal(String romanNumber) {
		int decimal = 0;
		int lastNumber = 0;
		String romanNumeral = romanNumber.toUpperCase();
		/*
		 * operation to be performed on upper cases even if user enters roman values in
		 * lower case chars
		 */
		for (int x = romanNumeral.length() - 1; x >= 0; x--) {
			char convertToDecimal = romanNumeral.charAt(x);
			switch (convertToDecimal) {
			case 'M':
				decimal = processDecimal(1000, lastNumber, decimal);
				lastNumber = 1000;
				break;

			case 'D':
				decimal = processDecimal(500, lastNumber, decimal);
				lastNumber = 500;
				break;

			case 'C':
				decimal = processDecimal(100, lastNumber, decimal);
				lastNumber = 100;
				break;

			case 'L':
				decimal = processDecimal(50, lastNumber, decimal);
				lastNumber = 50;
				break;

			case 'X':
				decimal = processDecimal(10, lastNumber, decimal);
				lastNumber = 10;
				break;

			case 'V':
				decimal = processDecimal(5, lastNumber, decimal);
				lastNumber = 5;
				break;

			case 'I':
				decimal = processDecimal(1, lastNumber, decimal);
				lastNumber = 1;
				break;
			}
		}
		return decimal;
	}

	public static int processDecimal(int decimal, int lastNumber, int lastDecimal) {
		if (lastNumber > decimal) {
			return lastDecimal - decimal;
		} else {
			return lastDecimal + decimal;
		}
	}

	public static void playSound(FileConfiguration config, String path, Player player) {
		if (!config.getBoolean(path + ".Enabled") && config.isSet(path + ".Enabled"))
			return;

		player.playSound(player.getLocation(), Sounds.valueOf(config.getString(path + ".Name")).bukkitSound(),
				config.contains(path + ".Volume") ? (float) config.getDouble(path + ".Volume") : 1,
				config.contains(path + ".Pitch") ? (float) config.getDouble(path + ".Pitch") : 1);
	}

	public static void playSound(FileConfiguration config, String path, Location loc) {
		if (!config.getBoolean(path + ".Enabled") && config.isSet(path + ".Enabled"))
			return;

		loc.getWorld().playSound(loc, Sounds.valueOf(config.getString(path + ".Name")).bukkitSound(),
				config.contains(path + ".Volume") ? (float) config.getDouble(path + ".Volume") : 1,
				config.contains(path + ".Pitch") ? (float) config.getDouble(path + ".Pitch") : 1);
	}

	@SuppressWarnings("deprecation")
	public static void emptyInventory(Player player) {
		if (plugin.lang.getBoolean("InventoryFull.Title.Enabled")) {
			player.sendTitle(MSG.color(plugin.lang.getString("InventoryFull.Title.Toplayer")),
					MSG.color(plugin.lang.getString("InventoryFull.Title.Bottom")));

		}
		if (plugin.lang.getBoolean("InventoryFull.ActionBarMessage.Enabled")) {
			HotbarMessenger.sendHotBarMessage(player,
					MSG.color(plugin.lang.getString("InventoryFull.ActionBarMessage.Message")));
		}
		Utils.playSound(plugin.lang, "InventoryFull.Sound", player);
		MSG.tell(player, plugin.lang.getString("InventoryFull.ChatMessage"));
	}
}
