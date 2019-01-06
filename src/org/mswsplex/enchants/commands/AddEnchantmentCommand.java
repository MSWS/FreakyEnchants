package org.mswsplex.enchants.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

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
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			return true;
		}
		Player player = (Player) sender;
		int level = 1;
		if (args.length > 1)
			level = Integer.parseInt(args[1]);
		if (args[0].equalsIgnoreCase("all")) {
			if (!sender.hasPermission("customenchant.addenchant.all")) {
				MSG.tell(sender, MSG.getString("NoPermission", "No Permission"));
				return true;
			}
			for (String enchant : plugin.getEnchantmentManager().enchants.keySet()) {
				Enchantment ench = plugin.getEnchantmentManager().enchants.get(enchant);
				plugin.getEnchantmentManager().addEnchant(player.getItemInHand(), level,
						plugin.getEnchantmentManager().enchants.get(enchant));
				MSG.tell(player, MSG.getString("EnchantAdded", "added %enchant% %level%")
						.replace("%enchant%", ench.getName()).replace("%level%", MSG.toRoman(level)));
			}
		} else if (!plugin.getEnchantmentManager().enchants.containsKey(args[0].toLowerCase())) {
			MSG.tell(sender, "Unknown enchantment");
			return true;
		} else {
			if (!sender.hasPermission("customenchant.addenchant." + args[0])) {
				MSG.tell(sender, MSG.getString("NoPermission", "No Permission"));
				return true;
			}
			Enchantment ench = plugin.getEnchantmentManager().enchants.get(args[0].toLowerCase());
			plugin.getEnchantmentManager().addEnchant(player.getItemInHand(), level, ench);
			MSG.tell(player, MSG.getString("EnchantAdded", "added %enchant% %level%")
					.replace("%enchant%", ench.getName()).replace("%level%", MSG.toRoman(level)));
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
