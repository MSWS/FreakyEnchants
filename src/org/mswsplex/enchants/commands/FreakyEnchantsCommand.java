package org.mswsplex.enchants.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Utils;

public class FreakyEnchantsCommand implements CommandExecutor, TabCompleter {

	private FreakyEnchants plugin;

	public FreakyEnchantsCommand(FreakyEnchants plugin) {
		this.plugin = plugin;
		PluginCommand cmd = this.plugin.getCommand("freakyenchants");
		cmd.setExecutor(this);
		cmd.setPermission("freakyenchants.command");
		cmd.setPermissionMessage(MSG.color(MSG.getString("NoPermission", "No permission")));
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			MSG.sendHelp(sender, "default");
			return true;
		}

		switch (args[0].toLowerCase()) {
		case "help":
			MSG.sendHelp(sender, "default");
			break;
		case "version":
			if (!sender.hasPermission("freakyenchants.command.version")) {
				MSG.noPerm(sender);
				return true;
			}
			String online = plugin.getOnlineVer();
			if (online == null) {
				MSG.tell(sender, MSG.getString("Outdated.Error", "unable to grab online version"));
				break;
			}
			if (Utils.outdated(plugin.getDescription().getVersion(), online)) {
				MSG.tell(sender, MSG.getString("Outdated.InGame", "FreakyEnchants is outdated %ver%/%oVer%")
						.replace("%ver%", plugin.getDescription().getVersion()).replace("%oVer%", online));
			} else {
				MSG.tell(sender, MSG.getString("Updated.InGame", "FreakyEnchants is up to date %ver%/%oVer%")
						.replace("%ver%", plugin.getDescription().getVersion()).replace("%oVer%", online));
			}
			break;
		case "changelog":
			if (!sender.hasPermission("freakyenchants.command.changelog")) {
				MSG.noPerm(sender);
				return true;
			}
			plugin.getChangelog().forEach((line) -> {
				MSG.tell(sender, line);
			});
			break;
		case "setcurrency":
			if (args.length < 2) {
				MSG.sendHelp(sender, "default");
				return true;
			}
			if (!args[1].matches("(?i)(token|vault|xp)")) {
				MSG.tell(sender, MSG.getString("Currency.Unknown", "unknown currency type"));
				return true;
			}
			plugin.config.set("Economy.Type", args[1].toUpperCase());
			plugin.saveConfig();
			MSG.tell(sender, MSG.getString("Currency.Set", "set currency type to %type%").replace("%type%",
					MSG.camelCase(args[1])));
			break;
		case "reload":
			refreshFiles();
			MSG.tell(sender, MSG.getString("Reloaded", "There was an error reloading FreakyEnchant's files"));
			break;
		case "reset":
			for (String res : new String[] { "config", "guis", "lang", "costs" })
				plugin.saveResource(res + ".yml", true);
			refreshFiles();

			MSG.tell(sender, "&aFreakyEnchants files successfully reset.");
			break;
		}

		return true;
	}

	private void refreshFiles() {
		plugin.configYml = new File(plugin.getDataFolder(), "config.yml");
		plugin.config = YamlConfiguration.loadConfiguration(plugin.configYml);

		plugin.guiYml = new File(plugin.getDataFolder(), "guis.yml");
		plugin.gui = YamlConfiguration.loadConfiguration(plugin.guiYml);

		plugin.enchantCostsYml = new File(plugin.getDataFolder(), "costs.yml");
		plugin.enchantCosts = YamlConfiguration.loadConfiguration(plugin.enchantCostsYml);

		plugin.langYml = new File(plugin.getDataFolder(), "lang.yml");
		plugin.lang = YamlConfiguration.loadConfiguration(plugin.langYml);
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<>();
		if (args.length <= 1) {
			for (String res : new String[] { "help", "version", "changelog", "setcurrency", "reload", "reset" }) {
				if (sender.hasPermission("freakyenchants.token." + res)
						&& res.toLowerCase().startsWith(args[0].toLowerCase())) {
					result.add(res);
				}
			}
		}
		if (args.length > 1 && args[0].equalsIgnoreCase("setcurrency")
				&& sender.hasPermission("freakyenchants.command.setcurrency")) {
			for (String res : new String[] { "TOKEN", "XP", "VAULT" }) {
				if (res.toLowerCase().startsWith(args[1].toLowerCase()))
					result.add(res);
			}
		}
		return result;
	}
}
