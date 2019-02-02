package org.mswsplex.enchants.msws;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mswsplex.enchants.bstats.MetricsLite;
import org.mswsplex.enchants.checkers.armor.AlarmerChecker;
import org.mswsplex.enchants.checkers.armor.ArmorChecker;
import org.mswsplex.enchants.checkers.armor.BurningCheck;
import org.mswsplex.enchants.checkers.armor.DoubleJumpCheck;
import org.mswsplex.enchants.checkers.armor.FrostWalkerCheck;
import org.mswsplex.enchants.checkers.armor.NetherWalkerCheck;
import org.mswsplex.enchants.checkers.armor.SelfDestructCheck;
import org.mswsplex.enchants.checkers.armor.SoftTouchCheck;
import org.mswsplex.enchants.checkers.armor.SummonerCheck;
import org.mswsplex.enchants.checkers.axe.ChuckerCheck;
import org.mswsplex.enchants.checkers.axe.RecallCheck;
import org.mswsplex.enchants.checkers.axe.TreeFellerCheck;
import org.mswsplex.enchants.checkers.bow.BarrageCheck;
import org.mswsplex.enchants.checkers.bow.EnderShotCheck;
import org.mswsplex.enchants.checkers.bow.ExplosiveCheck;
import org.mswsplex.enchants.checkers.bow.StunCheck;
import org.mswsplex.enchants.checkers.bow.ToxicShotCheck;
import org.mswsplex.enchants.checkers.bow.WitherShotCheck;
import org.mswsplex.enchants.checkers.pickaxe.AutoGrabCheck;
import org.mswsplex.enchants.checkers.pickaxe.AutoSmeltCheck;
import org.mswsplex.enchants.checkers.pickaxe.ExcavationCheck;
import org.mswsplex.enchants.checkers.pickaxe.ExplosionCheck;
import org.mswsplex.enchants.checkers.pickaxe.ExtraXPCheck;
import org.mswsplex.enchants.checkers.pickaxe.OreSeekingCheck;
import org.mswsplex.enchants.checkers.sword.ChainReactionCheck;
import org.mswsplex.enchants.checkers.sword.FreezeCheck;
import org.mswsplex.enchants.checkers.sword.NightshadeCheck;
import org.mswsplex.enchants.checkers.sword.RageCheck;
import org.mswsplex.enchants.checkers.sword.ReviveCheck;
import org.mswsplex.enchants.checkers.sword.SeveredCheck;
import org.mswsplex.enchants.checkers.sword.StormbreakerCheck;
import org.mswsplex.enchants.checkers.sword.ToxicPointCheck;
import org.mswsplex.enchants.checkers.sword.TripperCheck;
import org.mswsplex.enchants.checkers.sword.WitherPointCheck;
import org.mswsplex.enchants.commands.AddEnchantmentCommand;
import org.mswsplex.enchants.commands.EnchanterCommand;
import org.mswsplex.enchants.commands.GiveEnchantCommand;
import org.mswsplex.enchants.commands.RedeemCommand;
import org.mswsplex.enchants.commands.TokenCommand;
import org.mswsplex.enchants.enchants.EnchantmentManager;
import org.mswsplex.enchants.listeners.NPCListener;
import org.mswsplex.enchants.listeners.OnLeaveListener;
import org.mswsplex.enchants.listeners.RedeemGUIListener;
import org.mswsplex.enchants.listeners.ShopListener;
import org.mswsplex.enchants.listeners.UpdateJoinListener;
import org.mswsplex.enchants.listeners.XPJoinListener;
import org.mswsplex.enchants.managers.CPlayer;
import org.mswsplex.enchants.managers.PlayerManager;
import org.mswsplex.enchants.papi.PAPIHook;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.NBTEditor;
import org.mswsplex.enchants.utils.Utils;

import net.milkbowl.vault.economy.Economy;

public class FreakyEnchants extends JavaPlugin {
	public FileConfiguration config, data, lang, gui, enchantCosts;
	public File configYml = new File(getDataFolder(), "config.yml"), dataYml = new File(getDataFolder(), "data.yml"),
			langYml = new File(getDataFolder(), "lang.yml"), guiYml = new File(getDataFolder(), "guis.yml"),
			enchantCostsYml = new File(getDataFolder(), "costs.yml");

	private EnchantmentManager eManager;
	private PlayerManager pManager;

	private Economy eco = null;

	private String onlineVer = "unknown";

	List<String> changelog;

