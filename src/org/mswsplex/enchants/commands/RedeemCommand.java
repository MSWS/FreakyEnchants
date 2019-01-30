package org.mswsplex.enchants.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.mswsplex.enchants.managers.CPlayer;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Utils;

public class RedeemCommand implements CommandExecutor {

	private FreakyEnchants plugin;

	public RedeemCommand(FreakyEnchants plugin) {
		this.plugin = plugin;
		PluginCommand cmd = plugin.getCommand("redeem");
		cmd.setExecutor(this);
		cmd.setPermission("freakyenchants.redeem");
		cmd.setPermissionMessage(MSG.color(MSG.getString("NoPermission", "No permission")));
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			MSG.tell(sender, "You must be a player");
			return true;
		}
		Player player = (Player) sender;
		CPlayer cp = plugin.getCPlayer(player);
		cp.setTempData("page", 0);
		cp.setTempData("openInventory", "RedeemMenu");
		Utils.playSound(plugin.config, "Sounds.OpenRedeemInventory", player);
		player.openInventory(Utils.getRedeemGUI(player));
		return true;
	}
}
