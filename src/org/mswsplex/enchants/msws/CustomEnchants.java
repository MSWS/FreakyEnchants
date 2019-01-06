package org.mswsplex.enchants.msws;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.mswsplex.enchants.checkers.ArmorChecker;
import org.mswsplex.enchants.checkers.AutoSmeltCheck;
import org.mswsplex.enchants.checkers.ExcavationCheck;
import org.mswsplex.enchants.checkers.ExplosionCheck;
import org.mswsplex.enchants.checkers.ExplosiveCheck;
import org.mswsplex.enchants.checkers.FreezeCheck;
import org.mswsplex.enchants.checkers.NightshadeCheck;
import org.mswsplex.enchants.checkers.PoisonPointCheck;
import org.mswsplex.enchants.checkers.ReviveCheck;
import org.mswsplex.enchants.checkers.StormbreakerCheck;
import org.mswsplex.enchants.checkers.StunCheck;
import org.mswsplex.enchants.checkers.SummonerCheck;
import org.mswsplex.enchants.checkers.ToxicShotCheck;
import org.mswsplex.enchants.checkers.WitherPointCheck;
import org.mswsplex.enchants.checkers.WitherShotCheck;
import org.mswsplex.enchants.commands.AddEnchantmentCommand;
import org.mswsplex.enchants.commands.TokenCommand;
import org.mswsplex.enchants.enchants.EnchantmentManager;
import org.mswsplex.enchants.managers.PlayerManager;
import org.mswsplex.enchants.utils.MSG;

public class CustomEnchants extends JavaPlugin {
	public FileConfiguration config, data, lang, gui;
	public File configYml = new File(getDataFolder(), "config.yml"), dataYml = new File(getDataFolder(), "data.yml"),
			langYml = new File(getDataFolder(), "lang.yml"), guiYml = new File(getDataFolder(), "guis.yml");

	private EnchantmentManager eManager;

	@SuppressWarnings("deprecation")
	public void onEnable() {
		if (!configYml.exists())
			saveResource("config.yml", true);
		if (!langYml.exists())
			saveResource("lang.yml", true);
		if (!guiYml.exists())
			saveResource("guis.yml", true);
		config = YamlConfiguration.loadConfiguration(configYml);
		data = YamlConfiguration.loadConfiguration(dataYml);
		lang = YamlConfiguration.loadConfiguration(langYml);
		gui = YamlConfiguration.loadConfiguration(guiYml);

		eManager = new EnchantmentManager(this);

		MSG.plugin = this;
		PlayerManager.plugin = this;
		
		new AddEnchantmentCommand(this);
		new TokenCommand(this);

		new ExplosionCheck(this);
		new ExcavationCheck(this);
		new AutoSmeltCheck(this);
		new WitherPointCheck(this);
		new PoisonPointCheck(this);
		new ReviveCheck(this);
		new FreezeCheck(this);
		new StormbreakerCheck(this);
		new NightshadeCheck(this);
		new SummonerCheck(this);
		new ExplosiveCheck(this);
		new StunCheck(this);
		new WitherShotCheck(this);
		new ToxicShotCheck(this);

		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new ArmorChecker(this), 0, 5);



		MSG.log("&aSuccessfully Enabled!");
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

	public EnchantmentManager getEnchantmentManager() {
		return eManager;
	}
}
