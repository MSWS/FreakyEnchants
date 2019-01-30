package org.mswsplex.enchants.listeners;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.mswsplex.enchants.managers.CPlayer;
import org.mswsplex.enchants.msws.FreakyEnchants;
import org.mswsplex.enchants.utils.MSG;
import org.mswsplex.enchants.utils.Utils;

public class RedeemGUIListener implements Listener {
	private FreakyEnchants plugin;

	public RedeemGUIListener(FreakyEnchants plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, this.plugin);
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		CPlayer cp = plugin.getCPlayer(player);
		ItemStack item = event.getCurrentItem();
		if (item == null || item.getType() == Material.AIR)
			return;
		String inv = cp.getTempString("openInventory");
		if (inv == null || !inv.equals("RedeemMenu"))
			return;
		event.setCancelled(true);

		shop: if (cp.getTempData("enchantToApply") != null) {
			Enchantment apply = plugin.getEnchManager().enchants.get(cp.getTempString("enchantToApply"));
			if (!event.getClickedInventory().getName().equals("container.inventory"))
				break shop;

			if (!apply.canEnchantItem(item)) {
				MSG.tell(player, MSG.getString("Enchant.Invalid", "unable to add %enchant% to item")
						.replace("%enchant%", apply.getName()));
				break shop;
			}
			plugin.getEnchManager().addEnchant(item, (int) cp.getTempDouble("amplifier"), apply);

			Utils.playSound(plugin.config, "Sounds.EnchantmentAdded", player);

			MSG.tell(player,
					MSG.getString("Enchant.Added", "Added %enchat% %level%").replace("%enchant%", apply.getName())
							.replace("%level%", MSG.toRoman(cp.getTempInteger("amplifier"))));

			List<String> tokens = (List<String>) cp.getSaveData("enchantmentTokens");
			Iterator<String> tokenIt = tokens.iterator();
			while (tokenIt.hasNext()) {
				String line = tokenIt.next();
				if (line.contains(cp.getTempString("enchantToApply") + " " + cp.getTempInteger("amplifier"))) {
					tokenIt.remove();
					break;
				}
			}
			cp.setTempData("enchantmentTokens", tokens);
			cp.removeTempData("enchantToApply");
			cp.removeTempData("amplifier");

			player.openInventory(Utils.getRedeemGUI(player));
			cp.setTempData("openInventory", "RedeemMenu");
		}

		if (event.getRawSlot() == event.getInventory().getSize() - 1 && event.getInventory().getSize() == 54) {
			cp.setTempData("page", cp.getTempInteger("page") + 1);
			player.openInventory(Utils.getRedeemGUI(player));
			cp.setTempData("openInventory", "RedeemMenu");
		}
		if (event.getRawSlot() == event.getInventory().getSize() - 9 && event.getInventory().getSize() == 54) {
			cp.setTempData("page", cp.getTempInteger("page") - 1);
			player.openInventory(Utils.getRedeemGUI(player));
			cp.setTempData("openInventory", "RedeemMenu");
		}

		if (!item.hasItemMeta() || !item.getItemMeta().hasLore()
				|| event.getClickedInventory().getName().equals("container.inventory"))
			return;

		String e = ChatColor.stripColor(item.getItemMeta().getLore().get(0));

		Enchantment ench = null;
		String name = "";
		for (int i = 0; i < e.split(" ").length - 1; i++) {
			name += e.split(" ")[i] + " ";
		}
		name = name.trim();
		String id = "";
		for (Entry<String, Enchantment> en : plugin.getEnchManager().enchants.entrySet()) {
			if (en.getValue().getName().equals(name)) {
				ench = en.getValue();
				id = en.getKey();
				break;
			}
		}

		if (ench == null)
			return;

		int level = Utils.romanToDecimal(e.split(" ")[e.split(" ").length - 1]);

		if (event.getClick() == ClickType.DROP && player.hasPermission("freakyenchants.redeem.delete")) {
			List<String> tokens = (List<String>) cp.getSaveData("enchantmentTokens");
			Iterator<String> tokenIt = tokens.iterator();
			while (tokenIt.hasNext()) {
				String line = tokenIt.next();
				if (line.contains(id + " " + level)) {
					tokenIt.remove();
					break;
				}
			}
			cp.setTempData("enchantmentTokens", tokens);
			Utils.playSound(plugin.config, "Sounds.TokenDeleted", player);
			player.openInventory(Utils.getRedeemGUI(player));
			cp.setTempData("openInventory", "RedeemMenu");
		} else {
			MSG.tell(player,
					MSG.getString("Enchant.Click", "click item in inventory you want to enchant with %enchant% %level%")
							.replace("%enchant%", ench.getName()).replace("%level%", MSG.toRoman(level)));

			Utils.playSound(plugin.config, "Sounds.SelectedEnchantment", player);
			cp.setTempData("enchantToApply", id);
			cp.setTempData("amplifier", item.getAmount());
		}
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		String inv = cp.getTempString("openInventory");
		if (inv == null || !inv.equals("RedeemMenu"))
			return;
		Utils.playSound(plugin.config, "Sounds.CloseRedeemInventory", player);
		cp.removeTempData("openInventory");
		cp.removeTempData("enchantToApply");
		cp.removeTempData("amplifier");
	}
}
