package org.mswsplex.enchants.commands;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
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
			if (!sender.hasPermission("freakyenchants.command.setcurrency")) {
				MSG.noPerm(sender);
				return true;
			}
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
			if (!sender.hasPermission("freakyenchants.command.reload")) {
				MSG.noPerm(sender);
				return true;
			}
			refreshFiles();
			MSG.tell(sender, MSG.getString("Reloaded", "There was an error reloading FreakyEnchant's files"));
			break;
		case "reset":
			if (!sender.hasPermission("freakyenchants.command.reset")) {
				MSG.noPerm(sender);
				return true;
			}
			for (String res : new String[] { "config", "guis", "lang", "costs" })
				plugin.saveResource(res + ".yml", true);
			refreshFiles();

			MSG.tell(sender, "&9&lFreaky&1&lEnchants&b files successfully reset.");
			break;
		case "testapi":
			if (!sender.hasPermission("freakyenchants.command.testapi")) {
				MSG.noPerm(sender);
				return true;
			}
			final int id = 64154;
			final String url = "https://api.spiget.org/v2/resources/" + id;
			ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta meta = (BookMeta) book.getItemMeta();
			List<String> pages = new ArrayList<String>();
			String query = args.length > 1 && !NumberUtils.isNumber(args[1]) ? args[1] : "";
			try {
				URL u = new URL(url);
				String s = IOUtils.toString(u);

				JSONObject obj = (JSONObject) JSONValue.parseWithException(s);

				if (sender instanceof Player && query.isEmpty()) {
					pages.add(MSG.color("&9&lFreaky&1&lEnchants\n\n&2&lOnline Version: &a" + plugin.getOnlineVer()
							+ "\n&4&lCurrent Version: &c" + plugin.getDescription().getVersion() + "\n&6&lDownloads: &8"
							+ obj.get("downloads") + "\n&5&lEnchantments: &d"
							+ plugin.getEnchManager().enchants.size()));
					String log = "";
					for (String line : plugin.getChangelog())
						log += line + "\n";
					pages.add(MSG.color("&4&lChangelog\n&8" + log));
				}

				for (Object set : obj.keySet()) {
					if (!query.toLowerCase().contains(set.toString().toLowerCase()) && !query.isEmpty())
						continue;
					Object val = obj.get(set);
					String key = MSG.parseJSON(val);
					if (key.length() > 300) {
						MSG.log("Skipping long key " + set + " (size: " + key.length() + ")");
						continue;
					}
					if (!(sender instanceof Player) || !query.isEmpty())
						MSG.tell(sender, set + ": " + key);
					else
						pages.add(MSG.color("&4&l" + MSG.camelCase(set.toString()) + "\n&9" + key));
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			meta.setPages(pages);
			meta.setTitle(MSG.color("&9&lFreaky&1&lEnchants"));
			meta.setAuthor("MSWS");
			book.setItemMeta(meta);
			if (sender instanceof Player && !pages.isEmpty())
				((Player) sender).setItemInHand(book);
			break;
		}
		return true;
	}

	private void refreshFiles() {
		plugin.configYml = new File(plugin.getDataFolder(), "config.yml");
		plugin.guiYml = new File(plugin.getDataFolder(), "guis.yml");
		plugin.enchantCostsYml = new File(plugin.getDataFolder(), "costs.yml");
		plugin.langYml = new File(plugin.getDataFolder(), "lang.yml");

		plugin.config = YamlConfiguration.loadConfiguration(plugin.configYml);
		plugin.gui = YamlConfiguration.loadConfiguration(plugin.guiYml);
		plugin.enchantCosts = YamlConfiguration.loadConfiguration(plugin.enchantCostsYml);
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
