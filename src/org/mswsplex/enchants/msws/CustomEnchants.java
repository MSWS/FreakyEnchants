package org.mswsplex.enchants.msws;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mswsplex.enchants.checkers.ArmorChecker;
import org.mswsplex.enchants.checkers.AutoGrabCheck;
import org.mswsplex.enchants.checkers.AutoSmeltCheck;
import org.mswsplex.enchants.checkers.BarrageCheck;
import org.mswsplex.enchants.checkers.EnderShotCheck;
import org.mswsplex.enchants.checkers.ExcavationCheck;
import org.mswsplex.enchants.checkers.ExplosionCheck;
import org.mswsplex.enchants.checkers.ExplosiveCheck;
import org.mswsplex.enchants.checkers.ExtraXPCheck;
import org.mswsplex.enchants.checkers.FreezeCheck;
import org.mswsplex.enchants.checkers.NightshadeCheck;
import org.mswsplex.enchants.checkers.RageCheck;
import org.mswsplex.enchants.checkers.ReviveCheck;
import org.mswsplex.enchants.checkers.SelfDestructCheck;
import org.mswsplex.enchants.checkers.SeveredCheck;
import org.mswsplex.enchants.checkers.StormbreakerCheck;
import org.mswsplex.enchants.checkers.StunCheck;
import org.mswsplex.enchants.checkers.SummonerCheck;
import org.mswsplex.enchants.checkers.ToxicPointCheck;
import org.mswsplex.enchants.checkers.ToxicShotCheck;
import org.mswsplex.enchants.checkers.WitherPointCheck;
import org.mswsplex.enchants.checkers.WitherShotCheck;
import org.mswsplex.enchants.commands.AddEnchantmentCommand;
import org.mswsplex.enchants.commands.EnchanterCommand;
import org.mswsplex.enchants.commands.GiveEnchantCommand;
import org.mswsplex.enchants.commands.RedeemCommand;
import org.mswsplex.enchants.commands.TokenCommand;
import org.mswsplex.enchants.enchants.EnchantmentManager;
import org.mswsplex.enchants.listeners.NPCListener;
import org.mswsplex.enchants.listeners.RedeemGUIListener;
import org.mswsplex.enchants.listeners.ShopListener;
import org.mswsplex.enchants.managers.PlayerManager;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.NBTEditor;
import org.mswsplex.enchants.utils.Utils;

import net.milkbowl.vault.economy.Economy;

public class CustomEnchants extends JavaPlugin {
	public FileConfiguration config, data, lang, gui, enchantCosts;
	public File configYml = new File(getDataFolder(), "config.yml"), dataYml = new File(getDataFolder(), "data.yml"),
			langYml = new File(getDataFolder(), "lang.yml"), guiYml = new File(getDataFolder(), "guis.yml"),
			enchantCostsYml = new File(getDataFolder(), "enchantCosts.yml");

	private EnchantmentManager eManager;

	private Economy eco = null;

	public void onEnable() {
		if (!configYml.exists())
			saveResource("config.yml", true);
		if (!langYml.exists())
			saveResource("lang.yml", true);
		if (!guiYml.exists())
			saveResource("guis.yml", true);
		if (!enchantCostsYml.exists())
			saveResource("enchantCosts.yml", true);
		config = YamlConfiguration.loadConfiguration(configYml);
		data = YamlConfiguration.loadConfiguration(dataYml);
		lang = YamlConfiguration.loadConfiguration(langYml);
		gui = YamlConfiguration.loadConfiguration(guiYml);
		enchantCosts = YamlConfiguration.loadConfiguration(enchantCostsYml);
		eManager = new EnchantmentManager(this);

		MSG.plugin = this;
		Utils.plugin = this;
		PlayerManager.plugin = this;

		if (setupEconomy()) {
			MSG.log("Successfully linked with Vault.");
		} else {
			MSG.log("Vault not found, using Tokens as currency.");
		}

		if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard"))
			MSG.log("WorldGuard successfully linked.");

		new AddEnchantmentCommand(this);
		new TokenCommand(this);
		new EnchanterCommand(this);
		new RedeemCommand(this);
		new GiveEnchantCommand(this);

		new ShopListener(this);
		new NPCListener(this);
		new RedeemGUIListener(this);

		registerEnchantChecks();
		refreshNPCs();
	}

	public void onDisable() {
		saveData();
	}

	@SuppressWarnings("deprecation")
	public void registerEnchantChecks() {
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
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new ArmorChecker(this), 0, 5);
	}

	public void refreshNPCs() {
		deleteNPCs();
		loadNPCs();
	}

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

	public EnchantmentManager getEnchantmentManager() {
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
}
