package org.mswsplex.enchants.checkers.armor;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.managers.CPlayer;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Utils;

public class DoubleJumpCheck implements Listener {
	private FreakyEnchants plugin;

	public DoubleJumpCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);

		if (player.getEquipment() == null || player.getEquipment().getBoots() == null)
			return;
		ItemStack armor = player.getEquipment().getBoots();
		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
			return;
		if (!armor.containsEnchantment(plugin.getEnchant("doublejump")) || player.getLocation().getY() % 1 != 0)
			return;
		if (!player.getLocation().clone().getBlock().getRelative(BlockFace.DOWN).getType().isSolid())
			return;

		double cool = plugin.getEnchManager().getBonusAmount("doublejump",
				armor.getEnchantmentLevel(plugin.getEnchant("doublejump")));

		if (System.currentTimeMillis() - cp.getTempDouble("doublejump") < cool)
			return;
		player.setAllowFlight(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onToggleFlight(PlayerToggleFlightEvent event) {
		if (!event.isFlying())
			return;
		Player player = event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
			return;
		if (player.getEquipment() == null || player.getEquipment().getBoots() == null)
			return;
		ItemStack armor = player.getEquipment().getBoots();
		if (!armor.containsEnchantment(plugin.getEnchant("doublejump")))
			return;
		event.setCancelled(true);
		player.setFlying(false);
		player.setAllowFlight(false);
		Utils.playSound(plugin.config, "DoubleJump.JumpSound", player.getLocation());
		cp.setTempData("doublejump", (double) System.currentTimeMillis());
		player.setVelocity(player.getVelocity()
				.add(player.getLocation().getDirection().multiply(plugin.config.getDouble("DoubleJump.Strength")))
				.setY(1 - (player.getLocation().getPitch() / 90)));
		MSG.sendTimedHotbar(player, "DoubleJump", armor.getEnchantmentLevel(plugin.getEnchant("doublejump")));
	}
}
