package org.mswsplex.enchants.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.mswsplex.enchants.managers.PlayerManager;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.MSG;

public class GiveEnchantCommand implements CommandExecutor, TabCompleter {

	private FreakyEnchants plugin;

	public GiveEnchantCommand(FreakyEnchants plugin) {
		this.plugin = plugin;
		PluginCommand cmd = this.plugin.getCommand("giveenchant");
		cmd.setExecutor(this);
		cmd.setTabCompleter(this);
		cmd.setPermission("freakyenchants.giveenchant");
		cmd.setPermissionMessage(MSG.color(MSG.getString("NoPermission", "No permission")));
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length <= 1) {
			MSG.sendHelp(sender, "giveenchant");
			return true;
		}
		OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

		int level = 1;
		if (args.length > 2)
			level = Integer.parseInt(args[2]);

		if (!plugin.getEnchManager().enchants.containsKey(args[1].toLowerCase())) {
			MSG.tell(sender, MSG.getString("Enchant.Unknown", "unknown enchantment"));
			return true;
		}
		if (!sender.hasPermission("freakyenchants.giveenchant." + args[0])) {
			MSG.tell(sender, MSG.getString("NoPermission", "No Permission"));
			return true;
		}
		Enchantment ench = plugin.getEnchant(args[1].toLowerCase());
		if (!sender.hasPermission("freakyenchants.addenchant.bypasslimit"))
			level = Math.min(ench.getMaxLevel(), level);

		List<String> tokens = PlayerManager.getStringList(target, "enchantmentTokens");
		if (tokens == null)
			tokens = new ArrayList<String>();

		tokens.add(args[1].toLowerCase() + " " + level + " " + plugin.config.getString("DefaultSpawnedItemsType"));
		PlayerManager.setInfo(target, "enchantmentTokens", tokens);
		MSG.tell(sender,
				MSG.getString("Enchant.Give.Sender", "gave %target% %ench% %level%")
						.replace("%target%", target.getName()).replace("%ench%", ench.getName())
						.replace("%level%", MSG.toRoman(level)));
		if (target.isOnline())
			MSG.tell((Player) target,
					MSG.getString("Enchant.Give.Receiver", "%sender% gave you a %ench% %level%")
							.replace("%sender%", sender.getName()).replace("%ench%", ench.getName())
							.replace("%level%", MSG.toRoman(level)));
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<>();
		if (args.length <= 1) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.getName().toLowerCase().startsWith(args[0].toLowerCase()) && !isVanished(p)) {
					result.add(p.getName());
				}
			}
		} else if (args.length <= 2) {
			for (Entry<String, Enchantment> res : plugin.getEnchManager().enchants.entrySet()) {
				if (res.getKey().toLowerCase().startsWith(args[1].toLowerCase())) {
					result.add(res.getValue().getName().replace(" ", ""));
				}
			}
		}

		return result;
	}

	private boolean isVanished(Player player) {
		for (MetadataValue meta : player.getMetadata("vanished")) {
			if (meta.asBoolean())
				return true;
		}
		return false;
	}

}
