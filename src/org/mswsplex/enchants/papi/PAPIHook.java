package org.mswsplex.enchants.papi;

import org.bukkit.OfflinePlayer;
import org.mswsplex.enchants.managers.PlayerManager;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PAPIHook extends PlaceholderExpansion {
	@Override
	public String onRequest(OfflinePlayer player, String args) {
		if (player == null)
			return "";
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
		}
		return null;
	}

	public void hook() {
	}

	@Override
	public String getAuthor() {
		return "MSWS";
	}

	@Override
	public String getIdentifier() {
		return "customenchants";
	}

	@Override
	public String getVersion() {
		return "TESTVERSION";
	}
}
