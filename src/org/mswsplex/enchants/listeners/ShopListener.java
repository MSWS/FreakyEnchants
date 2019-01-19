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
import org.mswsplex.enchants.managers.PlayerManager;
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
		ItemStack item = event.getCurrentItem();
		if (item == null || item.getType() == Material.AIR)
			return;
		String inv = PlayerManager.getString(player, "openInventory");
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

		shop: if (PlayerManager.getInfo(player, "enchantToApply") != null) {
			Enchantment apply = plugin.getEnchManager().enchants.get(PlayerManager.getString(player, "enchantToApply"));
			if (!event.getClickedInventory().getName().equals("container.inventory"))
				break shop;
			if (!apply.canEnchantItem(item)) {
				MSG.tell(player, MSG.getString("Enchant.Invalid", "unable to add %enchant% to item")
						.replace("%enchant%", apply.getName()));
				break shop;
			}
			plugin.getEnchManager().addEnchant(item, PlayerManager.getDouble(player, "amplifier").intValue(), apply);
			Utils.playSound(plugin.config, "Sounds.EnchantmentAdded", player);
			MSG.tell(player,
					MSG.getString("Enchant.Added", "Added %enchat% %level%").replace("%enchant%", apply.getName())
							.replace("%level%", MSG.toRoman(PlayerManager.getDouble(player, "amplifier").intValue())));
			PlayerManager.removeInfo(player, "enchantToApply");
			PlayerManager.removeInfo(player, "amplifier");
		}
		if (id.isEmpty())
			return;
		if (section.contains(id + ".NextInventory")) {
			player.openInventory(Utils.getGui(player, section.getString(id + ".NextInventory"), 0));
			PlayerManager.setInfo(player, "openInventory", section.getString(id + ".NextInventory"));
			Utils.playSound(plugin.config, "Sounds.NextInventory", player);
		}
		if (plugin.enchantCosts.contains(id)) {
			Enchantment ench = plugin.getEnchant(id);
			double cost = plugin.enchantCosts.getInt(id + "." + item.getAmount());
			if (event.getClick() == ClickType.RIGHT && ench.getMaxLevel() != 1) {
				PlayerManager.setInfo(player, id,
						PlayerManager.getInfo(player, id) == null ? 2
								: Math.max(((PlayerManager.getDouble(player, id) + 1) % (ench.getMaxLevel() + 1))
										+ ench.getStartLevel(), 1 + ench.getStartLevel()));
				PlayerManager.setInfo(player, "ignore", true);
				player.playSound(player.getLocation(),
						Sounds.valueOf(plugin.config.getString("Sounds.IterateLevels.Name")).bukkitSound(), 2,
						(((float) (PlayerManager.getInfo(player, id) == null ? 2
								: Math.max(((PlayerManager.getDouble(player, id)) % (ench.getMaxLevel() + 1))
										+ ench.getStartLevel(), 1 + ench.getStartLevel()))
								/ ench.getMaxLevel()) * 2f));
				player.openInventory(Utils.getGui(player, inv, 0));
				PlayerManager.setInfo(player, "openInventory", inv);
				return;
			}
			if (event.getClick() == ClickType.LEFT) {
				if (PlayerManager.getBalance(player) < cost) {
					MSG.tell(player, MSG
							.getString("Token.Insufficient",
									"&cYou have insufficient funds. (&4%total% &cof &a%cost%%c).")
							.replace("%total%", PlayerManager.getBalance(player) + "").replace("%cost%", cost + ""));
					Utils.playSound(plugin.config, "Sounds.InsufficientFunds", player);
					return;
				}

				Utils.playSound(plugin.config, "Sounds.PurchasedEnchantment", player);
				if (PlayerManager.getInfo(player, "enchantToApply") != null) {
					List<String> tokens = (List<String>) PlayerManager.getInfo(player, "enchantmentTokens");
					if (tokens == null)
						tokens = new ArrayList<>();
					tokens.add(PlayerManager.getString(player, "enchantToApply") + " "
							+ PlayerManager.getDouble(player, "amplifier").intValue() + " "
							+ PlayerManager.getString(player, "enchantItem"));
					PlayerManager.setInfo(player, "enchantmentTokens", tokens);
					MSG.tell(player, MSG.getString("Enchant.Unused",
							"enchantment added to your tokens, type /redeem to redeem purchased enchantments"));
					plugin.saveData();
				}

				MSG.tell(player, MSG
						.getString("Enchant.Click",
								"click item in inventory you want to enchant with %enchant% %level%")
						.replace("%enchant%", ench.getName()).replace("%level%", MSG.toRoman(item.getAmount())));
				PlayerManager.setInfo(player, "enchantToApply", id);
				PlayerManager.setInfo(player, "amplifier", item.getAmount());
				PlayerManager.setBalance(player, PlayerManager.getBalance(player) - cost);
				PlayerManager.setInfo(player, "enchantItem", item.getType() + "");
			}
		}
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		String inv = PlayerManager.getString(player, "openInventory");
		if (inv == null || inv.equals("RedeemMenu"))
			return;
		PlayerManager.removeInfo(player, "openInventory");
		if (inv.equals("MainMenu")) {
			Utils.playSound(plugin.config, "Sounds.CloseEnchantmentInventory", player);
			return;
		}

		if (PlayerManager.getInfo(player, "enchantToApply") != null) {
			List<String> tokens = (List<String>) PlayerManager.getInfo(player, "enchantmentTokens");
			if (tokens == null)
				tokens = new ArrayList<>();
			tokens.add(PlayerManager.getString(player, "enchantToApply") + " "
					+ PlayerManager.getDouble(player, "amplifier").intValue() + " "
					+ PlayerManager.getString(player, "enchantItem"));
			PlayerManager.setInfo(player, "enchantmentTokens", tokens);
			MSG.tell(player, MSG.getString("Enchant.Unused",
					"enchantment added to your tokens, type /redeem to redeem purchased enchantments"));
			plugin.saveData();
		}

		PlayerManager.removeInfo(player, "enchantToApply");
		PlayerManager.removeInfo(player, "amplifier");
		PlayerManager.removeInfo(player, "cost");
		if (PlayerManager.getInfo(player, "ignore") != null) {
			PlayerManager.removeInfo(player, "ignore");
			return;
		}
		for (String r : plugin.getEnchManager().enchants.keySet())
			PlayerManager.removeInfo(player, r);

		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			Utils.playSound(plugin.config, "Sounds.GoToMain", player);
			player.openInventory(Utils.getGui(player, "MainMenu", 0));
			PlayerManager.setInfo(player, "openInventory", "MainMenu");
		}, 1);
	}
}
