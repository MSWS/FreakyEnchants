package org.mswsplex.enchants.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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
import org.mswsplex.enchants.utils.Sounds;
import org.mswsplex.enchants.utils.Utils;

public class ShopListener implements Listener {
	private FreakyEnchants plugin;

	public ShopListener(FreakyEnchants plugin) {
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
		if (inv == null || inv.equals("RedeemMenu"))
			return;
		event.setCancelled(true);
		int slot = event.getSlot();
		String id = "";
		ConfigurationSection section = plugin.gui.getConfigurationSection(inv);
		for (String entry : section.getKeys(false)) {
			if (section.contains(entry + ".Slot") && section.getInt(entry + ".Slot") == slot) {
				id = entry;
				break;
			}
		}

		shop: if (cp.getTempData("enchantToApply") != null) {
			Enchantment apply = plugin.getEnchManager().enchants.get(cp.getTempString("enchantToApply"));
			if (!event.getClickedInventory().getName().equals("container.inventory"))
				break shop;
			if (!apply.canEnchantItem(item)) {
				MSG.tell(player, MSG.getString("Enchant.Invalid", "unable to add %enchant% to item")
						.replace("%enchant%", apply.getName()));
				break shop;
			}
			plugin.getEnchManager().addEnchant(item, cp.getTempInteger("amplifier"), apply);
			Utils.playSound(plugin.config, "Sounds.EnchantmentAdded", player);
			MSG.tell(player,
					MSG.getString("Enchant.Added", "Added %enchat% %level%").replace("%enchant%", apply.getName())
							.replace("%level%", MSG.toRoman(cp.getTempInteger("amplifier"))));
			cp.removeTempData("enchantToApply");
			cp.removeTempData("amplifier");
		}
		if (id.isEmpty())
			return;
		if (section.contains(id + ".Commands")) {
			section.getStringList(id + ".Commands").forEach((command) -> {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
						command.replace("%player%", player.getName()).replace("%uuid%", player.getUniqueId() + "")
								.replace("%world%", player.getWorld().getName())
								.replace("%balance%", cp.getBalance() + ""));
			});
		}
		if (section.contains(id + ".NextInventory")) {
			player.openInventory(Utils.getGui(player, section.getString(id + ".NextInventory"), 0));
			cp.setTempData("openInventory", section.getString(id + ".NextInventory"));
			Utils.playSound(plugin.config, "Sounds.NextInventory", player);
		}
		if (plugin.enchantCosts.contains(id)) {
			Enchantment ench = plugin.getEnchant(id);
			double cost = plugin.enchantCosts.getInt(id + "." + item.getAmount());
			if (event.getClick() == ClickType.RIGHT && ench.getMaxLevel() != 1) {
				cp.setTempData(id,
						cp.getTempData(id) == null ? 2
								: Math.max(
										((cp.getTempInteger(id) + 1) % (ench.getMaxLevel() + 1)) + ench.getStartLevel(),
										1 + ench.getStartLevel()));
				cp.setTempData("ignore", true);
				player.playSound(player.getLocation(),
						Sounds.valueOf(plugin.config.getString("Sounds.IterateLevels.Name")).bukkitSound(), 2,
						(((float) (cp.getTempData(id) == null ? 2
								: Math.max(((cp.getTempInteger(id)) % (ench.getMaxLevel() + 1)) + ench.getStartLevel(),
										1 + ench.getStartLevel()))
								/ ench.getMaxLevel()) * 2f));
				player.openInventory(Utils.getGui(player, inv, 0));
				cp.setTempData("openInventory", inv);
				return;
			}
			if (event.getClick() == ClickType.LEFT) {
				if (cp.getBalance() < cost) {
					MSG.tell(player,
							MSG.getString("Token.Insufficient",
									"&cYou have insufficient funds. (&4%total% &cof &a%cost%%c).")
									.replace("%total%", cp.getBalance() + "").replace("%cost%", cost + ""));
					Utils.playSound(plugin.config, "Sounds.InsufficientFunds", player);
					return;
				}

				Utils.playSound(plugin.config, "Sounds.PurchasedEnchantment", player);
				if (cp.getTempData("enchantToApply") != null) {
					List<String> tokens = (List<String>) cp.getTempData("enchantmentTokens");
					if (tokens == null)
						tokens = new ArrayList<>();
					tokens.add(cp.getTempString("enchantToApply") + " " + cp.getTempInteger("amplifier") + " "
							+ cp.getTempString("enchantItem"));
					cp.setTempData("enchantmentTokens", tokens);
					MSG.tell(player, MSG.getString("Enchant.Unused",
							"enchantment added to your tokens, type /redeem to redeem purchased enchantments"));
					plugin.saveData();
				}

				MSG.tell(player, MSG
						.getString("Enchant.Click",
								"click item in inventory you want to enchant with %enchant% %level%")
						.replace("%enchant%", ench.getName()).replace("%level%", MSG.toRoman(item.getAmount())));
				cp.setTempData("enchantToApply", id);
				cp.setTempData("amplifier", item.getAmount());
				cp.setBalance(cp.getBalance() - cost);
				cp.setTempData("enchantItem", item.getType() + "");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		CPlayer cp = plugin.getCPlayer(player);
		String inv = cp.getTempString("openInventory");
		if (inv == null || inv.equals("RedeemMenu"))
			return;
		cp.removeTempData("openInventory");
		if (inv.equals("MainMenu")) {
			Utils.playSound(plugin.config, "Sounds.CloseEnchantmentInventory", player);
			return;
		}

		if (cp.getTempData("enchantToApply") != null) {
			List<String> tokens = (List<String>) cp.getSaveData("enchantmentTokens");
			if (tokens == null)
				tokens = new ArrayList<>();
			tokens.add(cp.getTempString("enchantToApply") + " " + cp.getTempInteger("amplifier") + " "
					+ cp.getTempString("enchantItem"));
			cp.setSaveData("enchantmentTokens", tokens);
			MSG.tell(player, MSG.getString("Enchant.Unused",
					"enchantment added to your tokens, type /redeem to redeem purchased enchantments"));
			cp.saveData();
		}

		cp.removeTempData("enchantToApply");
		cp.removeTempData("amplifier");
		cp.removeTempData("cost");
		if (cp.getTempData("ignore") != null) {
			cp.removeTempData("ignore");
			return;
		}
		for (String r : plugin.getEnchManager().enchants.keySet())
			cp.removeTempData(r);

		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			Utils.playSound(plugin.config, "Sounds.GoToMain", player);
			player.openInventory(Utils.getGui(player, "MainMenu", 0));
			cp.setTempData("openInventory", "MainMenu");
		}, 1);
	}
}
