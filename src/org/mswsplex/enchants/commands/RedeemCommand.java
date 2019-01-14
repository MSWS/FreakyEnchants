package org.mswsplex.enchants.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.mswsplex.enchants.managers.PlayerManager;
import org.mswsplex.enchants.msws.CustomEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Utils;

public class RedeemCommand implements CommandExecutor {

	public RedeemCommand(CustomEnchants plugin) {
		PluginCommand cmd = plugin.getCommand("redeem");
		cmd.setExecutor(this);
		cmd.setPermission("customenchants.redeem");
		cmd.setPermissionMessage(MSG.color(MSG.getString("NoPermission", "No permission")));
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		PlayerManager.setInfo(player, "page", 0);
		PlayerManager.setInfo(player, "openInventory", "RedeemMenu");
		player.openInventory(Utils.getRedeemGUI(player));
		return true;
	}
}
