package org.mswsplex.enchants.checkers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Utils;

public class SeveredCheck implements Listener {

	private FreakyEnchants plugin;

	public SeveredCheck(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(ignoreCancelled = true)
	public void entityDeath(EntityDeathEvent event) {
		LivingEntity ent = event.getEntity();
		if (!Utils.allowEnchant(ent.getWorld(), "severed"))
			return;
		if (!(ent instanceof Player) || ent.getKiller() == null)
			return;
		Player player = (Player) ent, killer = ent.getKiller();
		ItemStack hand = killer.getItemInHand();
		if (hand == null || hand.getType() == Material.AIR)
			return;
		if (!hand.containsEnchantment(plugin.getEnchantmentManager().enchants.get("severed")))
			return;
		if (!plugin.getEnchantmentManager().checkProbability("severed",
				hand.getEnchantmentLevel(plugin.getEnchantmentManager().enchants.get("severed"))))
			return;
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) skull.getItemMeta();
		meta.setOwner(player.getName());
		skull.setItemMeta(meta);
		event.getDrops().add(skull);
		MSG.sendStatusMessage(killer, plugin.config.getString("Severed.SuccessMessage"));
	}

}
