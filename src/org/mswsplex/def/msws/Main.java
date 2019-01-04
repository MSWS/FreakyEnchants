package org.mswsplex.def.msws;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.mswsplex.def.commands.DefaultCommand;
import org.mswsplex.def.events.Events;
import org.mswsplex.def.managers.PlayerManager;
import org.mswsplex.def.utils.MSG;

public class Main extends JavaPlugin {
	public FileConfiguration config, data, lang, gui;
	public File configYml = new File(getDataFolder(), "config.yml"), dataYml = new File(getDataFolder(), "data.yml"),
			langYml = new File(getDataFolder(), "lang.yml"), guiYml = new File(getDataFolder(), "guis.yml");

	public void onEnable() {
		if(!configYml.exists())
			saveResource("config.yml", true);
		if(!langYml.exists())
			saveResource("lang.yml", true);
		if(!guiYml.exists())
			saveResource("guis.yml", true);
		config = YamlConfiguration.loadConfiguration(configYml);
		data = YamlConfiguration.loadConfiguration(dataYml);
		lang = YamlConfiguration.loadConfiguration(langYml);
		gui = YamlConfiguration.loadConfiguration(guiYml);
		
		new DefaultCommand(this);
		new Events(this);
		MSG.plugin = this;
		PlayerManager.plugin = this;
		
		MSG.log("&aSuccessfully Enabled!");
	}
	
	public void saveData() {
		try {
			data.save(dataYml);
		}catch(Exception e) {
			MSG.log("&cError saving data file");
			MSG.log("&a----------Start of Stack Trace----------");
			e.printStackTrace();
			MSG.log("&a----------End of Stack Trace----------");
		}
	}
	
	public void saveConfig() {
		try {
			config.save(configYml);
		}catch(Exception e) {
			MSG.log("&cError saving data file");
			MSG.log("&a----------Start of Stack Trace----------");
			e.printStackTrace();
			MSG.log("&a----------End of Stack Trace----------");
		}
	}
}
