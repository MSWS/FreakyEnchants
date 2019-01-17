package org.mswsplex.enchants.papi;

import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.mswsplex.enchants.managers.PlayerManager;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.Utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PAPIHook extends PlaceholderExpansion {
	private FreakyEnchants plugin;

	public PAPIHook(FreakyEnchants plugin) {
		this.plugin = plugin;
	}

	@Override
	public String onRequest(OfflinePlayer player, String args) {
		if (player == null)
			return "";
		if (args.startsWith("allow_")) {
			if (!player.isOnline())
				return null;
			Player p = (Player) player;
			return Utils.allowEnchant(p.getWorld(), args.substring("allow_".length())) ? "True" : "False";
		}
		if (args.startsWith("allowworld_")) {
			World world = Bukkit.getWorld(args.substring("allowworld_".length(), args.lastIndexOf("_")));
			if (world == null)
				return null;
			return Utils.allowEnchant(world, args.substring(("allowworld_" + world.getName()).length() + 1)) ? "True"
					: "False";
		}
		if (args.startsWith("cost_")) {
			if (args.substring("cost_".length()).indexOf("_") == -1)
				return null;
			String e = args.substring("cost_".length(), args.indexOf("_", "cost_".length()));
			return plugin.enchantCosts.getDouble(e + "." + args.substring("cost_".length() + e.length() + 1)) + "";
		}
		double bal = PlayerManager.getBalance(player);
		switch (args) {
		case "balance_round":
			return Math.round(bal) + "";
		case "balance":
			return bal + "";
		case "balance_floor":
			return (int) Math.floor(bal) + "";
		case "balance_ceil":
			return (int) Math.ceil(bal) + "";
		case "enchants":
			String res = "";
			for (Entry<String, Enchantment> e : plugin.getEnchantmentManager().enchants.entrySet()) {
				res += e.getValue().getName() + ", ";
			}
			return res.substring(0, res.length() - 2);
		}
		return null;
	}

	@Override
	public String getAuthor() {
		return "MSWS";
	}

	@Override
	public String getIdentifier() {
		return "ce";
	}

	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}
}
