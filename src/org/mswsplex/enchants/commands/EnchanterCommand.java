package org.mswsplex.enchants.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.mswsplex.enchants.managers.PlayerManager;
import org.mswsplex.enchants.msws.CustomEnchants;
import org.mswsplex.enchants.utils.Utils;

public class EnchanterCommand implements CommandExecutor {
	@SuppressWarnings("unused")
	private CustomEnchants plugin;

	public EnchanterCommand(CustomEnchants plugin) {
		this.plugin = plugin;
		PluginCommand cmd = plugin.getCommand("enchanter");
		cmd.setExecutor(this);
		cmd.setPermission("customenchants.command");
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		Player player = (Player) sender;
		PlayerManager.setInfo(player, "openInventory", "MainMenu");
		player.openInventory(Utils.getGui(player, "MainMenu", 0));
		return true;
	}
}
