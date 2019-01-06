package org.mswsplex.enchants.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.mswsplex.enchants.managers.PlayerManager;
import org.mswsplex.enchants.msws.CustomEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Utils;

public class TokenCommand implements CommandExecutor, TabCompleter {

	private CustomEnchants plugin;

	public TokenCommand(CustomEnchants plugin) {
		this.plugin = plugin;
		PluginCommand command = this.plugin.getCommand("token");
		command.setExecutor(this);
		command.setPermission("customenchants.token");
		command.setPermissionMessage(MSG.color(MSG.getString("NoPermission", "No permission")));
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			MSG.sendHelp(sender, 0, "default");
		}
		Player target = null;
		double amo;
		switch (args[0].toLowerCase()) {
		case "help":
			if (args.length == 1) {
				try {
					MSG.sendHelp(sender, Integer.parseInt(args[0]), "default");
				} catch (Exception e) {
					MSG.sendHelp(sender, 0, args[0]);
				}
			} else {
				try {
					MSG.sendHelp(sender, Integer.parseInt(args[1]), args[0]);
				} catch (Exception e) {
					MSG.sendHelp(sender, 0, args[0]);
				}
			}
			break;
		case "give":
			if (args.length < 3) {
				MSG.tell(sender, "");
				return true;
			}
			target = Bukkit.getPlayer(args[1]);
			if (target == null) {
				MSG.tell(sender, "Unknown Player");
				return true;
			}
			amo = Double.parseDouble(args[2]);
			PlayerManager.setInfo(target, "tokens", PlayerManager.getDouble(target, "tokens") + amo);
			break;
		case "set":
			if (args.length < 3) {
				MSG.tell(sender, "");
				return true;
			}
			target = Bukkit.getPlayer(args[2]);
			if (target == null) {
				MSG.tell(sender, "Unknown Player");
				return true;
			}
			amo = Double.parseDouble(args[1]);
			PlayerManager.setInfo(target, "tokens", amo);
			break;
		case "shop":
			if (sender instanceof Player) {
				PlayerManager.setInfo((Player) sender, "openInventory", "MainMenu");
				((Player) sender).openInventory(Utils.getGui((Player) sender, "MainMenu", 0));
			}
			break;
		case "amount":
			if (sender instanceof Player)
				target = (Player) sender;
			if (args.length > 1)
				target = Bukkit.getPlayer(args[1]);
			if (target == null) {
				MSG.tell(sender, "");
				return true;
			}
			MSG.tell(sender, MSG.getString("Token.Amount", "%player% has %amo%").replace("%player%", target.getName())
					.replace("%amo%", PlayerManager.getDouble(target, "tokens") + ""));
			break;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<>();
		if (args.length <= 1) {
			for (String res : new String[] { "help", "give", "set", "shop" }) {
				if (res.toLowerCase().startsWith(args[0].toLowerCase())) {
					result.add(res);
				}
			}
		}
		return result;
	}

}
