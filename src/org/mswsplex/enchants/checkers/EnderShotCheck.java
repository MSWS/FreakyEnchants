package org.mswsplex.enchants.checkers;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.mswsplex.enchants.managers.PlayerManager;
import org.mswsplex.enchants.managers.TimeManager;
import org.mswsplex.enchants.msws.CustomEnchants;
import org.mswsplex.enchants.utils.HotbarMessenger;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Utils;

public class EnderShotCheck implements Listener {

	private CustomEnchants plugin;

	public EnderShotCheck(CustomEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	private HashMap<Player, Integer> runnables = new HashMap<>();

	@EventHandler(ignoreCancelled = true)
	public void onEntityHit(ProjectileHitEvent event) {
		if (event.getEntity() == null || !event.getEntity().hasMetadata("enderArrow")
				|| event.getEntity().getShooter() == null || !(event.getEntity().getShooter() instanceof Player))
			return;
		Player player = (Player) event.getEntity().getShooter();
		if (!Utils.allowEnchant(player.getWorld(), "endershot"))
			return;
		player.teleport(event.getEntity(), TeleportCause.ENDER_PEARL);
		Utils.playSound(plugin.config, "Endershot.TeleportSound", player.getLocation());
		if (plugin.config.getBoolean("EnderShot.DeleteArrow"))
			event.getEntity().remove();
	}

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile proj = event.getEntity();
		if (proj == null || proj.getShooter() == null || !(proj.getShooter() instanceof Player))
			return;
		Player player = (Player) proj.getShooter();
		ItemStack hand = player.getItemInHand();
		if (!hand.containsEnchantment(plugin.getEnchantmentManager().enchants.get("endershot")))
			return;
		if (System.currentTimeMillis() - PlayerManager.getDouble(player, "endershot") > plugin.getEnchantmentManager()
				.getBonusAmount("endershot",
						hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("endershot")))
				|| PlayerManager.getDouble(player, "endershot") == 0) {
			proj.setMetadata("enderArrow", new FixedMetadataValue(plugin, true));
			PlayerManager.setInfo(player, "endershot", (double) System.currentTimeMillis());
			int i = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, sendTimer(player,
					hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("endershot"))), 0, 1);
			runnables.put(player, i);
		}
	}

	public BukkitRunnable sendTimer(Player player, int level) {
		return new BukkitRunnable() {
			@Override
			public void run() {
				if (player == null) {
					Bukkit.getScheduler().cancelTask(runnables.get(player));
					runnables.remove(player);
					return;
				}
				double total = plugin.getEnchantmentManager().getBonusAmount("endershot", level);
				double timeLeft = total - (System.currentTimeMillis() - PlayerManager.getDouble(player, "endershot"));

				if (timeLeft <= 0) {
					MSG.tell(player, plugin.config.getString("EnderShot.Cooldown.Chat.Message"));
					if (plugin.config.getBoolean("EnderShot.Cooldown.Actionbar.Enabled"))
						HotbarMessenger.sendHotBarMessage(player,
								MSG.color(plugin.config.getString("EnderShot.Cooldown.Actionbar.CompleteMessage")));
					Utils.playSound(plugin.config, "EnderShot.Cooldown.Sound", player);
					Bukkit.getScheduler().cancelTask(runnables.get(player));
					runnables.remove(player);
					return;
				}
				if (plugin.config.getBoolean("EnderShot.Cooldown.Actionbar.Enabled")) {
					HotbarMessenger.sendHotBarMessage(player,
							MSG.color(plugin.config.getString("EnderShot.Cooldown.Actionbar.Message")
									.replace("%time%", TimeManager.getTime(timeLeft))
									.replace("%bar%", MSG.progressBar(
											plugin.config.getString("EnderShot.Cooldown.Actionbar.Bar.ProgChar"),
											plugin.config.getString("EnderShot.Cooldown.Actionbar.Bar.LeftChar"),
											total - timeLeft, total,
											plugin.config.getInt("EnderShot.Cooldown.Actionbar.Bar.Length")))));
				}
			}
		};
	}
}
