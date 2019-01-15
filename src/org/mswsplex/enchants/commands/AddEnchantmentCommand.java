package org.mswsplex.enchants.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.mswsplex.enchants.msws.CustomEnchants;
import org.mswsplex.enchants.utils.MSG;

public class AddEnchantmentCommand implements CommandExecutor, TabCompleter {
	private CustomEnchants plugin;

	public AddEnchantmentCommand(CustomEnchants plugin) {
		this.plugin = plugin;
		PluginCommand cmd = plugin.getCommand("addenchant");
		cmd.setExecutor(this);
		cmd.setTabCompleter(this);
		cmd.setPermission("customenchants.addenchant");
		cmd.setPermissionMessage(MSG.color(MSG.getString("NoPermission", "No permission")));
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			MSG.sendHelp(sender, "addenchant");
			return true;
		}
		Player player = (Player) sender;
		if (player.getItemInHand() == null || player.getItemInHand().getType() == Material.AIR) {
			MSG.tell(player, MSG.getString("Enchant.Air", "hold the item in your hand"));
			return true;
		}
		int level = 1;
		if (args.length > 1)
			level = Integer.parseInt(args[1]);
		if (args[0].equalsIgnoreCase("all")) {
			if (!sender.hasPermission("customenchants.addenchant.all")) {
				MSG.tell(sender, MSG.getString("NoPermission", "No Permission"));
				return true;
			}
			for (Entry<String, Enchantment> e : plugin.getEnchantmentManager().enchants.entrySet()) {
				Enchantment ench = e.getValue();
				if (!player.hasPermission("customenchants.addenchant.bypasslimit")) {
					plugin.getEnchantmentManager().addEnchant(player.getItemInHand(),
							Math.min(level, ench.getMaxLevel()), ench);
				} else {
					plugin.getEnchantmentManager().addEnchant(player.getItemInHand(), level, ench);
				}
				MSG.tell(player,
						MSG.getString("Enchant.Added", "added %enchant% %level%").replace("%enchant%", ench.getName())
								.replace("%level%", MSG.toRoman(player.getItemInHand().getEnchantmentLevel(ench))));
			}
		} else if (!plugin.getEnchantmentManager().enchants.containsKey(args[0].toLowerCase())) {
			MSG.tell(sender, MSG.getString("Enchant.Unknown", "unknown enchantment"));
			return true;
		} else {
			if (!sender.hasPermission("customenchants.addenchant." + args[0])) {
				MSG.tell(sender, MSG.getString("NoPermission", "No Permission"));
				return true;
			}
			Enchantment ench = plugin.getEnchantmentManager().enchants.get(args[0].toLowerCase());
			if (!player.hasPermission("customenchants.addenchant.bypasslimit")) {
				plugin.getEnchantmentManager().addEnchant(player.getItemInHand(), Math.min(level, ench.getMaxLevel()),
						ench);
			} else {
				plugin.getEnchantmentManager().addEnchant(player.getItemInHand(), level, ench);
			}
			MSG.tell(player,
					MSG.getString("Enchant.Added", "added %enchant% %level%").replace("%enchant%", ench.getName())
							.replace("%level%", MSG.toRoman(player.getItemInHand().getEnchantmentLevel(ench))));
		}
		return true;
	}

	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> result = new ArrayList<>();
		if (args.length <= 1) {
			for (String res : new String[] { "all" }) {
				if (res.toLowerCase().startsWith(args[0].toLowerCase()))
					result.add(res);
			}
			for (Entry<String, Enchantment> res : plugin.getEnchantmentManager().enchants.entrySet()) {
				if (res.getKey().toLowerCase().startsWith(args[0].toLowerCase())) {
					result.add(res.getValue().getName().replace(" ", ""));
				}
			}
		}
		return result;
	}

}
