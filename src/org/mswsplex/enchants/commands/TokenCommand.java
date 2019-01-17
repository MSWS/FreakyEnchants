package org.mswsplex.enchants.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.mswsplex.enchants.managers.PlayerManager;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Utils;

public class TokenCommand implements CommandExecutor, TabCompleter {

	private FreakyEnchants plugin;

	public TokenCommand(FreakyEnchants plugin) {
		this.plugin = plugin;
		PluginCommand cmd = this.plugin.getCommand("token");
		cmd.setExecutor(this);
		cmd.setPermission("freakyenchants.token");
		cmd.setPermissionMessage(MSG.color(MSG.getString("NoPermission", "No permission")));
	}

	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			MSG.sendHelp(sender, "token");
			return true;
		}
		OfflinePlayer target = null;
		double amo;
		switch (args[0].toLowerCase()) {
		case "help":
			MSG.sendHelp(sender, "token");
		case "give":
			if (!sender.hasPermission("freakyenchants.token.give")) {
				MSG.tell(sender, MSG.getString("NoPermission", "No Permission"));
				return true;
			}
			if (args.length < 3) {
				MSG.tell(sender, "");
				return true;
			}
			target = Bukkit.getOfflinePlayer(args[1]);
			if (target == null) {
				MSG.tell(sender, "Unknown Player");
				return true;
			}
			amo = Double.parseDouble(args[2]);
			PlayerManager.setInfo(target, "tokens", PlayerManager.getDouble(target, "tokens") + amo);
			MSG.tell(sender,
					MSG.getString("Token.Give", "gave %target% %amo% token%s%, they now have %total%")
							.replace("%target%", sender.getName()).replace("%amo%", (int) Math.round(amo) + "")
							.replace("%total%", (int) Math.round(PlayerManager.getDouble(target, "tokens")) + "")
							.replace("%s%", PlayerManager.getDouble(target, "tokens") == 1 ? "" : "s"));
			break;
		case "set":
			if (!sender.hasPermission("freakyenchants.token.set")) {
				MSG.tell(sender, MSG.getString("NoPermission", "No Permission"));
				return true;
			}
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
			PlayerManager.setBalance(target, amo);
			MSG.tell(sender,
					MSG.getString("Token.Set", "set %target% %amo% to have %total%")
							.replace("%target%", sender.getName()).replace("%amo%", amo + "")
							.replace("%total%", (int) Math.round(PlayerManager.getDouble(target, "tokens")) + "")
							.replace("%s%", PlayerManager.getDouble(target, "tokens") == 1 ? "" : "s"));
			break;
		case "shop":
			if (sender instanceof Player) {
				PlayerManager.setInfo((Player) sender, "openInventory", "MainMenu");
				((Player) sender).openInventory(Utils.getGui((Player) sender, "MainMenu", 0));
				((Player) sender).playSound(((Player) sender).getLocation(), Sound.ANVIL_USE, 1, 2);
			}
			break;
		case "get":
			if (!sender.hasPermission("freakyenchants.token.get")) {
				MSG.tell(sender, MSG.getString("NoPermission", "No Permission"));
				return true;
			}
			if (sender instanceof Player)
				target = (Player) sender;
			if (args.length > 1 && sender.hasPermission("freakyenchants.token.get.others"))
				target = Bukkit.getPlayer(args[1]);
			if (target == null) {
				MSG.tell(sender, "Unknown Player");
				return true;
			}
			MSG.tell(sender,
					MSG.getString("Token.Get", "%target% has %total%").replace("%target%", target.getName())
							.replace("%total%", (int) Math.round(PlayerManager.getBalance(target)) + "")
							.replace("%s%", PlayerManager.getBalance(target) == 1 ? "" : "s"));
			break;
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<>();
		if (args.length <= 1) {
			for (String res : new String[] { "give", "get", "set", "shop" }) {
				if (sender.hasPermission("freakyenchants.token." + res)
						&& res.toLowerCase().startsWith(args[0].toLowerCase())) {
					result.add(res);
				}
			}
		}
		for (Player t : Bukkit.getOnlinePlayers()) {
			if (!isVanished(t) && t.getName().toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
				result.add(t.getName());
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