	public void onEnable() {
		if (!configYml.exists())
			saveResource("config.yml", true);
		if (!langYml.exists())
			saveResource("lang.yml", true);
		if (!guiYml.exists())
			saveResource("guis.yml", true);
		if (!enchantCostsYml.exists())
			saveResource("costs.yml", true);
		config = YamlConfiguration.loadConfiguration(configYml);
		data = YamlConfiguration.loadConfiguration(dataYml);
		lang = YamlConfiguration.loadConfiguration(langYml);
		gui = YamlConfiguration.loadConfiguration(guiYml);
		enchantCosts = YamlConfiguration.loadConfiguration(enchantCostsYml);

		MSG.plugin = this;
		Utils.plugin = this;

		eManager = new EnchantmentManager(this);
		pManager = new PlayerManager(this);

		changelog = new ArrayList<>();

		if (config.getBoolean("Updater.OnEnable")) {
			onlineVer = Utils.getSpigotVersion(64154);

			if (onlineVer == null) {
				MSG.log(lang.getString("Outdated.Error"));
			} else if (MSG.outdated(getDescription().getVersion(), onlineVer)) {
				MSG.log(lang.getString("Outdated.Console").replace("%ver%", getDescription().getVersion())
						.replace("%oVer%", onlineVer));

				if (config.getBoolean("Changelog.OnEnable"))
					try {
						URL u = new URL("https://raw.githubusercontent.com/MSWS/FreakyEnchants/master/changelog.txt");
						URLConnection conn = u.openConnection();
						BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						String inputLine;
						while ((inputLine = in.readLine()) != null) {
							MSG.log(inputLine);
							changelog.add(inputLine);
						}
						in.close();
					} catch (Exception e) {
						MSG.log("Unable to grab latest changelog.");
					}
			}
		}

		List<String> links = new ArrayList<>();

		if (setupEconomy()) {
			links.add("Vault");
		} else {
			MSG.log("Vault not found, disabling vault-reliant economy.");
		}

		if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
			links.add("WorldGuard");
		}

		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			new PAPIHook(this).register();
			links.add("PlaceholderAPI");
		}

		if (links.size() == 0) {
			MSG.log("No dependencies detected,");
		} else if (links.size() == 1) {
			MSG.log("Successfully linked with " + links.get(0));
		} else if (links.size() == 2) {
			MSG.log("Successfully linked with " + links.get(0) + " and " + links.get(1));
		} else {
			String linkMsg = "";
			for (int i = 0; i < links.size(); i++) {
				if (i == links.size() - 2) {
					linkMsg += links.get(i) + ", and ";
				} else {
					linkMsg += links.get(i) + ", ";
				}
			}
			linkMsg = linkMsg.substring(0, linkMsg.length() - 2);
			MSG.log("Successfully linked with " + linkMsg + ".");
		}

		String msg = "";
		if (!config.contains("ConfigVersion")) {
			msg = "&4[WARNING] &cYour config version is out of date. Resetting your config is highly recommended.";
		} else {
			if (config.getString("ConfigVersion").equals(getDescription().getVersion())) {
				msg = "Your config is up to date and should be compatible with this version.";
			} else {
				switch (config.getString("ConfigVersion")) {
				case "1.0.5":
				case "1.0.4":
					msg = "[NOTE] Your config is slightly out of date.|[NOTE] There are no extreme differences, resetting is up to you.";
					break;
				case "1.0.3":
					msg = "[WARNING] Your config is slightly out of date. It is recommended you reset it.|[WARNING] However, it could work fine without resetting.";
					break;
				default:
					msg = "Your config version is severely out of date and it is highly recommended you reset it.";
					break;
				}
			}
		}

		for (String l : msg.split("\\|"))
			MSG.log(l);
		MSG.log("You can view the latest default config at &ahttp://bit.ly/FreakyConfig");
		MSG.log("It is recommended you keep up to date due to new versions constantly being released.");

		new AddEnchantmentCommand(this);
		new TokenCommand(this);
		new EnchanterCommand(this);
		new RedeemCommand(this);
		new GiveEnchantCommand(this);
		new UpdateJoinListener(this);
		new XPJoinListener(this);
		new OnLeaveListener(this);

		new ShopListener(this);
		new NPCListener(this);
		new RedeemGUIListener(this);

		new MetricsLite(this);

		registerEnchantChecks();
		refreshNPCs();
	}

	public void onDisable() {
		for (OfflinePlayer p : pManager.getLoadedPlayers())
			pManager.removePlayer(p);
		saveData();
	}

	public void registerEnchantChecks() {
		new ArmorChecker(this);

		new ExplosionCheck(this);
		new ExcavationCheck(this);
		new AutoSmeltCheck(this);
		new WitherPointCheck(this);
		new ToxicPointCheck(this);
		new ReviveCheck(this);
		new FreezeCheck(this);
		new StormbreakerCheck(this);
		new NightshadeCheck(this);
		new SummonerCheck(this);
		new SeveredCheck(this);
		new ExplosiveCheck(this);
		new StunCheck(this);
		new WitherShotCheck(this);
		new ToxicShotCheck(this);
		new RageCheck(this);
		new AutoGrabCheck(this);
		new BarrageCheck(this);
		new ExtraXPCheck(this);
		new SelfDestructCheck(this);
		new EnderShotCheck(this);
		new BurningCheck(this);
		new TreeFellerCheck(this);
		new FrostWalkerCheck(this);
		new TripperCheck(this);
		new DoubleJumpCheck(this);
		new ChainReactionCheck(this);
		new OreSeekingCheck(this);
		new AlarmerChecker(this);
		new ChuckerCheck(this);
		new RecallCheck(this);
		new SoftTouchCheck(this);
		new NetherWalkerCheck(this);
	}

	/**
	 * Returns a custom enchantment by id See
	 * {@link https://github.com/MSWS/FreakyEnchants/wiki/Enchantments} for
	 * enchantment ids
	 * 
	 * @param id Should be lower-cased, no special characters, no spaces, no dashes
	 * @return Enchantment, null if not found
	 */
	public Enchantment getEnchant(String id) {
		return eManager.enchants.get(id.toLowerCase());
	}

	/**
	 * Returns the PlayerManager. Use this for player data
	 * 
	 * @return
	 */
	public PlayerManager getPlayerManager() {
		return this.pManager;
	}

	/**
	 * Returns CPlayer
	 * 
	 * @see CPlayer
	 * @param player
	 * @return If player data is not loaded, new playerdata will be loaded
	 */
	public CPlayer getCPlayer(OfflinePlayer player) {
		return pManager.getPlayer(player);
	}

	/**
	 * Returns utils class
	 * 
	 * @return
	 */
	public Utils getUtils() {
		return new Utils();
	}

	/**
	 * Deletes and reloads all NPCs
	 */
	public void refreshNPCs() {
		deleteNPCs();
		loadNPCs();
	}

	/**
	 * Deletes all loaded NPC's Does not delete them from the data file
	 */
	public void deleteNPCs() {
		for (World w : Bukkit.getWorlds()) {
			for (Entity ent : w.getEntities()) {
				if (ent.hasMetadata("isNPC")) {
					Utils.getEntity(ent.getMetadata("holoID").get(0).asString(), ent.getWorld()).remove();
					ent.remove();
				}
			}
		}
	}

	/**
	 * Loads in all NPC's Note this will not remove currently loaded NPC's
	 */
	public void loadNPCs() {
		ConfigurationSection npcs = data.getConfigurationSection("NPC");
		if (npcs != null) {
			for (String entry : npcs.getKeys(false)) {
				Location loc = (Location) npcs.get(entry);
				Entity ent = loc.getWorld().spawnEntity(loc, EntityType.valueOf(config.getString("NPC.Type")));
				NBTEditor.setEntityTag(ent, 1, "NoAI");
				NBTEditor.setEntityTag(ent, 1, "Silent");
				ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(
						loc.clone().add(0, Utils.getEntityHeight(ent.getType()) - 2, 0), EntityType.ARMOR_STAND);
				stand.setVisible(false);
				stand.setCustomName(MSG.color(config.getString("NPC.Name")));
				stand.setCustomNameVisible(true);
				stand.setGravity(false);

				ent.setMetadata("isNPC", new FixedMetadataValue(this, entry));
				ent.setMetadata("holoID", new FixedMetadataValue(this, stand.getUniqueId() + ""));
			}
		}
	}

	/**
	 * CREATES and SAVES TO FILE an NPC Do NOT use this to respawn an NPC as this
	 * will duplicate NPCs
	 * 
	 * @param loc
	 */
	public void createSavedNPC(Location loc) {
		Entity ent = loc.getWorld().spawnEntity(loc, EntityType.valueOf(config.getString("NPC.Type")));
		NBTEditor.setEntityTag(ent, 1, "NoAI");
		NBTEditor.setEntityTag(ent, 1, "Silent");
		ArmorStand stand = (ArmorStand) loc.getWorld()
				.spawnEntity(loc.clone().add(0, Utils.getEntityHeight(ent.getType()) - 2, 0), EntityType.ARMOR_STAND);
		stand.setVisible(false);
		stand.setCustomName(MSG.color(config.getString("NPC.Name")));
		stand.setCustomNameVisible(true);
		stand.setGravity(false);
		int pos = 0;
		while (data.contains("NPC." + pos))
			pos++;
		data.set("NPC." + pos, loc);
		ent.setMetadata("isNPC", new FixedMetadataValue(this, pos));
		ent.setMetadata("holoID", new FixedMetadataValue(this, stand.getUniqueId() + ""));
	}

	public void saveData() {
		try {
			data.save(dataYml);
		} catch (Exception e) {
			MSG.log("&cError saving data file");
			MSG.log("&a----------Start of Stack Trace----------");
			e.printStackTrace();
			MSG.log("&a----------End of Stack Trace----------");
		}
	}

	public void saveConfig() {
		try {
			config.save(configYml);
		} catch (Exception e) {
			MSG.log("&cError saving data file");
			MSG.log("&a----------Start of Stack Trace----------");
			e.printStackTrace();
			MSG.log("&a----------End of Stack Trace----------");
		}
	}

	public void saveCosts() {
		try {
			enchantCosts.save(enchantCostsYml);
		} catch (Exception e) {
			MSG.log("&cError saving file");
			MSG.log("&a----------Start of Stack Trace----------");
			e.printStackTrace();
			MSG.log("&a----------End of Stack Trace----------");
		}
	}

	public EnchantmentManager getEnchManager() {
		return eManager;
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		eco = rsp.getProvider();
		return eco != null;
	}

	public Economy getEconomy() {
		return this.eco;
	}

	public String getOnlineVer() {
		return onlineVer;
	}

	public List<String> getChangelog() {
		return changelog;
	}
}
